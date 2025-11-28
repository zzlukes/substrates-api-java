import { TargetingState } from './types';
import type { SimulationState, SimulationConfig, Vector2, Entity } from './types';

export class GameLoop {
    private state: SimulationState;
    private config: SimulationConfig;
    private history: { time: number; mosquitoesTraditional: Entity[]; mosquitoesSubstrates: Entity[] }[] = [];
    private readonly MAX_HISTORY_MS = 10000; // 10 seconds history

    // State machine timers
    private traditionalTimer = 0;
    private substratesTimer = 0;

    private readonly MAX_BATTERY = 1000;
    // Power Consumption Estimates (J/ms)
    // Mic: ~1mW = 0.000001 J/ms -> 0.0001 J/ms (w/ processing)
    // LIDAR: ~10W = 0.01 J/ms
    // Galvo: ~20W = 0.02 J/ms
    private readonly COST_MIC_SEARCH = 0.0001;
    private readonly COST_LIDAR_TRACK = 0.01;
    private readonly COST_GALVO_MOVE = 0.02;
    private readonly COST_SHOT = 0.5; // per shot (0.5 J)
    private readonly HIT_THRESHOLD = 0.02; // Tightened to ~1 degree (very strict)
    private readonly SHOT_DURATION = 100; // Duration of the shot state (ms)

    private readonly SPEED_OF_SOUND = 42.875; // 343 m/s (at 1px=8mm scale)

    // Spawn timer
    private spawnTimer = 0;
    private readonly MAX_MOSQUITOES = 500; // Cap to prevent crash

    constructor(config: SimulationConfig) {
        this.config = config;
        this.state = this.getInitialState();
    }

    private getInitialState(): SimulationState {
        // Start empty, let arrival rate fill it
        return {
            time: 0,
            mosquitoesTraditional: [],
            mosquitoesSubstrates: [],
            turretTraditional: {
                position: { x: 200, y: 500 },
                z: 0,
                velocity: { x: 0, y: 0 },
                velocityZ: 0,
                angle: 0,
                wingbeatPhase: 0,
            },
            turretSubstrates: {
                position: { x: 200, y: 500 },
                z: 0,
                velocity: { x: 0, y: 0 },
                velocityZ: 0,
                angle: 0,
                wingbeatPhase: 0,
            },
            traditionalStats: {
                latency: this.config.traditionalLatency,
                hits: 0,
                misses: 0,
                state: TargetingState.SEARCHING,
                battery: this.MAX_BATTERY,
                energyUsed: 0,
                targetIndex: -1
            },
            substratesStats: {
                latency: this.config.substratesLatency,
                hits: 0,
                misses: 0,
                state: TargetingState.SEARCHING,
                battery: this.MAX_BATTERY,
                energyUsed: 0,
                targetIndex: -1
            },
        };
    }

    private createMosquito(): Entity {
        return {
            position: { x: 50 + Math.random() * 400, y: 50 + Math.random() * 300 }, // Constrain to left side (0-450)
            z: Math.random() * 500, // Random height 0-4m
            velocity: { x: 0, y: 0 },
            velocityZ: 0,
            angle: 0,
            wingbeatPhase: Math.random() * Math.PI * 2,
        };
    }

    public update(deltaTimeMs: number): SimulationState {
        const scaledDelta = deltaTimeMs * this.config.timeScale;
        this.state.time += scaledDelta;

        // 0. Spawn Logic (Arrival Rate)
        this.spawnTimer += scaledDelta;
        const spawnInterval = 1000 / (this.config.arrivalRate || 1); // ms per mosquito

        if (this.spawnTimer > spawnInterval) {
            if (this.state.mosquitoesTraditional.length < this.MAX_MOSQUITOES) {
                // Create IDENTICAL clones for A/B testing
                const baseMosquito = this.createMosquito();

                // Clone for Traditional
                this.state.mosquitoesTraditional.push({ ...baseMosquito, position: { ...baseMosquito.position }, velocity: { ...baseMosquito.velocity } });

                // Clone for Substrates
                this.state.mosquitoesSubstrates.push({ ...baseMosquito, position: { ...baseMosquito.position }, velocity: { ...baseMosquito.velocity } });
            }
            this.spawnTimer = 0;
        }

        // 1. Update Swarms
        this.state.mosquitoesTraditional.forEach(m => this.updateMosquito(m, scaledDelta));
        this.state.mosquitoesSubstrates.forEach(m => this.updateMosquito(m, scaledDelta));

        // 2. Record History (Store full swarm state for BOTH)
        const traditionalSnapshot = this.state.mosquitoesTraditional.map(m => ({ ...m, position: { ...m.position } }));
        const substratesSnapshot = this.state.mosquitoesSubstrates.map(m => ({ ...m, position: { ...m.position } }));

        this.history.push({
            time: this.state.time,
            mosquitoesTraditional: traditionalSnapshot,
            mosquitoesSubstrates: substratesSnapshot,
        });

        // Prune history
        if (this.history.length > 0 && this.state.time - this.history[0].time > this.MAX_HISTORY_MS) {
            this.history.shift();
        }

        // 3. Update Turrets
        if (this.state.traditionalStats.battery > 0) {
            this.updateTurretTraditional(scaledDelta);
        }
        if (this.state.substratesStats.battery > 0) {
            this.updateTurretSubstrates(scaledDelta);
        }

        return this.state;
    }

    private updateMosquito(mosquito: Entity, dt: number) {
        const speed = this.config.mosquitoSpeed;

        // Wingbeat Physics (600Hz = 0.6 beats per ms)
        // Phase goes 0 -> 2PI
        const WINGBEAT_FREQ = 0.6;
        mosquito.wingbeatPhase = (mosquito.wingbeatPhase || 0) + (dt * WINGBEAT_FREQ * Math.PI * 2);
        mosquito.wingbeatPhase %= (Math.PI * 2);

        // Movement is tied to wingbeat (bursts of acceleration)
        // Acceleration happens mostly during the "downstroke" (sin > 0)
        const thrust = Math.max(0, Math.sin(mosquito.wingbeatPhase));

        // Random erratic direction changes
        const ax = (Math.random() - 0.5) * 0.1 * thrust;
        const ay = (Math.random() - 0.5) * 0.1 * thrust;
        const az = (Math.random() - 0.5) * 0.1 * thrust; // Z-axis acceleration

        mosquito.velocity.x += ax * dt;
        mosquito.velocity.y += ay * dt;
        mosquito.velocityZ += az * dt;

        // Dampening
        mosquito.velocity.x *= 0.99;
        mosquito.velocity.y *= 0.99;
        mosquito.velocityZ *= 0.99;

        // Update position
        mosquito.position.x += mosquito.velocity.x * dt * speed;
        mosquito.position.y += mosquito.velocity.y * dt * speed;
        mosquito.z += mosquito.velocityZ * dt * speed;

        // Bounds checking (Hard Clamping + Bounce)
        // Logical area is 0-500 for X, 0-600 for Y
        const PADDING = 10;
        const MIN_X = PADDING;
        const MAX_X = 500 - PADDING; // 500px width
        const MIN_Y = PADDING;
        const MAX_Y = 600 - PADDING; // 600px height
        const MIN_Z = 0;
        const MAX_Z = 500; // 4m height

        if (mosquito.position.x < MIN_X) {
            mosquito.position.x = MIN_X;
            mosquito.velocity.x *= -1; // Bounce
        }
        if (mosquito.position.x > MAX_X) {
            mosquito.position.x = MAX_X;
            mosquito.velocity.x *= -1; // Bounce
        }
        if (mosquito.position.y < MIN_Y) {
            mosquito.position.y = MIN_Y;
            mosquito.velocity.y *= -1; // Bounce
        }
        if (mosquito.position.y > MAX_Y) {
            mosquito.position.y = MAX_Y;
            mosquito.velocity.y *= -1; // Bounce
        }
        if (mosquito.z < MIN_Z) {
            mosquito.z = MIN_Z;
            mosquito.velocityZ *= -1; // Bounce
        }
        if (mosquito.z > MAX_Z) {
            mosquito.z = MAX_Z;
            mosquito.velocityZ *= -1; // Bounce
        }
    }

    private updateTurretTraditional(dt: number) {
        // Energy Logic
        if (this.config.policy === 'ENGAGE') {
            const state = this.state.traditionalStats.state;
            let cost = 0;

            if (state === TargetingState.SEARCHING || state === TargetingState.HEARD) {
                cost = this.COST_MIC_SEARCH * dt;
            } else if (state === TargetingState.SPOTTED) {
                cost = this.COST_LIDAR_TRACK * dt;
            } else if (state === TargetingState.TARGETED) {
                cost = (this.COST_LIDAR_TRACK + this.COST_GALVO_MOVE) * dt;
            }
            this.state.traditionalStats.energyUsed += cost;
        }

        // State Machine: SEARCHING -> HEARD -> SPOTTED -> TARGETED -> SHOT
        // Transitions take time based on latency

        const latency = this.config.traditionalLatency;
        const currentState = this.state.traditionalStats.state;

        // Simulate processing delay accumulation
        this.traditionalTimer += dt;

        // Target Selection (Naive: Closest)
        // Traditional calculates this based on OLD data
        const targetTime = this.state.time - latency;
        const historyItem = this.getHistoryAt(targetTime);
        const historicalMosquitoes = historyItem ? historyItem.mosquitoesTraditional : [];

        // If we don't have a target, pick one
        if (this.state.traditionalStats.targetIndex === -1 && historicalMosquitoes.length > 0) {
            // Pick random or closest
            this.state.traditionalStats.targetIndex = Math.floor(Math.random() * historicalMosquitoes.length);
        }

        const targetIdx = this.state.traditionalStats.targetIndex;
        const targetMosquito = historicalMosquitoes[targetIdx];

        if (!targetMosquito) {
            this.state.traditionalStats.state = TargetingState.SEARCHING;
            return;
        }

        // Audio Delay Calculation
        const dist = Math.sqrt(
            Math.pow(targetMosquito.position.x - this.state.turretTraditional.position.x, 2) +
            Math.pow(targetMosquito.position.y - this.state.turretTraditional.position.y, 2)
        );
        const audioDelay = dist / this.SPEED_OF_SOUND;
        const totalLatency = latency + audioDelay;

        // Transition Logic
        if (currentState === TargetingState.SEARCHING) {
            // Always hear it eventually
            if (this.traditionalTimer > totalLatency) {
                this.state.traditionalStats.state = TargetingState.HEARD;
                this.traditionalTimer = 0;
            }
        } else if (currentState === TargetingState.HEARD) {
            if (this.traditionalTimer > latency) {
                this.state.traditionalStats.state = TargetingState.SPOTTED;
                this.traditionalTimer = 0;
            }
        } else if (currentState === TargetingState.SPOTTED) {
            if (this.traditionalTimer > latency) {
                this.state.traditionalStats.state = TargetingState.TARGETED;
                this.traditionalTimer = 0;
            }
        } else if (currentState === TargetingState.TARGETED) {
            // Aiming logic
            const angle = this.calculateAngle(this.state.turretTraditional.position, targetMosquito.position);
            this.state.turretTraditional.angle = angle;

            // Check alignment for shot
            // Traditional struggles to stay aligned due to lag
            if (this.traditionalTimer > latency) {
                // Check Policy
                if (this.config.policy === 'ENGAGE') {
                    // Fire attempt (likely miss due to lag)
                    this.state.traditionalStats.state = TargetingState.SHOT;

                    // Hit Calculation: Traditional misses often due to lag
                    // We check if the turret angle matches the CURRENT mosquito position
                    // Note: We must check against the LIVE mosquito in the Traditional array
                    // The targetMosquito is from history, so we need to find the corresponding live one?
                    // Actually, since we use index, we can try to find it in the live array.
                    // BUT, indices might shift if we delete.
                    // So we should probably track by ID. For now, let's assume index stability or just use the live one at that index if it exists.

                    const liveMosquito = this.state.mosquitoesTraditional[targetIdx];

                    if (liveMosquito) {
                        const currentAngleToTarget = this.calculateAngle(this.state.turretTraditional.position, liveMosquito.position);
                        const angleDiff = Math.abs(this.state.turretTraditional.angle - currentAngleToTarget);

                        // 3D Check: Must also match Z-axis
                        // We assume the turret aims at the historical Z
                        // So we check if the LIVE Z is within threshold of HISTORICAL Z
                        const zDiff = Math.abs(liveMosquito.z - targetMosquito.z);
                        const Z_THRESHOLD = 5; // 5 pixels (~4cm)

                        if (angleDiff < this.HIT_THRESHOLD && zDiff < Z_THRESHOLD) { // Hit threshold (3D)
                            this.state.traditionalStats.hits++;
                            // KILL LOGIC: Remove from Traditional Array ONLY
                            this.state.mosquitoesTraditional = this.state.mosquitoesTraditional.filter(m => m !== liveMosquito);
                            this.state.traditionalStats.targetIndex = -1;
                        } else {
                            this.state.traditionalStats.misses++;
                        }
                    } else {
                        // Target already gone
                        this.state.traditionalStats.targetIndex = -1;
                    }

                    // Deduct Shot Energy
                    // this.state.traditionalStats.battery -= this.COST_SHOT;
                    this.state.traditionalStats.energyUsed += this.COST_SHOT;
                } else {
                    // OBSERVE mode: Just track
                }
                this.traditionalTimer = 0;
            }
        } else if (currentState === TargetingState.SHOT) {
            // Reset cycle
            if (this.traditionalTimer > this.SHOT_DURATION) { // Short pulse visual
                this.state.traditionalStats.state = TargetingState.SEARCHING;
                this.state.traditionalStats.targetIndex = -1; // Pick new target
                this.traditionalTimer = 0;
            }
        }
    }

    private updateTurretSubstrates(dt: number) {
        // Energy Logic
        if (this.config.policy === 'ENGAGE') {
            const state = this.state.substratesStats.state;
            let cost = 0;

            if (state === TargetingState.SEARCHING || state === TargetingState.HEARD) {
                cost = this.COST_MIC_SEARCH * dt;
            } else if (state === TargetingState.SPOTTED) {
                cost = this.COST_LIDAR_TRACK * dt;
            } else if (state === TargetingState.TARGETED) {
                cost = (this.COST_LIDAR_TRACK + this.COST_GALVO_MOVE) * dt;
            }
            this.state.substratesStats.energyUsed += cost;
        }

        // if (this.state.substratesStats.battery <= 0) return;

        const latency = this.config.substratesLatency; // Very low (e.g. 1ms)
        const currentState = this.state.substratesStats.state;

        this.substratesTimer += dt;

        // Substrates uses HISTORICAL data based on latency, just like Traditional
        // This ensures that if latency is high, it misses too.
        const targetTime = this.state.time - latency;
        const historyItem = this.getHistoryAt(targetTime);
        const historicalMosquitoes = historyItem ? historyItem.mosquitoesSubstrates : [];

        if (this.state.substratesStats.targetIndex === -1 && historicalMosquitoes.length > 0) {
            this.state.substratesStats.targetIndex = Math.floor(Math.random() * historicalMosquitoes.length);
        }

        const targetIdx = this.state.substratesStats.targetIndex;
        const targetMosquito = historicalMosquitoes[targetIdx];

        if (!targetMosquito) {
            this.state.substratesStats.state = TargetingState.SEARCHING;
            return;
        }

        const dist = Math.sqrt(
            Math.pow(targetMosquito.position.x - this.state.turretSubstrates.position.x, 2) +
            Math.pow(targetMosquito.position.y - this.state.turretSubstrates.position.y, 2)
        );
        const audioDelay = dist / this.SPEED_OF_SOUND;

        // Substrates transitions are almost instant
        if (currentState === TargetingState.SEARCHING) {
            // Substrates hears it "as soon as sound arrives"
            if (this.substratesTimer > audioDelay) {
                this.state.substratesStats.state = TargetingState.HEARD;
                this.substratesTimer = 0;
            }
        } else if (currentState === TargetingState.HEARD) {
            if (this.substratesTimer > latency) {
                this.state.substratesStats.state = TargetingState.SPOTTED;
                this.substratesTimer = 0;
            }
        } else if (currentState === TargetingState.SPOTTED) {
            if (this.substratesTimer > latency) {
                this.state.substratesStats.state = TargetingState.TARGETED;
                this.substratesTimer = 0;
            }
        } else if (currentState === TargetingState.TARGETED) {
            // Aiming logic (Predictive / Instant)
            const angle = this.calculateAngle(this.state.turretSubstrates.position, targetMosquito.position);
            this.state.turretSubstrates.angle = angle;

            // Fire logic
            if (this.substratesTimer > latency * 2) { // Rapid fire
                if (this.config.policy === 'ENGAGE') {
                    this.state.substratesStats.state = TargetingState.SHOT;

                    // KILL LOGIC: Remove from Substrates Array ONLY
                    // We need to find the actual mosquito entity in the live array
                    const liveMosquito = this.state.mosquitoesSubstrates[targetIdx];

                    if (liveMosquito) {
                        // Check for hit accuracy based on angle
                        const currentAngleToTarget = this.calculateAngle(this.state.turretSubstrates.position, liveMosquito.position);
                        const angleDiff = Math.abs(this.state.turretSubstrates.angle - currentAngleToTarget);

                        // 3D Check: Must also match Z-axis
                        const zDiff = Math.abs(liveMosquito.z - targetMosquito.z);
                        const Z_THRESHOLD = 5; // 5 pixels (~4cm)

                        if (angleDiff < this.HIT_THRESHOLD && zDiff < Z_THRESHOLD) { // Hit threshold (3D)
                            this.state.substratesStats.hits++;
                            this.state.mosquitoesSubstrates = this.state.mosquitoesSubstrates.filter(m => m !== liveMosquito);
                            // Reset target index as the array changed
                            this.state.substratesStats.targetIndex = -1;
                        } else {
                            this.state.substratesStats.misses++;
                            // LOSE LOCK ON MISS: Revert to SEARCHING
                            // This penalizes high latency, as it forces re-acquisition
                            this.state.substratesStats.state = TargetingState.SEARCHING;
                            this.state.substratesStats.targetIndex = -1;
                        }
                    } else {
                        // Target already gone
                        this.state.substratesStats.targetIndex = -1;
                    }

                    // Deduct Shot Energy
                    // this.state.substratesStats.battery -= this.COST_SHOT;
                    this.state.substratesStats.energyUsed += this.COST_SHOT;
                } else {
                    // OBSERVE mode: Just track/count, do NOT fire
                    // We can simulate a "virtual" hit for stats if needed, but for now just stay TARGETED
                    // Maybe increment a "tracked" counter?
                }
                this.substratesTimer = 0;
            }
        } else if (currentState === TargetingState.SHOT) {
            // Reset cycle (SAME AS TRADITIONAL)
            if (this.substratesTimer > this.SHOT_DURATION) {
                this.state.substratesStats.state = TargetingState.SEARCHING;
                this.state.substratesStats.targetIndex = -1;
                this.substratesTimer = 0;
            }
        }
    }

    private getHistoryAt(time: number): { mosquitoesTraditional: Entity[]; mosquitoesSubstrates: Entity[] } | null {
        // Binary search or simple find for now (optimization later if needed)
        // Since history is sorted by time, we can find the closest frame
        if (this.history.length === 0) return null;

        // Simple linear scan from end (most likely close to recent)
        for (let i = this.history.length - 1; i >= 0; i--) {
            if (this.history[i].time <= time) {
                return this.history[i];
            }
        }
        return this.history[0];
    }

    private calculateAngle(from: Vector2, to: Vector2): number {
        return Math.atan2(to.y - from.y, to.x - from.x);
    }

    public setConfig(config: Partial<SimulationConfig>) {
        this.config = { ...this.config, ...config };

        // Sync state with config
        if (config.traditionalLatency !== undefined) {
            this.state.traditionalStats.latency = config.traditionalLatency;
        }
        if (config.substratesLatency !== undefined) {
            this.state.substratesStats.latency = config.substratesLatency;
        }
    }

    public getState(): SimulationState {
        return this.state;
    }
}

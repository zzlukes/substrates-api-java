import type { RoboticSimulationState, RoboticSimulationConfig, Egg } from './types';

export class RoboticGameLoop {
    private config: RoboticSimulationConfig;
    private state: RoboticSimulationState;
    private lastSpawnTime: number = 0;
    private eggIdCounter: number = 0;

    // Physics constants
    private readonly GRAVITY = 0.0005; // px/ms^2 (approx 9.8m/s^2 scaled)
    private readonly TERMINAL_VELOCITY = 0.5; // px/ms
    private readonly SPAWN_Y = 50;

    constructor(config: any) {
        this.config = {
            ...config,
            gripThreshold: 30, // Increased from 20
            eggMass: 1,
            safeCatchSpeed: 0.25, // Increased from 0.1 to make it possible to catch
            maxCapacity: 3
        };

        this.state = {
            time: 0,
            eggsTraditional: [],
            eggsSubstrates: [],
            handTraditional: {
                position: { x: 250, y: 500 },
                velocity: { x: 0, y: 0 },
                z: 0,
                velocityZ: 0,
                angle: 0,
                wingbeatPhase: 0,
                gripState: 'OPEN',
                gripForce: 0,
                heldEggs: [],
                mode: 'HUNTING'
            },
            handSubstrates: {
                position: { x: 750, y: 500 },
                velocity: { x: 0, y: 0 },
                z: 0,
                velocityZ: 0,
                angle: 0,
                wingbeatPhase: 0,
                gripState: 'OPEN',
                gripForce: 0,
                heldEggs: [],
                mode: 'HUNTING'
            },
            traditionalStats: { latency: 0, caught: 0, cracked: 0, dropped: 0 },
            substratesStats: { latency: 0, caught: 0, cracked: 0, dropped: 0 }
        };
    }

    public setConfig(config: any) {
        this.config = { ...this.config, ...config };
    }

    public getState(): RoboticSimulationState {
        return { ...this.state };
    }

    public update(deltaTime: number): RoboticSimulationState {
        const scaledDelta = deltaTime * this.config.timeScale;
        this.state.time += scaledDelta;

        // Spawn Eggs
        this.spawnEggs();

        // Update Physics & Logic
        this.updateSystem(scaledDelta, 'TRADITIONAL');
        this.updateSystem(scaledDelta, 'SUBSTRATES');

        return this.state;
    }

    private spawnEggs() {
        // Simple periodic spawning for now
        const interval = 1000 / this.config.arrivalRate;
        if (this.state.time - this.lastSpawnTime > interval) {
            this.spawnEggPair();
            this.lastSpawnTime = this.state.time;
        }
    }

    private spawnEggPair() {
        const id = `egg-${this.eggIdCounter++}`;
        const startX = (Math.random() * 200) - 100; // Random offset

        const egg: Egg = {
            id,
            position: { x: startX, y: this.SPAWN_Y },
            velocity: { x: 0, y: 0 },
            z: 0,
            velocityZ: 0,
            angle: 0,
            wingbeatPhase: 0,
            state: 'FALLING',
            radius: 15
        };

        // Deep copy for independent simulations
        this.state.eggsTraditional.push(JSON.parse(JSON.stringify({ ...egg, position: { x: 250 + startX, y: this.SPAWN_Y } })));
        this.state.eggsSubstrates.push(JSON.parse(JSON.stringify({ ...egg, position: { x: 750 + startX, y: this.SPAWN_Y } })));
    }

    private updateSystem(dt: number, type: 'TRADITIONAL' | 'SUBSTRATES') {
        const eggs = type === 'TRADITIONAL' ? this.state.eggsTraditional : this.state.eggsSubstrates;
        const hand = type === 'TRADITIONAL' ? this.state.handTraditional : this.state.handSubstrates;
        const stats = type === 'TRADITIONAL' ? this.state.traditionalStats : this.state.substratesStats;
        const latency = type === 'TRADITIONAL' ? this.config.traditionalLatency : this.config.substratesLatency;

        // Update Stats
        stats.latency = latency;

        // 1. Update Egg Physics
        eggs.forEach(egg => {
            if (egg.state === 'FALLING') {
                egg.velocity.y += this.GRAVITY * dt;
                egg.velocity.y = Math.min(egg.velocity.y, this.TERMINAL_VELOCITY);
                egg.position.y += egg.velocity.y * dt;

                // Floor check
                if (egg.position.y > 600) {
                    egg.state = 'CRACKED';
                    stats.dropped++;
                }
            } else if (egg.state === 'CAUGHT') {
                // Move with hand
                // Handled by heldEggs logic
            } else if (egg.state === 'CRACKED') {
                // Continue falling with gravity
                egg.velocity.y += this.GRAVITY * dt;
                egg.velocity.y = Math.min(egg.velocity.y, this.TERMINAL_VELOCITY);
                egg.position.y += egg.velocity.y * dt;
            }
        });

        // Sync held eggs position
        hand.heldEggs.forEach((egg, index) => {
            // Stack them visually
            egg.position.x = hand.position.x;
            egg.position.y = hand.position.y + 10 + (index * 15);
            egg.velocity = { ...hand.velocity };
        });

        // 2. Control Loop

        // Check Capacity
        if (hand.heldEggs.length >= this.config.maxCapacity) {
            hand.mode = 'OFFLOADING';
        } else if (hand.heldEggs.length === 0 && hand.mode === 'OFFLOADING') {
            hand.mode = 'HUNTING';
        }

        if (hand.mode === 'OFFLOADING') {
            // Move to Offload Zone
            // Traditional -> Left (0), Substrates -> Right (Width)
            // Relative to their lane center? 
            // Traditional Lane Center = 250. Offload Left = 50.
            // Substrates Lane Center = 750. Offload Right = 950.

            const offloadX = type === 'TRADITIONAL' ? 50 : 950;
            const offloadY = 500; // Low to drop

            const dx = offloadX - hand.position.x;
            const dy = offloadY - hand.position.y;
            const dist = Math.sqrt(dx * dx + dy * dy);
            const speed = 0.8;

            if (dist > 10) {
                hand.velocity.x = (dx / dist) * speed;
                hand.velocity.y = (dy / dist) * speed;
            } else {
                // Arrived! Offload!
                hand.velocity = { x: 0, y: 0 };
                hand.heldEggs.forEach(e => {
                    // Remove from main array or mark as processed?
                    // For now, just clear them from hand and let them "disappear" or fall
                    // Let's just remove them from the simulation to keep it clean
                    const idx = eggs.findIndex(mainEgg => mainEgg.id === e.id);
                    if (idx !== -1) eggs.splice(idx, 1);
                });
                hand.heldEggs = [];
                hand.gripState = 'OPEN';
                // Mode will switch back to HUNTING next frame
            }

            // Smooth movement for offloading
            hand.position.x += hand.velocity.x * dt;
            hand.position.y += hand.velocity.y * dt;

        } else {
            // HUNTING MODE

            // Find target egg (lowest falling egg)
            const targetEgg = eggs.find(e => e.state === 'FALLING');

            if (targetEgg) {
                // Delayed Perception
                let perceivedPos = { ...targetEgg.position };

                if (latency > 0) {
                    perceivedPos.y -= targetEgg.velocity.y * latency;
                    perceivedPos.x -= targetEgg.velocity.x * latency;
                }

                // Strategy Update
                // Traditional: Catch High (Y=150) to minimize speed
                // Substrates: Catch Low (Y=400) to show off reflex

                const catchY = type === 'TRADITIONAL' ? 150 : 400;

                if (type === 'TRADITIONAL') {
                    // Advanced Traditional Logic: Prediction + Velocity Matching
                    // 1. Predict where egg will be at Catch Y
                    // We need time to reach Catch Y
                    // d = v*t + 0.5*a*t^2
                    // We know d (dist to Catch Y), v (current vel), a (gravity)
                    // Solve for t? Or just estimate based on current speed?
                    // Let's use current state to predict intersection.

                    const distToCatch = catchY - perceivedPos.y;

                    if (distToCatch > 0) {
                        // Egg is above catch line
                        // Wait at catch line, align X
                        const targetX = perceivedPos.x;
                        const targetY = catchY;

                        // Move hand
                        const dx = targetX - hand.position.x;
                        const dy = targetY - hand.position.y;
                        const dist = Math.sqrt(dx * dx + dy * dy);
                        const speed = 0.8;

                        if (dist > 5) {
                            hand.velocity.x = (dx / dist) * speed;
                            hand.velocity.y = (dy / dist) * speed;
                        } else {
                            hand.velocity = { x: 0, y: 0 };
                        }
                    } else {
                        // Egg is AT or BELOW catch line!
                        // EXECUTE CATCH MANEUVER

                        // We need to match the egg's velocity AND close the distance
                        const perceivedVelY = targetEgg.velocity.y;

                        // Latency Compensation: Predict where the egg IS right now
                        // perceivedPos is where it WAS 'latency' ms ago.
                        // estimatedPos = perceivedPos + (vel * latency)
                        const estimatedRealY = perceivedPos.y + (perceivedVelY * latency);
                        const estimatedRealX = perceivedPos.x + (targetEgg.velocity.x * latency);

                        // P-Controller for Position + Feed-Forward Velocity
                        const Kp = 0.15; // Position gain
                        const errorY = estimatedRealY - hand.position.y;
                        const errorX = estimatedRealX - hand.position.x;

                        hand.velocity.y = perceivedVelY + (errorY * Kp);
                        hand.velocity.x = targetEgg.velocity.x + (errorX * Kp);

                        // Close gripper if close enough to ESTIMATED position
                        // Note: We still use perceivedPos for the "trigger" in a pure vision system, 
                        // but a smart system would trigger based on prediction.
                        // Let's use the estimated distance for the trigger to give it a fair chance.
                        const estimatedDist = Math.sqrt(Math.pow(estimatedRealX - hand.position.x, 2) + Math.pow(estimatedRealY - hand.position.y, 2));

                        if (estimatedDist < this.config.gripThreshold) {
                            hand.gripState = 'CLOSED';
                        } else {
                            hand.gripState = 'OPEN';
                        }
                    }

                    hand.position.x += hand.velocity.x * dt;
                    hand.position.y += hand.velocity.y * dt;

                } else {
                    // SUBSTRATES Logic (Reflex)
                    // Strategy: Align X, wait at Catch Y until egg arrives, then follow
                    const targetX = perceivedPos.x;
                    const targetY = Math.max(perceivedPos.y, catchY);

                    // Move hand towards egg
                    // Use P-controller to avoid jitter
                    const dx = targetX - hand.position.x;
                    const dy = targetY - hand.position.y;
                    const dist = Math.sqrt(dx * dx + dy * dy);

                    // Max speed
                    const maxSpeed = 0.8;
                    const approachSpeed = Math.min(maxSpeed, dist * 0.05); // Slow down when close

                    if (dist > 0) {
                        hand.velocity.x = (dx / dist) * approachSpeed;
                        hand.velocity.y = (dy / dist) * approachSpeed;
                    }

                    hand.position.x += hand.velocity.x * dt;
                    hand.position.y += hand.velocity.y * dt;
                }

                // 3. Catch Logic
                const realDist = Math.sqrt(Math.pow(targetEgg.position.x - hand.position.x, 2) + Math.pow(targetEgg.position.y - hand.position.y, 2));

                if (type === 'SUBSTRATES') {
                    // GelSight / Sensor Fusion Logic
                    // If touching (distance < threshold), we have INSTANT, PRECISE info
                    if (realDist < this.config.gripThreshold) {
                        // "Reflex": Match velocity to cushion impact
                        const relVelY = Math.abs(targetEgg.velocity.y - hand.velocity.y);

                        if (relVelY < this.config.safeCatchSpeed) {
                            // Soft catch!
                            targetEgg.state = 'CAUGHT';
                            hand.heldEggs.push(targetEgg);
                            hand.gripState = 'CLOSED';
                            stats.caught++;
                        } else {
                            // Too fast! Cushion it!
                            // OVERRIDE the position controller to match egg velocity
                            // This ensures we don't fight the physics in the next frame
                            hand.velocity.y = targetEgg.velocity.y;
                            hand.velocity.x = targetEgg.velocity.x; // Match X too
                        }
                    }
                } else {
                    // Traditional Logic
                    // We already set gripState in the control loop based on perception

                    if (hand.gripState === 'CLOSED') {
                        // Did we actually catch it?
                        // Check REAL distance and REAL relative velocity
                        const actualRelVelY = Math.abs(targetEgg.velocity.y - hand.velocity.y);

                        // Passive Compliance / Springiness
                        // Traditional grippers often have rubber pads or spring loading.
                        // We simulate this by reducing the effective impact velocity.
                        const complianceFactor = 0.3; // Absorbs 70% of the impact energy (Very soft)
                        const effectiveImpactVel = actualRelVelY * complianceFactor;

                        if (realDist < this.config.gripThreshold) {
                            if (effectiveImpactVel < this.config.safeCatchSpeed) {
                                targetEgg.state = 'CAUGHT';
                                hand.heldEggs.push(targetEgg);
                                stats.caught++;
                            } else {
                                // Hard impact! Crack!
                                targetEgg.state = 'CRACKED';
                                stats.cracked++;
                                hand.gripState = 'OPEN'; // Failed catch
                            }
                        } else {
                            // Missed (closed hand on air)
                            // Egg continues falling
                        }
                    }
                }
            } else {
                // No eggs? Return to "Home" position (Center)
                // This prevents snapping when switching back from offload if no eggs are present
                // Or if we just finished offloading, we should move to the next target smoothly.
                // The P-controller in the target logic handles smooth movement, BUT
                // if we are far away (offload zone), we need to ensure we don't snap.
                // The current logic uses:
                // hand.velocity.x = (dx / dist) * approachSpeed;
                // This IS a smooth movement (velocity based).
                // So why does it snap?
                // Ah, maybe because `approachSpeed` is high? 
                // Or maybe because we set velocity to 0 when offload finishes?

                // Let's ensure we have a default target if no eggs
                const homeX = type === 'TRADITIONAL' ? 250 : 750;
                const homeY = 500;

                const dx = homeX - hand.position.x;
                const dy = homeY - hand.position.y;
                const dist = Math.sqrt(dx * dx + dy * dy);
                const speed = 0.8;

                if (dist > 5) {
                    hand.velocity.x = (dx / dist) * speed;
                    hand.velocity.y = (dy / dist) * speed;
                } else {
                    hand.velocity = { x: 0, y: 0 };
                }

                hand.position.x += hand.velocity.x * dt;
                hand.position.y += hand.velocity.y * dt;
            }

            // Cleanup
            if (eggs.length > 20) eggs.shift();
        }
    }
}

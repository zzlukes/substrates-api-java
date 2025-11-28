import type { DPISimulationState, DPISimulationConfig, Packet } from './types';

export class DPIGameLoop {
    private state: DPISimulationState;
    private config: DPISimulationConfig;

    // Packet Generation
    private spawnTimer = 0;
    private packetIdCounter = 0;

    // Traditional Processing (Queue)
    private packetQueue: Packet[] = [];
    private processingTimer = 0;

    // Substrates Processing (Wire Speed)
    // No queue, just throughput limits if any, but effectively instant for this demo

    constructor(config: DPISimulationConfig) {
        this.config = config;
        this.state = this.getInitialState();
    }

    private getInitialState(): DPISimulationState {
        return {
            time: 0,
            packetsTraditional: [],
            packetsSubstrates: [],
            traditionalStats: {
                throughput: 0,
                latency: 0,
                drops: 0,
                queueDepth: 0,
                maxQueueDepth: 0,
                processedCount: 0
            },
            substratesStats: {
                throughput: 0,
                latency: 0,
                drops: 0,
                queueDepth: 0,
                maxQueueDepth: 0,
                processedCount: 0
            }
        };
    }

    private createPacket(): Packet {
        const isMalicious = Math.random() < 0.1; // 10% malicious
        const isEncrypted = Math.random() < 0.3; // 30% encrypted

        return {
            id: `pkt-${this.packetIdCounter++}`,
            position: { x: 0, y: 0 }, // Will be set by spawn logic
            z: 0,
            velocity: { x: 0, y: 0 },
            velocityZ: 0,
            angle: 0,
            wingbeatPhase: 0,
            isMalicious,
            isEncrypted,
            processed: false,
            dropped: false
        };
    }

    public update(deltaTimeMs: number): DPISimulationState {
        const scaledDelta = deltaTimeMs * this.config.timeScale;
        this.state.time += scaledDelta;

        // 1. Spawn Packets (Arrival Rate)
        this.spawnTimer += scaledDelta;
        const spawnInterval = 1000 / (this.config.arrivalRate || 1);

        if (this.spawnTimer > spawnInterval) {
            const basePacket = this.createPacket();

            // Spawn at Left Edge
            const startY = 100 + Math.random() * 400; // Random Y spread

            // Clone for Traditional - Manually construct to ensure NO shared references
            const pTrad: Packet = {
                ...basePacket,
                id: basePacket.id + '-trad',
                position: { x: 0, y: startY },
                velocity: { x: this.config.mosquitoSpeed, y: 0 }
            };
            this.state.packetsTraditional.push(pTrad);

            // Clone for Substrates - Manually construct to ensure NO shared references
            const pSub: Packet = {
                ...basePacket,
                id: basePacket.id + '-sub',
                position: { x: 0, y: startY },
                velocity: { x: this.config.mosquitoSpeed, y: 0 }
            };
            this.state.packetsSubstrates.push(pSub);

            this.spawnTimer = 0;
        }

        // 2. Update Traditional (Buffered)
        this.updateTraditional(scaledDelta);

        // 3. Update Substrates (Wire Speed)
        this.updateSubstrates(scaledDelta);

        return this.state;
    }

    private updateTraditional(dt: number) {
        const APPLIANCE_X = 400; // Location of the "Appliance"
        const PROCESSING_TIME = this.config.processingCost; // Time to process one packet
        const BUFFER_SIZE = this.config.bufferSize;

        // Move packets
        this.state.packetsTraditional.forEach(p => {
            if (p.dropped) return; // Dropped packets disappear or fade out

            // If packet reaches appliance and not processed
            if (!p.processed && p.position.x >= APPLIANCE_X && p.position.x < APPLIANCE_X + 50) {
                // Enqueue
                if (!this.packetQueue.includes(p)) {
                    if (this.packetQueue.length < BUFFER_SIZE) {
                        this.packetQueue.push(p);
                        // Stop movement while in queue? Or just slow down?
                        // For visual clarity, let's stop them at the gate
                        p.velocity.x = 0;
                        p.position.x = APPLIANCE_X; // Stack them visually?
                    } else {
                        // Drop!
                        p.dropped = true;
                        this.state.traditionalStats.drops++;
                    }
                }
            } else {
                // Move normally
                p.position.x += p.velocity.x * dt;
            }
        });

        // Process Queue
        if (this.packetQueue.length > 0) {
            this.processingTimer += dt;
            if (this.processingTimer > PROCESSING_TIME) {
                const p = this.packetQueue.shift();
                if (p) {
                    p.processed = true;
                    p.velocity.x = this.config.mosquitoSpeed; // Resume movement
                    this.state.traditionalStats.processedCount++;
                }
                this.processingTimer = 0;
            }
        }

        // Update Stats
        this.state.traditionalStats.queueDepth = this.packetQueue.length;
        this.state.traditionalStats.maxQueueDepth = Math.max(this.state.traditionalStats.maxQueueDepth, this.packetQueue.length);

        // Cleanup off-screen
        this.state.packetsTraditional = this.state.packetsTraditional.filter(p => p.position.x < 1000 && !p.dropped);
    }

    private updateSubstrates(dt: number) {
        const APPLIANCE_X = 400;
        // Substrates processes in parallel / wire speed
        // Effectively, latency is negligible per packet relative to flow

        this.state.packetsSubstrates.forEach(p => {
            if (p.position.x >= APPLIANCE_X && !p.processed) {
                p.processed = true; // Instant process
                this.state.substratesStats.processedCount++;

                // If malicious, maybe we drop it? Or just mark it?
                // For DPI demo, we usually want to show "Inspected & Allowed" vs "Inspected & Blocked"
                if (p.isMalicious) {
                    p.dropped = true; // Blocked threat
                    this.state.substratesStats.drops++; // Count as "Blocked" (good drop)
                }
            }

            if (!p.dropped) {
                // Force velocity to be constant wire speed (override any potential stops)
                p.velocity.x = this.config.mosquitoSpeed;
                p.position.x += p.velocity.x * dt;
            }
        });

        // Cleanup
        this.state.packetsSubstrates = this.state.packetsSubstrates.filter(p => p.position.x < 1000 && !p.dropped);
    }

    public setConfig(config: Partial<DPISimulationConfig>) {
        this.config = { ...this.config, ...config };
    }

    public getState(): DPISimulationState {
        return this.state;
    }
}

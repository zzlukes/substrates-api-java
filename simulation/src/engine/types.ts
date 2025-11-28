export interface Vector2 {
  x: number;
  y: number;
}

export const TargetingState = {
  SEARCHING: 'SEARCHING',
  HEARD: 'HEARD',
  SPOTTED: 'SPOTTED',
  TARGETED: 'TARGETED',
  SHOT: 'SHOT'
} as const;

export type TargetingState = typeof TargetingState[keyof typeof TargetingState];

export interface Entity {
  position: Vector2;
  z: number; // Height (0-500px equivalent to 0-4m)
  velocity: Vector2;
  velocityZ: number;
  angle: number; // In radians
  wingbeatPhase: number; // 0 to 2PI
}

export interface SimulationState {
  time: number; // Simulation time in milliseconds
  mosquitoesTraditional: Entity[]; // Swarm for Traditional
  mosquitoesSubstrates: Entity[]; // Swarm for Substrates
  turretTraditional: Entity;
  turretSubstrates: Entity;

  // Stats
  traditionalStats: {
    latency: number; // Current simulated latency in ms
    hits: number;
    misses: number;
    state: TargetingState;
    battery: number; // Remaining energy
    energyUsed: number; // Total energy consumed
    targetIndex: number; // Index of current target in swarm
  };

  substratesStats: {
    latency: number; // Current simulated latency in ms
    hits: number;
    misses: number;
    state: TargetingState;
    battery: number; // Remaining energy
    energyUsed: number; // Total energy consumed
    targetIndex: number; // Index of current target in swarm
  };
}

export interface SimulationConfig {
  timeScale: number; // 1.0 = Realtime, 0.1 = Slow motion
  traditionalLatency: number; // ms
  substratesLatency: number; // ms
  mosquitoSpeed: number; // pixels per ms
  arrivalRate: number; // mosquitoes per second
  policy: 'ENGAGE' | 'OBSERVE';
  mode: 'MOSQUITO' | 'DPI' | 'ROBOTIC';
  maxCapacity?: number;
}

export interface Egg extends Entity {
  id: string;
  state: 'FALLING' | 'CAUGHT' | 'CRACKED';
  radius: number;
}

export interface Hand extends Entity {
  gripState: 'OPEN' | 'CLOSED';
  gripForce: number; // 0-1
  heldEggs: Egg[];
  mode: 'HUNTING' | 'OFFLOADING';
}

export interface RoboticSimulationState {
  time: number;
  eggsTraditional: Egg[];
  eggsSubstrates: Egg[];
  handTraditional: Hand;
  handSubstrates: Hand;

  traditionalStats: {
    latency: number;
    caught: number;
    cracked: number;
    dropped: number;
  };

  substratesStats: {
    latency: number;
    caught: number;
    cracked: number;
    dropped: number;
  };
}

export interface RoboticSimulationConfig extends SimulationConfig {
  gripThreshold: number; // Distance to trigger grip
  eggMass: number;
  safeCatchSpeed: number; // Max relative speed for safe catch
  maxCapacity: number;
}

export interface Packet extends Entity {
  id: string;
  isMalicious: boolean;
  isEncrypted: boolean;
  processed: boolean;
  dropped: boolean;
}

export interface DPIStats {
  throughput: number; // Packets per second
  latency: number; // Average processing latency
  drops: number; // Total dropped packets
  queueDepth: number; // Current queue size
  maxQueueDepth: number; // Peak queue size
  processedCount: number;
}

export interface DPISimulationState {
  time: number;
  packetsTraditional: Packet[];
  packetsSubstrates: Packet[];

  traditionalStats: DPIStats;
  substratesStats: DPIStats;
}

export interface DPISimulationConfig extends SimulationConfig {
  bufferSize: number; // Max packets in queue for Traditional
  processingCost: number; // ms to process one packet
}

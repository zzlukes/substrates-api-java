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
}

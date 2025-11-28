import { useState, useEffect, useRef } from 'react';
import { GameLoop } from './engine/GameLoop';
import { DPIGameLoop } from './engine/DPIGameLoop';
import type { SimulationConfig, SimulationState, DPISimulationState } from './engine/types';
import { SimulationCanvas } from './components/SimulationCanvas';
import { DPISimulationCanvas } from './components/DPISimulationCanvas';
import { Controls } from './components/Controls';
import { StatsPanel } from './components/StatsPanel';
import { RoboticGameLoop } from './engine/RoboticGameLoop';
import { RoboticSimulationCanvas } from './components/RoboticSimulationCanvas';
import type { RoboticSimulationState } from './engine/types';

const INITIAL_CONFIG: SimulationConfig = {
  timeScale: 1.0,
  traditionalLatency: 100, // 100ms default
  substratesLatency: 1,    // 1ms default
  mosquitoSpeed: 0.3,      // pixels per ms
  arrivalRate: 5,          // mosquitoes per second
  policy: 'ENGAGE',
  mode: 'MOSQUITO',
};

// DPI Defaults
const DPI_DEFAULTS = {
  bufferSize: 20,
  processingCost: 50, // ms
};

function App() {
  const [config, setConfig] = useState<SimulationConfig>(INITIAL_CONFIG);
  const [isPlaying, setIsPlaying] = useState(true);
  const [state, setState] = useState<SimulationState | DPISimulationState | RoboticSimulationState>(() => new GameLoop(INITIAL_CONFIG).getState());

  const gameLoopRef = useRef<GameLoop | DPIGameLoop | RoboticGameLoop | null>(null);
  const requestRef = useRef<number | undefined>(undefined);
  const lastTimeRef = useRef<number | undefined>(undefined);

  // Refs for state accessible in animation loop
  const configRef = useRef(config);
  const isPlayingRef = useRef(isPlaying);

  // Sync refs
  useEffect(() => {
    configRef.current = config;
    if (gameLoopRef.current) {
      // @ts-ignore - Config types are compatible enough for this demo
      gameLoopRef.current.setConfig(config);
    }
  }, [config]);

  useEffect(() => {
    isPlayingRef.current = isPlaying;
  }, [isPlaying]);

  // Mode Switch / Init Effect
  useEffect(() => {
    // Re-initialize loop when mode changes
    if (config.mode === 'DPI') {
      gameLoopRef.current = new DPIGameLoop({ ...config, ...DPI_DEFAULTS });
    } else if (config.mode === 'ROBOTIC') {
      gameLoopRef.current = new RoboticGameLoop(config);
    } else {
      gameLoopRef.current = new GameLoop(config);
    }

    // Reset time
    lastTimeRef.current = performance.now();

    // Force initial state update
    setState(gameLoopRef.current.getState());

  }, [config.mode]); // Only re-run on mode change

  useEffect(() => {
    // Animation Loop
    const animate = (time: number) => {
      if (isPlayingRef.current && lastTimeRef.current !== undefined) {
        const deltaTime = time - lastTimeRef.current;
        lastTimeRef.current = time;

        if (gameLoopRef.current) {
          // Ensure loop has latest config
          // @ts-ignore
          gameLoopRef.current.setConfig(configRef.current);
          const newState = gameLoopRef.current.update(deltaTime);
          setState({ ...newState }); // Force re-render
        }
      } else {
        lastTimeRef.current = time;
      }
      requestRef.current = requestAnimationFrame(animate);
    };

    requestRef.current = requestAnimationFrame(animate);

    return () => {
      if (requestRef.current) cancelAnimationFrame(requestRef.current);
    };
  }, []); // Init once (loop logic is stable, refs handle updates)

  // Handle play/pause
  const togglePlay = () => {
    setIsPlaying(!isPlaying);
    lastTimeRef.current = performance.now(); // Reset time to avoid huge delta
  };

  const isDPI = config.mode === 'DPI';
  const isRobotic = config.mode === 'ROBOTIC';

  const getTitle = () => {
    if (isDPI) return 'Network Deep Packet Inspection';
    if (isRobotic) return 'Robotic Control: The Egg Catch';
    return 'Substrates Adaptive Control Demo';
  };

  const getSubtitle = () => {
    if (isDPI) return 'Comparing Traditional Appliance (Buffered) vs. Wire Speed Inspection';
    if (isRobotic) return 'Comparing Cloud Vision (High Latency) vs. Edge GelSight (Sensor Fusion)';
    return 'Comparing Traditional Cloud Control vs. Edge Native Adaptive Control';
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-8 gap-6">
      <header className="text-center space-y-2">
        <h1 className="text-4xl font-bold bg-gradient-to-r from-amber-400 to-blue-500 bg-clip-text text-transparent">
          {getTitle()}
        </h1>
        <p className="text-slate-400">
          {getSubtitle()}
        </p>
      </header>

      <div className="relative rounded-xl overflow-hidden shadow-2xl border border-slate-700">
        {isDPI && 'packetsTraditional' in state ? (
          <DPISimulationCanvas state={state as DPISimulationState} width={1000} height={600} isPlaying={isPlaying} />
        ) : isRobotic && 'eggsTraditional' in state ? (
          <RoboticSimulationCanvas state={state as RoboticSimulationState} width={1000} height={600} isPlaying={isPlaying} />
        ) : !isDPI && !isRobotic && 'mosquitoesTraditional' in state ? (
          <SimulationCanvas state={state as SimulationState} width={1000} height={600} isPlaying={isPlaying} />
        ) : (
          <div className="w-[1000px] h-[600px] bg-slate-900 flex items-center justify-center text-slate-500">
            Initializing Simulation...
          </div>
        )}
      </div>

      <div className="w-full max-w-4xl grid grid-cols-1 md:grid-cols-2 gap-6">
        <Controls
          config={config}
          onConfigChange={(newConfig) => setConfig({ ...config, ...newConfig })}
          isPlaying={isPlaying}
          onTogglePlay={togglePlay}
        />
        {!isDPI && !isRobotic && <StatsPanel state={state as SimulationState} />}
      </div>
    </div>
  );
}

export default App;

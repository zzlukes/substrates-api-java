import { useState, useEffect, useRef } from 'react';
import { GameLoop } from './engine/GameLoop';
import type { SimulationConfig } from './engine/types';
import { SimulationCanvas } from './components/SimulationCanvas';
import { Controls } from './components/Controls';
import { StatsPanel } from './components/StatsPanel';

const INITIAL_CONFIG: SimulationConfig = {
  timeScale: 1.0,
  traditionalLatency: 100, // 100ms default
  substratesLatency: 1,    // 1ms default
  mosquitoSpeed: 0.3,      // pixels per ms
  arrivalRate: 5,          // mosquitoes per second
  policy: 'ENGAGE',
};

function App() {
  const [config, setConfig] = useState<SimulationConfig>(INITIAL_CONFIG);
  const [isPlaying, setIsPlaying] = useState(true);
  const [state, setState] = useState(() => new GameLoop(INITIAL_CONFIG).getState());

  const gameLoopRef = useRef<GameLoop | null>(null);
  const requestRef = useRef<number | undefined>(undefined);
  const lastTimeRef = useRef<number | undefined>(undefined);

  // Refs for state accessible in animation loop
  const configRef = useRef(config);
  const isPlayingRef = useRef(isPlaying);

  // Sync refs
  useEffect(() => {
    configRef.current = config;
    if (gameLoopRef.current) {
      gameLoopRef.current.setConfig(config);
    }
  }, [config]);

  useEffect(() => {
    isPlayingRef.current = isPlaying;
  }, [isPlaying]);

  useEffect(() => {
    gameLoopRef.current = new GameLoop(INITIAL_CONFIG);
    lastTimeRef.current = performance.now();

    const animate = (time: number) => {
      if (isPlayingRef.current && lastTimeRef.current !== undefined) {
        const deltaTime = time - lastTimeRef.current;
        lastTimeRef.current = time;

        if (gameLoopRef.current) {
          // Ensure loop has latest config (redundant but safe)
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
  }, []); // Init once

  // Handle play/pause
  const togglePlay = () => {
    setIsPlaying(!isPlaying);
    lastTimeRef.current = performance.now(); // Reset time to avoid huge delta
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-8 gap-6">
      <header className="text-center space-y-2">
        <h1 className="text-4xl font-bold bg-gradient-to-r from-amber-400 to-blue-500 bg-clip-text text-transparent">
          Substrates Adaptive Control Demo
        </h1>
        <p className="text-slate-400">
          Comparing Traditional Cloud Control vs. Edge Native Adaptive Control
        </p>
      </header>

      <div className="relative rounded-xl overflow-hidden shadow-2xl border border-slate-700">
        <SimulationCanvas state={state} width={1000} height={600} isPlaying={isPlaying} />
      </div>

      <div className="w-full max-w-4xl grid grid-cols-1 md:grid-cols-2 gap-6">
        <Controls
          config={config}
          onConfigChange={(newConfig) => setConfig({ ...config, ...newConfig })}
          isPlaying={isPlaying}
          onTogglePlay={togglePlay}
        />
        <StatsPanel state={state} />
      </div>
    </div>
  );
}

export default App;

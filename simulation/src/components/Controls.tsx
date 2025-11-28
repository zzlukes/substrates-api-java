import React from 'react';
import type { SimulationConfig } from '../engine/types';

interface ControlsProps {
    config: SimulationConfig;
    onConfigChange: (config: Partial<SimulationConfig>) => void;
    isPlaying: boolean;
    onTogglePlay: () => void;
}

export const Controls: React.FC<ControlsProps> = ({ config, onConfigChange, isPlaying, onTogglePlay }) => {

    // Logarithmic slider conversion
    const toLog = (val: number) => Math.log10(val);
    const fromLog = (val: number) => Math.pow(10, val);

    const isDPI = config.mode === 'DPI';

    return (
        <div className="p-4 bg-slate-800 text-slate-200 rounded-lg shadow-lg space-y-4">
            <div className="flex items-center justify-between">
                <h2 className="text-xl font-bold">Simulation Controls</h2>
                <button
                    onClick={onTogglePlay}
                    className={`p-2 rounded-full font-bold transition-colors ${isPlaying ? 'bg-slate-700 hover:bg-slate-600 text-amber-400' : 'bg-green-600 hover:bg-green-500 text-white'}`}
                    title={isPlaying ? "Pause" : "Play"}
                >
                    {isPlaying ? (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 9v6m4-6v6m7-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    )}
                </button>
            </div>

            {/* Mode Switcher */}
            <div className="flex bg-slate-900 rounded-lg p-1">
                <button
                    className={`flex-1 py-2 rounded-md text-sm font-bold transition-all ${!isDPI ? 'bg-slate-700 text-white shadow' : 'text-slate-400 hover:text-slate-200'}`}
                    onClick={() => onConfigChange({ mode: 'MOSQUITO' })}
                >
                    Mosquito Defense
                </button>
                <button
                    className={`flex-1 py-2 rounded-md text-sm font-bold transition-all ${isDPI ? 'bg-blue-600 text-white shadow' : 'text-slate-400 hover:text-slate-200'}`}
                    onClick={() => onConfigChange({ mode: 'DPI' })}
                >
                    Network DPI
                </button>
                <button
                    className={`flex-1 py-2 rounded-md text-sm font-bold transition-all ${config.mode === 'ROBOTIC' ? 'bg-amber-600 text-white shadow' : 'text-slate-400 hover:text-slate-200'}`}
                    onClick={() => onConfigChange({ mode: 'ROBOTIC' })}
                >
                    Robotic Control
                </button>
            </div>

            {/* Time Scale */}
            <div className="space-y-2">
                <label className="block text-sm font-medium">
                    Time Scale: {config.timeScale.toFixed(3)}x
                </label>
                <input
                    type="range"
                    min="-4" // 0.0001
                    max="0"  // 1.0
                    step="0.1"
                    value={toLog(config.timeScale)}
                    onChange={(e) => onConfigChange({ timeScale: fromLog(parseFloat(e.target.value)) })}
                    className="w-full h-2 bg-slate-700 rounded-lg appearance-none cursor-pointer"
                />
                <div className="flex justify-between text-xs text-slate-400">
                    <span>0.0001x</span>
                    <span>1.0x</span>
                </div>
            </div>

            {/* Latency Controls */}
            <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                    <label className="block text-sm font-medium text-amber-400">
                        {isDPI ? 'Appliance Latency (Buffered)' : 'Vision Latency'} : {config.traditionalLatency}ms
                    </label>
                    <input
                        type="range"
                        min="0"
                        max="500"
                        value={config.traditionalLatency}
                        onChange={(e) => onConfigChange({ traditionalLatency: parseInt(e.target.value) })}
                        className="w-full h-2 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-amber-500"
                    />
                </div>
                <div className="space-y-2">
                    <label className="block text-sm font-medium text-blue-400">
                        {isDPI ? 'Wire Speed Latency' : 'Edge Latency'} : {config.substratesLatency < 0.001 ? (config.substratesLatency * 1000000).toFixed(0) + 'ns' : config.substratesLatency < 1 ? (config.substratesLatency * 1000).toFixed(0) + 'µs' : config.substratesLatency.toFixed(2) + 'ms'}
                    </label>
                    <input
                        type="range"
                        min="-6" // 0.000001 ms = 1ns
                        max="2"  // 100 ms
                        step="0.1"
                        value={Math.log10(config.substratesLatency || 0.000001)}
                        onChange={(e) => onConfigChange({ substratesLatency: Math.pow(10, parseFloat(e.target.value)) })}
                        className="w-full h-2 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-blue-500"
                    />
                    <div className="flex justify-between text-xs text-slate-400">
                        <span>1ns</span>
                        <span>1µs</span>
                        <span>1ms</span>
                        <span>100ms</span>
                    </div>
                </div>
                <div className="space-y-2">
                    <label className="text-sm text-slate-400">Policy</label>
                    <div className="flex gap-2">
                        <button
                            className={`flex-1 py-1 rounded text-sm font-bold ${config.policy === 'ENGAGE' ? 'bg-red-500 text-white' : 'bg-slate-700 text-slate-400'}`}
                            onClick={() => onConfigChange({ policy: 'ENGAGE' })}
                        >
                            ENGAGE
                        </button>
                        <button
                            className={`flex-1 py-1 rounded text-sm font-bold ${config.policy === 'OBSERVE' ? 'bg-blue-500 text-white' : 'bg-slate-700 text-slate-400'}`}
                            onClick={() => onConfigChange({ policy: 'OBSERVE' })}
                        >
                            OBSERVE
                        </button>
                    </div>
                </div>

                <div className="space-y-2">
                    <label className="text-sm text-slate-400">{isDPI ? 'Packet Arrival Rate' : 'Arrival Rate'}: {config.arrivalRate} / sec</label>
                    <input
                        type="range"
                        min="0"
                        max={isDPI ? "100" : "20"}
                        value={config.arrivalRate}
                        onChange={(e) => onConfigChange({ arrivalRate: parseInt(e.target.value) })}
                        className="w-full accent-blue-500"
                    />
                </div>


            </div>
        </div>
    );
};

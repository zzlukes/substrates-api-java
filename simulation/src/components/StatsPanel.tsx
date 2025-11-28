import React from 'react';
import type { SimulationState } from '../engine/types';

interface StatsPanelProps {
    state: SimulationState;
}

export const StatsPanel: React.FC<StatsPanelProps> = ({ state }) => {
    return (
        <div className="grid grid-cols-2 gap-4 p-4 bg-slate-800 text-slate-200 rounded-lg shadow-lg">
            {/* Traditional Stats */}
            <div className="space-y-2 border-r border-slate-700 pr-4">
                <h3 className="text-lg font-bold text-amber-400">Traditional (Cloud)</h3>
                <div className="grid grid-cols-2 gap-2 text-sm">
                    <div className="text-slate-400">Latency</div>
                    <div className="text-right font-mono">{state.traditionalStats.latency.toFixed(0)} ms</div>

                    <div className="text-slate-400">State</div>
                    <div className={`text-right font-mono font-bold ${state.traditionalStats.state === 'SHOT' ? 'text-red-500' : 'text-amber-400'}`}>
                        {state.traditionalStats.state}
                    </div>

                    <div className="col-span-2 mt-4 border-t border-slate-700 pt-2">
                        <div className="text-slate-400 text-xs uppercase tracking-wider">Energy Consumed</div>
                        <div className="text-2xl font-mono text-white">{state.traditionalStats.energyUsed.toLocaleString()} J</div>
                    </div>
                </div>
            </div>

            {/* Substrates Stats */}
            <div className="space-y-2 pl-4">
                <h3 className="text-lg font-bold text-blue-400">Substrates (Edge)</h3>
                <div className="grid grid-cols-2 gap-2 text-sm">
                    <div className="text-slate-400">Latency</div>
                    <div className="text-right font-mono">{state.substratesStats.latency.toFixed(2)} ms</div>

                    <div className="text-slate-400">State</div>
                    <div className={`text-right font-mono font-bold ${state.substratesStats.state === 'SHOT' ? 'text-green-500' : 'text-blue-400'}`}>
                        {state.substratesStats.state}
                    </div>

                    <div className="col-span-2 mt-4 border-t border-slate-700 pt-2">
                        <div className="text-slate-400 text-xs uppercase tracking-wider">Energy Consumed</div>
                        <div className="text-2xl font-mono text-white">{state.substratesStats.energyUsed.toLocaleString()} J</div>
                    </div>
                </div>
            </div>
        </div>
    );
};

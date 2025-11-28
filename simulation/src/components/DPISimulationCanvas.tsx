import React, { useRef, useEffect } from 'react';
import type { DPISimulationState, Packet } from '../engine/types';

interface DPISimulationCanvasProps {
    state: DPISimulationState;
    width: number;
    height: number;
    isPlaying: boolean;
}

export const DPISimulationCanvas: React.FC<DPISimulationCanvasProps> = ({ state, width, height, isPlaying }) => {
    const canvasRef = useRef<HTMLCanvasElement>(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        try {
            // Clear
            ctx.clearRect(0, 0, width, height);

            // Draw Background (Split Screen)
            ctx.fillStyle = '#0f172a'; // Dark Slate
            ctx.fillRect(0, 0, width, height);

            // Split Line
            ctx.strokeStyle = '#334155';
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(0, height / 2);
            ctx.lineTo(width, height / 2);
            ctx.stroke();

            // Labels
            ctx.font = '20px Inter, sans-serif';
            ctx.fillStyle = '#94a3b8';
            ctx.textAlign = 'left';
            ctx.fillText('Traditional Appliance (Buffered)', 20, 40);
            ctx.fillText('Substrates Appliance (Wire Speed)', 20, height / 2 + 40);

            // Draw Packet Function
            const drawPacket = (p: Packet, offsetY: number) => {
                ctx.save();
                ctx.translate(p.position.x, p.position.y + offsetY);

                // Packet Shape
                const size = 12;

                if (p.isMalicious) {
                    ctx.fillStyle = '#ef4444'; // Red
                } else if (p.isEncrypted) {
                    ctx.fillStyle = '#a855f7'; // Purple
                } else {
                    ctx.fillStyle = '#3b82f6'; // Blue
                }

                // If processed, maybe glow or change style?
                if (p.processed) {
                    ctx.strokeStyle = '#22c55e'; // Green border
                    ctx.lineWidth = 2;
                    ctx.strokeRect(-size / 2, -size / 2, size, size);
                }

                ctx.fillRect(-size / 2, -size / 2, size, size);

                ctx.restore();
            };

            // Draw Appliance / Inspection Zone
            const APPLIANCE_X = 400;
            const ZONE_WIDTH = 50;

            // Traditional Appliance (Top)
            ctx.fillStyle = '#1e293b';
            ctx.fillRect(APPLIANCE_X, 50, ZONE_WIDTH, height / 2 - 100);
            ctx.strokeStyle = '#475569';
            ctx.strokeRect(APPLIANCE_X, 50, ZONE_WIDTH, height / 2 - 100);

            // Queue Visualization (Traditional)
            const queueHeight = state.traditionalStats.queueDepth * 5;
            ctx.fillStyle = state.traditionalStats.queueDepth > 40 ? '#ef4444' : '#fbbf24'; // Red if full
            ctx.fillRect(APPLIANCE_X + 10, height / 2 - 60 - queueHeight, 30, queueHeight);

            // Substrates Appliance (Bottom)
            ctx.fillStyle = '#1e293b';
            ctx.fillRect(APPLIANCE_X, height / 2 + 50, ZONE_WIDTH, height / 2 - 100);
            // Laser Gate Effect
            ctx.strokeStyle = '#06b6d4'; // Cyan
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(APPLIANCE_X + ZONE_WIDTH / 2, height / 2 + 50);
            ctx.lineTo(APPLIANCE_X + ZONE_WIDTH / 2, height - 50);
            ctx.stroke();

            // Render Packets
            // Traditional (Top Half)
            state.packetsTraditional.forEach(p => drawPacket(p, 0));

            // Substrates (Bottom Half)
            state.packetsSubstrates.forEach(p => drawPacket(p, height / 2));

            // Stats Overlay
            ctx.font = '14px monospace';
            ctx.fillStyle = '#fff';

            // Trad Stats
            ctx.fillText(`Queue: ${state.traditionalStats.queueDepth}`, APPLIANCE_X + 60, 100);
            ctx.fillText(`Drops: ${state.traditionalStats.drops}`, APPLIANCE_X + 60, 120);
            ctx.fillText(`Processed: ${state.traditionalStats.processedCount}`, APPLIANCE_X + 60, 140);

            // Sub Stats
            ctx.fillText(`Queue: 0 (Wire Speed)`, APPLIANCE_X + 60, height / 2 + 100);
            ctx.fillText(`Drops: ${state.substratesStats.drops} (Blocked)`, APPLIANCE_X + 60, height / 2 + 120);
            ctx.fillText(`Processed: ${state.substratesStats.processedCount}`, APPLIANCE_X + 60, height / 2 + 140);

        } catch (e) {
            console.error("Canvas Rendering Error:", e);
        }
    }, [state, width, height, isPlaying]);

    return <canvas ref={canvasRef} width={width} height={height} className="block" />;
};

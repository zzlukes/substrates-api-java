import React, { useRef, useEffect } from 'react';
import type { RoboticSimulationState, Egg, Hand } from '../engine/types';

interface Props {
    state: RoboticSimulationState;
    width: number;
    height: number;
    isPlaying: boolean;
}

export const RoboticSimulationCanvas: React.FC<Props> = ({ state, width, height }) => {
    const canvasRef = useRef<HTMLCanvasElement>(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        // Clear
        ctx.fillStyle = '#0f172a'; // Slate 900
        ctx.fillRect(0, 0, width, height);

        // Draw Divider
        ctx.strokeStyle = '#334155'; // Slate 700
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(width / 2, 0);
        ctx.lineTo(width / 2, height);
        ctx.stroke();

        // Labels
        ctx.font = 'bold 20px Inter, sans-serif';
        ctx.textAlign = 'center';
        ctx.fillStyle = '#94a3b8'; // Slate 400
        ctx.fillText('Traditional (Vision)', width * 0.25, 30);
        ctx.fillText('Substrates (Edge GelSight)', width * 0.75, 30);

        // Draw Offload Zones
        drawOffloadZones(ctx, width, height);

        // Draw Scenes
        drawScene(ctx, state.eggsTraditional, state.handTraditional, 0);
        drawScene(ctx, state.eggsSubstrates, state.handSubstrates, width / 2);

        // Stats Overlay
        drawStats(ctx, state, width, height);

    }, [state, width, height]);

    const drawScene = (
        ctx: CanvasRenderingContext2D,
        eggs: Egg[],
        hand: Hand,
        offsetX: number,
        // w: number,
        // h: number
    ) => {
        ctx.save();
        ctx.translate(offsetX, 0);

        // Draw Eggs
        eggs.forEach(egg => {
            ctx.beginPath();
            ctx.arc(egg.position.x - offsetX, egg.position.y, egg.radius, 0, Math.PI * 2);

            if (egg.state === 'FALLING') ctx.fillStyle = '#f8fafc'; // White
            else if (egg.state === 'CAUGHT') ctx.fillStyle = '#4ade80'; // Green
            else if (egg.state === 'CRACKED') ctx.fillStyle = '#ef4444'; // Red

            ctx.fill();

            // Shine effect
            ctx.beginPath();
            ctx.arc(egg.position.x - offsetX - 5, egg.position.y - 5, 4, 0, Math.PI * 2);
            ctx.fillStyle = 'rgba(255, 255, 255, 0.5)';
            ctx.fill();
        });

        // Draw Held Eggs
        hand.heldEggs.forEach(egg => {
            ctx.beginPath();
            ctx.arc(egg.position.x - offsetX, egg.position.y, egg.radius, 0, Math.PI * 2);
            ctx.fillStyle = '#4ade80'; // Green
            ctx.fill();

            // Shine
            ctx.beginPath();
            ctx.arc(egg.position.x - offsetX - 5, egg.position.y - 5, 4, 0, Math.PI * 2);
            ctx.fillStyle = 'rgba(255, 255, 255, 0.5)';
            ctx.fill();
        });

        // Draw Hand
        const handX = hand.position.x - offsetX;
        const handY = hand.position.y;

        ctx.fillStyle = hand.gripState === 'CLOSED' ? '#fbbf24' : '#64748b'; // Amber if closed, Slate if open

        // Simple U-shape claw
        ctx.beginPath();
        ctx.arc(handX, handY, 30, 0, Math.PI, false); // Bottom arc
        ctx.lineTo(handX - 30, handY - 20);
        ctx.lineTo(handX - 20, handY - 20);
        ctx.arc(handX, handY, 20, Math.PI, 0, true); // Inner arc
        ctx.lineTo(handX + 30, handY - 20);
        ctx.closePath();
        ctx.fill();

        // Capacity Indicator
        if (hand.heldEggs.length > 0) {
            ctx.fillStyle = '#ffffff';
            ctx.font = '12px Inter, sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText(`${hand.heldEggs.length}`, handX, handY + 5);
        }

        // GelSight Visual (Substrates only)
        if (offsetX > 0 && hand.gripState === 'CLOSED') { // Right side is Substrates
            ctx.strokeStyle = '#38bdf8'; // Sky blue
            ctx.lineWidth = 3;
            ctx.beginPath();
            ctx.arc(handX, handY, 35, 0, Math.PI * 2);
            ctx.stroke();

            // "Sensor Data" particles
            for (let i = 0; i < 5; i++) {
                ctx.fillStyle = '#38bdf8';
                ctx.fillRect(handX + (Math.random() * 40 - 20), handY + (Math.random() * 40 - 20), 2, 2);
            }
        }

        ctx.restore();
    };

    const drawStats = (ctx: CanvasRenderingContext2D, state: RoboticSimulationState, w: number, h: number) => {
        const drawStatBox = (x: number, stats: any, color: string) => {
            ctx.fillStyle = 'rgba(15, 23, 42, 0.8)';
            ctx.fillRect(x - 100, h - 120, 200, 100);

            ctx.fillStyle = color;
            ctx.font = 'bold 16px monospace';
            ctx.textAlign = 'left';
            ctx.fillText(`Caught:  ${stats.caught}`, x - 80, h - 90);
            ctx.fillText(`Cracked: ${stats.cracked}`, x - 80, h - 70);
            ctx.fillText(`Dropped: ${stats.dropped}`, x - 80, h - 50);
            ctx.fillText(`Latency: ${stats.latency.toFixed(1)}ms`, x - 80, h - 30);
        };

        drawStatBox(w * 0.25, state.traditionalStats, '#fbbf24');
        drawStatBox(w * 0.75, state.substratesStats, '#38bdf8');
    };

    const drawOffloadZones = (ctx: CanvasRenderingContext2D, w: number, h: number) => {
        // Traditional Zone (Left)
        ctx.fillStyle = 'rgba(255, 255, 255, 0.1)';
        ctx.fillRect(0, h - 150, 100, 150);
        ctx.fillStyle = '#94a3b8';
        ctx.font = '14px Inter, sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText('Offload', 50, h - 20);

        // Substrates Zone (Right)
        ctx.fillStyle = 'rgba(255, 255, 255, 0.1)';
        ctx.fillRect(w - 100, h - 150, 100, 150);
        ctx.fillText('Offload', w - 50, h - 20);
    };

    return <canvas ref={canvasRef} width={width} height={height} className="w-full h-full" />;
};

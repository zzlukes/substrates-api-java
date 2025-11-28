import React, { useRef, useEffect } from 'react';
import type { SimulationState, Entity } from '../engine/types';
import { TargetingState } from '../engine/types';

interface SimulationCanvasProps {
    state: SimulationState;
    width: number;
    height: number;
    isPlaying: boolean;
}

export const SimulationCanvas: React.FC<SimulationCanvasProps> = ({ state, width, height, isPlaying }) => {
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
            ctx.fillStyle = '#1a1a1a';
            ctx.fillRect(0, 0, width / 2, height); // Left: Traditional
            ctx.fillStyle = '#0f172a';
            ctx.fillRect(width / 2, 0, width / 2, height); // Right: Substrates

            // Draw Divider
            ctx.strokeStyle = '#334155';
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(width / 2, 0);
            ctx.lineTo(width / 2, height);
            ctx.stroke();

            // Draw Labels
            ctx.font = '20px Inter, sans-serif';
            ctx.fillStyle = '#94a3b8';
            ctx.textAlign = 'center';
            ctx.fillText('Traditional (Cloud)', width / 4, 40);
            ctx.fillText('Substrates (Edge)', (width / 4) * 3, 40);

            // Kill Counts
            ctx.font = 'bold 32px Inter, sans-serif';
            ctx.fillStyle = '#ef4444'; // Red
            ctx.fillText(`${state.traditionalStats.hits}`, width / 4, 80);
            ctx.fillStyle = '#06b6d4'; // Cyan
            ctx.fillText(`${state.substratesStats.hits}`, (width / 4) * 3, 80);

            // Pause Overlay
            if (!isPlaying) {
                ctx.fillStyle = 'rgba(0, 0, 0, 0.5)';
                ctx.fillRect(0, 0, width, height);

                ctx.font = 'bold 48px Inter, sans-serif';
                ctx.fillStyle = '#fff';
                ctx.textAlign = 'center';
                ctx.fillText('PAUSED', width / 2, height / 2);
            }


            const drawMosquito = (entity: Entity, offsetX: number = 0) => {
                ctx.save();
                ctx.translate(entity.position.x + offsetX, entity.position.y);

                // Wingbeat visual (pulsing based on phase)
                // Phase is 0-2PI. 
                const wingOffset = Math.sin(entity.wingbeatPhase || 0) * 8;

                ctx.strokeStyle = 'rgba(255, 255, 255, 0.5)';
                ctx.lineWidth = 1;

                // Left Wing
                ctx.beginPath();
                ctx.ellipse(-6, 0, 8, Math.abs(3 + wingOffset / 2), Math.PI / 4, 0, Math.PI * 2);
                ctx.stroke();

                // Right Wing
                ctx.beginPath();
                ctx.ellipse(6, 0, 8, Math.abs(3 + wingOffset / 2), -Math.PI / 4, 0, Math.PI * 2);
                ctx.stroke();

                // Body
                ctx.fillStyle = '#ef4444'; // Red for enemy
                ctx.beginPath();
                ctx.arc(0, 0, 4, 0, Math.PI * 2);
                ctx.fill();

                ctx.restore();
            };

            const drawTurret = (entity: Entity, color: string, offsetX: number = 0, state: TargetingState, isSubstrates: boolean) => {
                ctx.save();

                // CLIP to chamber to prevent laser crossover
                ctx.beginPath();
                if (isSubstrates) {
                    ctx.rect(width / 2, 0, width / 2, height);
                } else {
                    ctx.rect(0, 0, width / 2, height);
                }
                ctx.clip();

                const x = entity.position.x + offsetX;
                const y = entity.position.y;

                ctx.translate(x, y);

                // Base
                ctx.fillStyle = '#475569';
                ctx.beginPath();
                ctx.arc(0, 0, 20, 0, Math.PI * 2);
                ctx.fill();

                // Barrel
                ctx.rotate(entity.angle);
                ctx.fillStyle = color;
                ctx.fillRect(0, -5, 40, 10);

                // Laser Sight / Pulse Beam
                if (state === TargetingState.TARGETED) {
                    // Thin tracking line
                    ctx.strokeStyle = color;
                    ctx.lineWidth = 1;
                    ctx.setLineDash([5, 5]);
                    ctx.beginPath();
                    ctx.moveTo(40, 0);
                    ctx.lineTo(800, 0);
                    ctx.stroke();
                    ctx.setLineDash([]);
                } else if (state === TargetingState.SHOT) {
                    if (isSubstrates) {
                        // Minimal Kill Beam (Substrates) - Surgical, precise
                        ctx.strokeStyle = '#06b6d4'; // Cyan
                        ctx.lineWidth = 2;
                        ctx.shadowBlur = 5;
                        ctx.shadowColor = '#06b6d4';
                        ctx.beginPath();
                        ctx.moveTo(40, 0);
                        ctx.lineTo(800, 0);
                        ctx.stroke();
                        ctx.shadowBlur = 0;
                    } else {
                        // High intensity pulse beam (Traditional) - Messy, high energy
                        const gradient = ctx.createLinearGradient(40, 0, 800, 0);
                        gradient.addColorStop(0, 'rgba(239, 68, 68, 0)');
                        gradient.addColorStop(0.1, 'rgba(239, 68, 68, 1)'); // Bright red start
                        gradient.addColorStop(0.9, 'rgba(239, 68, 68, 1)'); // Bright red end
                        gradient.addColorStop(1, 'rgba(239, 68, 68, 0)');

                        ctx.strokeStyle = gradient;
                        ctx.lineWidth = 8; // Thicker
                        ctx.shadowBlur = 15; // More glow
                        ctx.shadowColor = '#ef4444';
                        ctx.beginPath();
                        ctx.moveTo(40, 0);
                        ctx.lineTo(800, 0);
                        ctx.stroke();
                        ctx.shadowBlur = 0;
                    }
                }

                ctx.restore();
            };

            const drawMacroView = (entity: Entity, targetState: TargetingState, x: number, y: number, size: number, label: string) => {
                ctx.save();

                // Border / Background
                ctx.fillStyle = '#000';
                ctx.fillRect(x, y, size, size);
                ctx.strokeStyle = '#475569';
                ctx.lineWidth = 2;
                ctx.strokeRect(x, y, size, size);

                // Clip to box
                ctx.beginPath();
                ctx.rect(x, y, size, size);
                ctx.clip();

                // Draw zoomed mosquito centered
                ctx.translate(x + size / 2, y + size / 2);
                ctx.scale(5, 5); // 5x Zoom

                // We need to draw the mosquito relative to center
                // But drawMosquito takes absolute world coords.
                // Let's just draw a local mosquito here
                const wingOffset = Math.sin(entity.wingbeatPhase || 0) * 8;
                ctx.strokeStyle = 'rgba(255, 255, 255, 0.8)';
                ctx.lineWidth = 0.5;

                // Left Wing
                ctx.beginPath();
                ctx.ellipse(-6, 0, 8, Math.abs(3 + wingOffset / 2), Math.PI / 4, 0, Math.PI * 2);
                ctx.stroke();

                // Right Wing
                ctx.beginPath();
                ctx.ellipse(6, 0, 8, Math.abs(3 + wingOffset / 2), -Math.PI / 4, 0, Math.PI * 2);
                ctx.stroke();

                // Body
                ctx.fillStyle = '#ef4444';
                ctx.beginPath();
                ctx.arc(0, 0, 4, 0, Math.PI * 2);
                ctx.fill();

                // Reticle / Lock status
                ctx.restore(); // Undo zoom/clip for text

                // Draw Reticle Overlay
                ctx.strokeStyle = targetState === TargetingState.TARGETED || targetState === TargetingState.SHOT ? '#22c55e' : '#ef4444';
                ctx.lineWidth = 2;
                ctx.strokeRect(x + 10, y + 10, size - 20, size - 20);

                // Label
                ctx.fillStyle = '#fff';
                ctx.font = '12px monospace';
                ctx.textAlign = 'left';
                ctx.fillText(label, x + 5, y + size - 5);

                // State Text
                ctx.textAlign = 'right';
                ctx.fillStyle = targetState === TargetingState.TARGETED ? '#22c55e' : '#94a3b8';
                ctx.fillText(targetState, x + size - 5, y + 15);
            };

            // --- LEFT SCREEN (Traditional) ---
            // Discrete Area 1: 0 to 500px
            // Render Swarm
            state.mosquitoesTraditional.forEach(m => drawMosquito(m, 0));
            // Draw Turret (Centered in 500px area -> x=250)
            drawTurret(state.turretTraditional, '#fbbf24', 0, state.traditionalStats.state, false); // Amber

            // Macro View (Bottom Left)
            const leftTarget = state.mosquitoesTraditional[state.traditionalStats.targetIndex] || state.mosquitoesTraditional[0];
            if (leftTarget) {
                drawMacroView(leftTarget, state.traditionalStats.state, 20, height - 170, 150, 'CAM-01 (CLOUD)');
            }

            // --- RIGHT SCREEN (Substrates) ---
            // Discrete Area 2: 500px to 1000px
            // We render the SAME swarm, but offset by 500px (width/2)
            const rightOffset = width / 2;

            // Render Swarm (Substrates Array)
            state.mosquitoesSubstrates.forEach(m => drawMosquito(m, rightOffset));

            // Draw Turret (Substrates turret position needs to be relative to its area)
            // GameLoop has turretSubstrates at x=200 (same as Traditional).
            // We apply the rightOffset to shift it to the second screen.
            drawTurret(state.turretSubstrates, '#3b82f6', rightOffset, state.substratesStats.state, true); // Blue, Offset applied

            // Macro View (Bottom Right)
            const rightTarget = state.mosquitoesSubstrates[state.substratesStats.targetIndex] || state.mosquitoesSubstrates[0];
            if (rightTarget) {
                drawMacroView(rightTarget, state.substratesStats.state, width - 170, height - 170, 150, 'CAM-02 (EDGE)');
            }

        } catch (e) {
            console.error("Canvas Rendering Error:", e);
            ctx.fillStyle = 'red';
            ctx.font = '20px sans-serif';
            ctx.fillText('Rendering Error: ' + (e as Error).message, 50, 50);
        }

    }, [state, width, height, isPlaying]);

    return <canvas ref={canvasRef} width={width} height={height} className="block" />;
};

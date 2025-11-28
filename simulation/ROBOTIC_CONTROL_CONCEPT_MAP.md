# Robotic Control Concept Map: The Egg Catching Challenge

> "In robotics, latency isn't just a delay; it's a broken egg."

This document lays the trail for the next simulation: **High-Speed Robotic Manipulation**.
We move from *shooting mosquitoes* and *inspecting packets* to the delicate, high-stakes world of *catching fragile objects*.

## The Core Metaphor

| Simulation Entity | Robotic Equivalent | Description |
| :--- | :--- | :--- |
| **Mosquito / Packet** | **The Egg** | A fragile object subject to physics (gravity). It breaks if mishandled or dropped. |
| **Turret / Appliance** | **Robotic Hand/Gripper** | The actuator that must position itself perfectly to intercept the object. |
| **Laser Shot** | **Soft Catch / Grasp** | The action is no longer destructive (destroy/drop) but constructive (decelerate/catch). |
| **Latency** | **Proprioceptive Lag** | The delay between "seeing" the egg and moving the hand. (Cloud = Video feed lag, Edge = Haptic reflex). |
| **Wingbeat / Jitter** | **Surface Texture / Slip** | High-frequency signals. Detecting "slip" requires kHz-level sampling (haptics). |
| **3D Evasion** | **Unpredictable Bounce** | If the egg hits an obstacle or is thrown with spin, its trajectory changes rapidly. |

## The "Reflex" Gap

In this simulation, we demonstrate why **Cloud Control** fails at physical tasks.

### Traditional (Cloud / Vision-Only)
*   **The Loop**: Camera -> Encode -> Network -> Cloud AI -> Network -> Motor Controller.
*   **The Failure**: By the time the Cloud AI says "Close Gripper," the egg has already slipped through or smashed against the palm.
*   **Visual**: The hand moves jerkily. It tries to predict where the egg will be, but misses the micro-adjustments needed for a soft catch. The egg cracks.

### Substrates (Edge / Sensor Fusion)
*   **The Loop**: Tactile Sensors (Fingertips) -> Local Spiking Neural Net -> Motor Reflex.
*   **The Success**: The hand "feels" the egg touch a fingertip and *instantly* adjusts grip force to secure it without crushing it.
*   **Visual**: Fluid, organic motion. The catch looks like a magic trick—perfectly timed deceleration.

## Implementation Guide

To build this on the existing engine:

### 1. Physics Engine (`EggGameLoop.ts`)
*   **Gravity**: Unlike mosquitoes (flying) or packets (linear flow), Eggs fall with acceleration ($g = 9.8m/s^2$).
*   **Trajectory**: Use simple projectile motion equations.
    ```typescript
    egg.velocity.y += GRAVITY * dt;
    egg.position += egg.velocity * dt;
    ```
*   **Collision**: The "Hit" check is now a "Catch" check.
    *   Instead of `angleDiff < Threshold`, check `distance(hand, egg) < GRIP_RADIUS` AND `relativeVelocity < SAFE_CATCH_SPEED`.

### 2. The "Soft Catch" Mechanic
*   **Traditional**: Can only track position. It closes the gripper at $T_{predicted}$. If latency is high, it closes too early (air) or too late (egg hits palm -> crack).
*   **Substrates**: Has access to "Haptic" data (simulated).
    *   *Trigger*: When `distance < TOUCH_THRESHOLD`, engage "Reflex Mode".
    *   *Action*: Match hand velocity to egg velocity (cushioning), then close gripper.

### 3. Visuals (`RoboticSimulationCanvas.tsx`)
*   **The Egg**: Oval shape. Color indicates state (White = Falling, Green = Caught, Yellow = Cracked).
*   **The Hand**: A simple claw or cup shape.
*   **Impact**: If `Traditional` fails, show a "Splat" visual (particle effect).

## Key Architectural Lesson (From DPI Experience)
> **Do Not Share State.**

When implementing the "Traditional" vs "Substrates" split:
1.  **Spawn Distinct Objects**: Create `eggTraditional` and `eggSubstrates` as completely separate objects in memory.
2.  **Decouple Physics**: Ensure that if the Traditional egg smashes, the Substrates egg continues falling/catching independently.
3.  **Force "Wire Speed"**: For the Substrates simulation, ensure the control loop runs at the equivalent of "1kHz" (update every frame without artificial delay), while the Traditional loop suffers from `config.traditionalLatency`.

## Why This Matters
This simulation will demonstrate **Sensor Fusion**.
*   **Vision** (Long range, slow) gets the hand *near* the egg.
*   **Touch** (Short range, fast) performs the *catch*.
Substrates enables that tight, local tactile loop that the Cloud simply cannot touch.

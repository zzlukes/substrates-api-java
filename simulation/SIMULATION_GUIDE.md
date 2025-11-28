# Simulation Developer Guide: The Golden Pathway

Welcome, traveler. You stand on the shoulders of giants.

This guide distills the architectural patterns, physics secrets, and design philosophies learned from building the **Mosquito Defense**, **Network DPI**, and **Robotic Control** simulations. Use this as your map to build the next generation of Substrates demos.

## 1. The Core Philosophy: "The Comparative Model"

Every simulation in this project tells a specific story: **Traditional vs. Substrates**.

*   **Traditional**: Represents the "Old World". High latency (Cloud), centralized processing, polling, rigid logic. It should function, but fail under stress (latency, load).
*   **Substrates**: Represents the "New World". Low latency (Edge), distributed intelligence, event-driven reflexes, sensor fusion. It should feel robust, snappy, and "magical".

**Golden Rule**: *Do not share state.* To ensure a fair comparison, run two independent simulations side-by-side. They may share a "world" (e.g., falling eggs), but their *perception* and *reaction* must be separate.

## 2. Architecture: The Loop-State-Canvas Pattern

We use a strict separation of concerns to keep simulations performant and maintainable.

### A. The State (`types.ts`)
Define the universe in pure data.
*   **Config**: Parameters the user can tweak (Latency, Speed, Capacity).
*   **State**: The mutable world (Positions, Velocities, Scores).
*   **Entities**: The things in the world (Mosquitoes, Packets, Eggs).

### B. The Engine (`GameLoop.ts`)
The brain. It runs on a `requestAnimationFrame` loop but calculates logic based on `deltaTime`.
*   **`update(dt)`**: The main heartbeat.
*   **Physics**: Apply gravity, velocity, collisions.
*   **Logic**: Run the "Traditional" and "Substrates" control logic separately.
*   **Latency Simulation**: *Crucial Pattern*.
    *   *Traditional*: Use a "Delayed Perception" buffer. Don't act on where the target *is*, act on where it *was* `N` ms ago.
    *   *Substrates*: Act on the current state (or a very small delay).

### C. The Renderer (`Canvas.tsx`)
The eyes. A pure React component that receives the `State` and draws it.
*   **Canvas API**: Use `requestAnimationFrame` to draw. It's faster than DOM nodes for many moving parts.
*   **Visual Language**:
    *   *Traditional*: Amber/Orange. Show the "ghost" of where it thinks the target is to visualize latency.
    *   *Substrates*: Cyan/Blue. Show precise, locked-on visuals.

## 3. Key Patterns & "Secret Sauce"

### Pattern 1: The Latency Buffer (Mosquito Defense)
To simulate cloud latency convincingly:
1.  Store the history of target positions.
2.  In the Traditional loop, look up the position from `Now - Latency`.
3.  Render a "Ghost Target" at that delayed position so the user *sees* what the bot sees.
4.  **Lesson**: If the bot shoots at the ghost, it will miss the real target. This visually explains "why" it failed.

### Pattern 2: The Throughput Pipe (Network DPI)
To visualize processing power:
1.  **Traditional**: Add a "processing delay" (sleep/wait) for each item. This creates a bottleneck/queue.
2.  **Substrates**: Process instantly (wire-speed).
3.  **Lesson**: Visualizing the *queue* building up is more powerful than just a number.

### Pattern 3: Control Loops & Compliance (Robotic Control)
For physical interaction:
1.  **Prediction**: Simple tracking fails with latency. Implement `TargetPos + (Velocity * Latency)` prediction.
2.  **P-Controller**: Don't just teleport. Use `Velocity = (Target - Current) * Gain`.
3.  **Passive Compliance**: *The Secret Weapon*. Real robots aren't rigid.
    *   If `ImpactVelocity` is too high -> Crack/Fail.
    *   Multiply `ImpactVelocity` by a `ComplianceFactor` (e.g., 0.3) to simulate a soft/rubber gripper.
    *   **Lesson**: "Softness" makes the simulation forgiving and realistic.

## 4. Tuning for "The Balance"

A simulation is a game design problem.
1.  **Make Traditional Fail**: Find the breaking point (e.g., 100ms latency).
2.  **Make Substrates Shine**: Ensure it works at that breaking point.
3.  **Don't Rig It**: Traditional *should* work if the conditions are perfect (0ms latency). If it fails even then, your physics are broken.
4.  **Iterate**: Use the `Controls` to tweak variables live. If you can't catch an egg, increase the `GripThreshold` or `SafeSpeed`.

## 5. Checklist for New Simulations

- [ ] **Concept**: What is the "Old vs. New" metaphor?
- [ ] **Types**: Define `State` and `Config`.
- [ ] **Loop**: Create the `GameLoop` class.
- [ ] **Canvas**: Build the renderer.
- [ ] **Controls**: Add sliders for Latency and Difficulty.
- [ ] **Tuning**: Verify Traditional works at 0ms, fails at 100ms. Substrates works at 100ms (if edge-based) or handles high load.

*Go forth and build.*

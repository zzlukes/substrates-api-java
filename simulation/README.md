# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) (or [oxc](https://oxc.rs) when used in [rolldown-vite](https://vite.dev/guide/rolldown)) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type-aware lint rules:

```js
export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...

      // Remove tseslint.configs.recommended and replace with this
      tseslint.configs.recommendedTypeChecked,
      // Alternatively, use this for stricter rules
      tseslint.configs.strictTypeChecked,
      // Optionally, add this for stylistic rules
      tseslint.configs.stylisticTypeChecked,

      // Other configs...
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```

You can also install [eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x) and [eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom) for React-specific lint rules:

```js
// eslint.config.js
import reactX from 'eslint-plugin-react-x'
import reactDom from 'eslint-plugin-react-dom'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...
      // Enable lint rules for React
      reactX.configs['recommended-typescript'],
      // Enable lint rules for React DOM
      reactDom.configs.recommended,
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```

## Simulation Context: The Edge Advantage

This simulation demonstrates the critical difference between **Traditional Cloud Control** and **Edge Native Adaptive Control** (Substrates) in a high-speed, real-world scenario.

### The Challenge: "Faster than a Wingbeat"
Imagine a room-sized chamber (4 meters wide) containing mosquitoes. To neutralize a mosquito with a laser, you must:
1.  **Detect** it using a microphone array or camera.
2.  **Track** its erratic flight path.
3.  **Target** it by steering a laser beam using galvanometer mirrors.
4.  **Fire** a precise pulse.

The catch? A mosquito beats its wings **600 times per second (600Hz)**. That means one full wingbeat takes just **1.6 milliseconds**.

### The Physics of Latency
*   **Cloud Control (Left Screen)**: In a traditional cloud architecture, sensor data travels to a server, is processed, and a command is sent back. This round trip often takes **100ms or more**. By the time the "Fire" command arrives, the mosquito has moved and flapped its wings **60 times**. The laser fires at where the mosquito *was*, not where it *is*. The result? Missed shots and wasted energy.
*   **Edge Native Control (Right Screen)**: Substrates enables a local control loop running directly on the edge device with **<1ms latency**. This is "faster than a wingbeat." The system can track the exact phase of the wings and adjust the galvanometer mirrors in real-time.

### Realism & Precision
This scenario is based on real-world physics:
*   **Galvanometer Mirrors**: These devices use electromagnetic drives to steer laser beams with micro-radian precision. They are energy-efficient (~20W) and fast enough to track insects.
*   **LIDAR Tracking**: High-speed LIDAR (Light Detection and Ranging) provides the depth and position data needed for 3D tracking.
*   **Energy Efficiency**: By firing only when a hit is guaranteed, the Edge system saves massive amounts of energy compared to the "spray and pray" approach of the Cloud system.

In this simulation, you can slow down time (using the **Time Scale** slider) to see the individual wingbeats and observe how the Edge system locks onto the target while the Cloud system lags behind.

## Verification: The Nanosecond Scale

The core advantage of Substrates is its ability to operate at the **nanosecond scale**, enabling control loops that are orders of magnitude faster than traditional network-based approaches.

The following table compares the signal emission latency of Substrates (verified in `BENCHMARKS.md`) against standard network interactions:

| Interaction Type | Latency | Time Scale |
| :--- | :--- | :--- |
| **Substrates Signal Emission** | **~8 ns** | **Instant** |
| Java Method Call | ~1 ns | Instant |
| Local Network (LAN) | ~1,000,000 ns (1 ms) | 125,000x slower |
| Cloud Round Trip (WAN) | ~100,000,000 ns (100 ms) | 12,500,000x slower |

This explains why the Edge system can track a 600Hz wingbeat (1,666,666 ns period) with ease, while a Cloud system is hopelessly behind.

## Physics & Realism

This simulation is grounded in real-world physics to ensure the comparison is valid.

### The Scale
*   **Chamber Width**: 4 meters
*   **Canvas Width**: 500 pixels
*   **Scale**: `1 pixel = 8 mm` (0.008 m)

### Mosquito Dynamics
*   **Speed**: The simulation uses `0.3 px/ms`, which translates to **2.4 m/s**. This is realistic for a fast-moving mosquito (typically 1.5 - 2.5 m/s).
*   **Evasion**: Mosquitoes move in 3D space (X, Y, and Z/Height). The simulation tracks all three dimensions, meaning the laser must align in both azimuth (angle) and elevation (height) to score a hit.

### The Tipping Point (~17ms)
Based on the physics constants:
*   **Hit Threshold**: `0.02 radians` (~1.15 degrees). At average range (2m), this is a **4cm** target zone.
*   **Mosquito Speed**: `2.4 m/s`.
*   **Result**: A mosquito travels **4cm** (the entire width of the kill zone) in **~17ms**.

Therefore, if the total system latency exceeds **17ms**, the system is physically incapable of reliably hitting the target, as the mosquito will have moved out of the beam path by the time the laser fires. This is the "Tipping Point" where eradication fails and the chamber is overrun.

### Speed of Sound
The simulation accounts for the speed of sound (`343 m/s`).
*   At 2 meters, sound takes **~6ms** to reach the microphone array.
*   This physical delay is unavoidable and applies to both Edge and Cloud systems, but it eats into the precious 17ms budget.

### 3D Evasion (The "Z-Axis" Factor)
Mosquitoes are not 2D sprites; they move in 3D space.
*   **The Mechanic**: Mosquitoes bob up and down (Z-axis) while flying.
*   **The Challenge**: The laser turret must track **Azimuth** (Left/Right) AND **Elevation** (Up/Down).
*   **The Impact**: High latency causes the system to aim at where the mosquito *was* in 3D space. If the mosquito has changed altitude, the shot misses "over" or "under" the target, even if the 2D angle looks correct. This exponentially increases the difficulty for the Cloud system.


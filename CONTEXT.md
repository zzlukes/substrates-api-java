# Project Context: Substrates API Java

## Overview
This repository contains the Java API for Substrates, a reactive programming framework. The primary focus of recent work has been implementing a reference "Simple" SDK (`io.humainary.substrates.sdk`) to satisfy the SPI requirements and allow the "Kitchen Sink" demo to run.

## Current State
- **API Module**: `api/` contains the core interfaces (`io.humainary.substrates.api`) and the reference implementation (`io.humainary.substrates.sdk`).
- **Implementation**: A complete, in-memory implementation of `Cortex`, `Circuit`, `Channel`, `Conduit`, etc., has been added in the `sdk` package.
- **Demo**: The `KitchenSinkDemo` (renamed to `KitchenSinkDemoTest`) verifies the core functionality.

## Key Components
- **`SimpleCortex`**: The main entry point.
- **`SimpleCircuit`**: Handles event execution.
- **`SimpleChannel`**: Manages subscriptions and routing.
- **`SimpleReservoir`**: Captures events for verification.

## Running the Demo
To run the verification demo, use the following Maven command:

```bash
./mvnw -pl api clean test -Dtest=KitchenSinkDemoTest -Dmaven.test.skip=false -Dio.humainary.substrates.spi.provider=io.humainary.substrates.sdk.SimpleCortexProvider
```

## Documentation
- **`docs/PARADIGM.md`**: **READ THIS FIRST**. Explains the "New Paradigm" of computing (Semiosphere, Meaning-Making) that this project implements.
- **`docs/WALKTHROUGH.md`**: Detailed walkthrough of the implementation and verification steps.
- **`api/src/main/java/io/humainary/substrates/sdk/`**: Source code for the reference implementation.

## The Humainary Paradigm
This project implements a "Semiosphere" approach to computing, shifting focus from data collection to meaning-making.
- **Substrates**: The deterministic, event-driven fabric.
- **Circuits**: Single-threaded execution contexts (simplified in this SDK).
- **Goal**: Neural-like computational networks and situational intelligence.
See [docs/PARADIGM.md](docs/PARADIGM.md) for details.

## Extending the Project
To experiment with new features without breaking the core:
- **SPI**: Switch providers at runtime.
- **Composers**: Create custom percepts.
- **Decorators**: Wrap existing components.
See [docs/EXTENDING.md](docs/EXTENDING.md) for a detailed guide.

## Future Work
- The current implementation is a "Simple" reference. Future work may involve optimizing performance, adding persistence, or implementing distributed capabilities.
- The `SimpleCortex` currently throws `UnsupportedOperationException` for `current()`.

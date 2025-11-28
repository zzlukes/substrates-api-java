# Substrates Demo Walkthrough

This document outlines the steps taken to implement the missing Substrates SDK components and verify the "Kitchen Sink" demo.

## Implementation Details

We implemented a simple, in-memory provider for the Substrates API (`io.humainary.substrates.sdk`).

### Core Components

- **`SimpleCortex`**: The central entry point, implementing the `Cortex` interface. It manages the creation of circuits, channels, and other components.
- **`SimpleCircuit`**: Implements the `Circuit` interface, managing the execution of events.
- **`SimpleChannel`**: Implements the `Channel` interface, handling event routing to subscribers.
- **`SimpleConduit`**: Implements the `Conduit` interface, acting as a source of events.
- **`SimpleReservoir`**: Implements the `Reservoir` interface, capturing emissions for observation.
- **`SimpleName`**: Implements `Name` and `Extent` interfaces for hierarchical naming.
- **`SimplePipe`**: A concrete implementation of `Pipe` to handle event emissions.
- **`SimpleSubscriber`**: A concrete implementation of `Subscriber` to handle callbacks.

### Key Fixes

1.  **Service Provider Registration**: Created `META-INF/services/io.humainary.substrates.spi.CortexProvider` to register `SimpleCortexProvider`.
2.  **Compilation Errors**:
    - Resolved `cannot find symbol` for `ContextualReceptor` (removed as it was unused/undefined).
    - Fixed method signatures for `Cortex.reservoir` and `Receptor.receive`.
    - Implemented missing methods in `Circuit`, `Channel`, `Conduit`, and `Name` interfaces.
    - Addressed type mismatches and generic type issues.
    - Replaced lambda implementations of `Pipe` with `SimplePipe` where necessary.
3.  **Maven Configuration**:
    - Enabled tests by removing `maven.test.skip=true`.
    - Configured `maven-surefire-plugin` to use the `SimpleCortexProvider` via system property.

## Verification

We verified the implementation by running the `KitchenSinkDemo` as a JUnit test.

### Test Execution

Command:
```bash
./mvnw -pl api clean test -Dtest=KitchenSinkDemoTest -Dmaven.test.skip=false -Dio.humainary.substrates.spi.provider=io.humainary.substrates.sdk.SimpleCortexProvider
```

### Results

The test passed successfully, producing the expected simulation output:

```
=== Starting Kitchen Sink Demo ===
>>> Injecting initial stimulus into Neuron A
[neuron.A] Received spike: 1
[neuron.A] Firing synapse to B
[neuron.B] Received spike: 2
[neuron.B] Firing synapse to C
[neuron.C] Received spike: 3
[neuron.C] Firing feedback synapse to A
[neuron.A] Received spike: 4
[neuron.A] Firing synapse to B
=== Simulation Complete ===
>>> Reservoir Observations:
Observed: neuron.A emitted 1
Observed: neuron.B emitted 2
```

This confirms that:
- The `SimpleCortex` provider is correctly loaded.
- Circuits and channels are correctly routing events.
- Feedback loops (A -> B -> C -> A) are functioning.
- Reservoirs are correctly capturing data.

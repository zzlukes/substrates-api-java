# Extending Substrates: A Guide to Experimental Evolution

The Substrates API is designed to be extended and evolved without breaking the core contract. This document outlines the recommended strategies for adding experimental features or alternative implementations.

## 1. The SPI Mechanism (The "Big Hammer")

The most powerful way to experiment is to provide a completely new implementation of the `Cortex` interface. The API uses a Service Provider Interface (SPI) pattern to load the implementation at runtime.

### How to use it:
1.  **Create a new package**: e.g., `io.humainary.substrates.experimental`.
2.  **Implement `CortexProvider`**: Create a class extending `io.humainary.substrates.spi.CortexProvider`.
3.  **Implement Core Interfaces**: Implement `Cortex`, `Circuit`, `Channel`, etc. You can inherit from or wrap the `Simple` implementation to save time.
4.  **Switch at Runtime**:
    ```bash
    -Dio.humainary.substrates.spi.provider=io.humainary.substrates.experimental.MyExperimentalProvider
    ```

### Use Cases:
- **Threading Models**: Test Virtual Threads (Project Loom) vs. Platform Threads vs. ForkJoinPool.
- **Persistence**: Implement a `Cortex` that persists state to disk or a database.
- **Distribution**: Create a `Cortex` that networks circuits across machines.

## 2. Custom Composers (The "Soft Touch")

If you want to create new types of sensors, actuators, or "Percepts" without changing the engine, use the `Composer` interface.

### How to use it:
`Circuit.conduit(Composer)` allows you to pass a factory that transforms a raw `Channel` into a domain-specific object.

```java
public class MyExperimentalSensor implements Percept {
    private final Pipe<String> pipe;
    // ...
}

Composer<String, MyExperimentalSensor> myComposer = channel -> new MyExperimentalSensor(channel.pipe());
Conduit<MyExperimentalSensor, String> conduit = cortex.circuit().conduit(myComposer);
```

### Use Cases:
- **Domain-Specific Instruments**: Create `Gauge`, `Histogram`, or `NeuralSpike` percepts.
- **Protocol Adapters**: Create percepts that serialize/deserialize data automatically.

## 3. Decorators (The "Wrapper")

To add cross-cutting concerns like logging, chaos engineering, or metrics to an existing implementation, use the Decorator pattern.

### How to use it:
Create a `CortexProvider` that delegates to the `SimpleCortexProvider` but wraps the returned `Cortex` in a proxy.

```java
public class ChaosCortex implements Cortex {
    private final Cortex delegate;
    
    // Intercept circuit creation to inject faults
    public Circuit circuit() {
        return new ChaosCircuit(delegate.circuit());
    }
}
```

### Use Cases:
- **Chaos Engineering**: Randomly drop emissions or delay processing to test resilience.
- **Observability**: Trace every method call without modifying the core code.

## Summary

| Goal | Recommended Strategy | Risk |
| :--- | :--- | :--- |
| **New Engine Mechanics** | SPI Provider | Low (Isolated) |
| **New Sensors/Data Types** | Custom Composers | Low (Additive) |
| **System-wide Behavior** | Decorators | Medium (Interception) |

**Recommendation**: Start by creating a new package `io.humainary.substrates.experimental` and implementing a `Decorator` around the `Simple` SDK. This allows you to selectively override behaviors while falling back to the working implementation.

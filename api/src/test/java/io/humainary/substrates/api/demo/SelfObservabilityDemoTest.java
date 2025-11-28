package io.humainary.substrates.api.demo;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.api.Substrates.Circuit;
import io.humainary.substrates.api.Substrates.Conduit;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Pipe;
import io.humainary.substrates.api.Substrates.Subscriber;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static io.humainary.substrates.api.Substrates.Composer;

/**
 * A demonstration of "Self-Observability" and "Adaptive Control" using
 * Substrates.
 * <p>
 * Scenario: A "Worker" component processes tasks and emits its current load.
 * A "Controller" component (the internalized rules engine) observes the load
 * and issues control signals to the Worker to adjust its processing rate.
 * <p>
 * This demonstrates:
 * 1. Self-Observability: The system monitors its own state via the Conduit.
 * 2. Adaptive Control: The system adjusts its behavior based on observations.
 * 3. Nanosecond Performance: The feedback loop operates with minimal latency.
 * 4. Authorization: Subject-based identity for policy enforcement.
 */
public class SelfObservabilityDemoTest {

    private final Substrates.Cortex cortex = Substrates.cortex();

    @Test
    public void runSelfObservabilityDemo() {
        System.out.println("=== Starting Self-Observability & Adaptive Control Demo ===");

        final Circuit circuit = cortex.circuit(cortex.name("demo.circuit"));

        try {
            // 1. The Conduit: The nervous system carrying signals (Load and Control)
            // We use Double for simplicity: > 0 is Load, < 0 is Control adjustment
            final Conduit<Pipe<Double>, Double> conduit = circuit.conduit(
                    cortex.name("cortex.control"),
                    Composer.pipe(Double.class));

            // 2. Define Identities (Subjects)
            final Name workerName = cortex.name("system.worker");
            final Name controllerName = cortex.name("system.controller");

            // 3. Create Pipes (Axons)
            final Pipe<Double> workerAxon = conduit.percept(workerName);
            final Pipe<Double> controllerAxon = conduit.percept(controllerName);

            // Metrics for benchmarking
            final AtomicLong loops = new AtomicLong(0);
            final long maxLoops = 1_000_000;
            final long startTime = System.nanoTime();

            // 4. The Rules Engine (Subscriber)
            final Subscriber<Double> rulesEngine = cortex.subscriber(
                    cortex.name("rules.engine"),
                    (subject, registrar) -> {

                        // Authorization Check (Mock SpiceDB integration)
                        if (!authorize(subject)) {
                            System.out.println("Access Denied for: " + subject.name());
                            return;
                        }

                        final Name name = subject.name();

                        if (name.equals(workerName)) {
                            // CONTROLLER LOGIC: Observing the Worker
                            registrar.register(cortex.<Double>pipe(load -> {
                                // "Self-Observability": We see the worker's load
                                // "Rules Engine": Simple threshold logic
                                if (load > 80.0) {
                                    // "Adaptive Control": Issue command to reduce rate
                                    // Emitting negative value as control signal
                                    controllerAxon.emit(-5.0);
                                } else if (load < 20.0) {
                                    // "Adaptive Control": Issue command to increase rate
                                    controllerAxon.emit(5.0);
                                } else {
                                    // Stable state, keep working
                                    // For the sake of the loop benchmark, we nudge it slightly
                                    controllerAxon.emit(0.1);
                                }
                            }));
                        } else if (name.equals(controllerName)) {
                            // WORKER LOGIC: Listening to the Controller
                            registrar.register(cortex.<Double>pipe(adjustment -> {
                                // Apply adjustment
                                double currentLoad = 50.0 + adjustment; // Simplified state

                                // Emit new state (closing the loop)
                                if (loops.incrementAndGet() < maxLoops) {
                                    workerAxon.emit(currentLoad);
                                }
                            }));
                        }
                    });

            // 5. Wire it up
            conduit.subscribe(rulesEngine);

            // 6. Kickstart
            System.out.println(">>> Injecting initial load signal from Worker");
            workerAxon.emit(50.0);

            // 7. Wait for completion
            circuit.await();

            long endTime = System.nanoTime();
            long durationNs = endTime - startTime;
            double durationMs = durationNs / 1_000_000.0;
            double nsPerOp = (double) durationNs / maxLoops;

            System.out.println("\n=== Demo Complete ===");
            System.out.printf("Total Loops: %d%n", maxLoops);
            System.out.printf("Total Time: %.2f ms%n", durationMs);
            System.out.printf("Latency per Loop: %.2f ns%n", nsPerOp);
            System.out.println(
                    "(Includes: Worker Emit -> Context Switch -> Controller Process -> Controller Emit -> Context Switch -> Worker Process)");

            printPerformanceComparison(nsPerOp);

        } finally {
            circuit.close();
        }
    }

    private boolean authorize(Substrates.Subject<?> subject) {
        // Mock Authorization Logic
        // In a real system, this would query SpiceDB using subject.id()
        // e.g., spiceDb.check(subject.id(), "can_emit_to", "control_plane")
        return true;
    }

    private void printPerformanceComparison(double nsPerOp) {
        System.out.println("\n=== Performance Context ===");
        System.out.printf("Substrates (In-Memory):     ~%.2f ns%n", nsPerOp);
        System.out.println("Local IPC (Pipes/Socket):   ~10,000 ns (10 µs)");
        System.out.println("LAN (Redis/MsgQueue):       ~500,000 ns (0.5 ms)");
        System.out.println("Cloud (HTTP/Gateway):       ~50,000,000 ns (50 ms)");
        System.out.println(
                "\nSubstrates is orders of magnitude faster because it keeps the 'control loop' within the same memory space and thread context, avoiding OS context switches and network serialization.");
    }
}

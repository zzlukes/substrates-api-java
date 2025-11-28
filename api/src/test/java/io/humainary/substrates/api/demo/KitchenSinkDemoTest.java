package io.humainary.substrates.api.demo;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.api.Substrates.Circuit;
import io.humainary.substrates.api.Substrates.Conduit;
import io.humainary.substrates.api.Substrates.Cortex;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Pipe;
import io.humainary.substrates.api.Substrates.Reservoir;
import io.humainary.substrates.api.Substrates.Subscriber;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.Composer;

/**
 * A "Kitchen Sink" demo showcasing key Substrates features:
 * 1. Neural-like signal propagation
 * 2. Feedback loops (cyclic topology)
 * 3. Deterministic ordering
 * 4. Dynamic topology
 */
public class KitchenSinkDemoTest {

    private final Cortex cortex = Substrates.cortex();

    @Test
    public void runSimulation() {
        System.out.println("=== Starting Kitchen Sink Demo ===");

        // 1. Create a Circuit (The processing engine)
        final Circuit circuit = cortex.circuit(cortex.name("demo.circuit"));

        try {
            // 2. Create a Conduit (The nervous system)
            // Conduit<PerceptType, EmissionType>
            final Conduit<Pipe<Integer>, Integer> conduit = circuit.conduit(
                    cortex.name("cortex.visual"),
                    Composer.pipe(Integer.class));

            // 3. Create a Reservoir to observe the output
            final Reservoir<Integer> reservoir = cortex.reservoir(conduit);

            // 4. Define Neuron Names
            final Name nameA = cortex.name("neuron.A");
            final Name nameB = cortex.name("neuron.B");
            final Name nameC = cortex.name("neuron.C");

            // 5. Create Pipes for Emission (Axons)
            final Pipe<Integer> axonA = conduit.percept(nameA);
            final Pipe<Integer> axonB = conduit.percept(nameB);
            final Pipe<Integer> axonC = conduit.percept(nameC);

            // 6. Define Subscriber to wire up the logic (Synapses)
            // The subscriber discovers channels and attaches processing logic (Receptors)
            final Subscriber<Integer> synapseBuilder = cortex.subscriber(
                    cortex.name("synapse.builder"),
                    (subject, registrar) -> {

                        final Name name = subject.name();

                        if (name.equals(nameA)) {
                            // Neuron A Logic: Fires to B if signal < 10
                            registrar.register(cortex.<Integer>pipe(signal -> {
                                System.out.printf("[%s] Received spike: %d%n", name, signal);
                                if (signal < 10) {
                                    System.out.printf("[%s] Firing synapse to B%n", name);
                                    axonB.emit(signal + 1);
                                } else {
                                    System.out.printf("[%s] Threshold reached. Inhibiting.%n", name);
                                }
                            }));
                        } else if (name.equals(nameB)) {
                            // Neuron B Logic: Fires to C
                            registrar.register(cortex.<Integer>pipe(signal -> {
                                System.out.printf("[%s] Received spike: %d%n", name, signal);
                                System.out.printf("[%s] Firing synapse to C%n", name);
                                axonC.emit(signal + 1);
                            }));
                        } else if (name.equals(nameC)) {
                            // Neuron C Logic: Fires back to A (Feedback Loop)
                            registrar.register(cortex.<Integer>pipe(signal -> {
                                System.out.printf("[%s] Received spike: %d%n", name, signal);
                                System.out.printf("[%s] Firing feedback synapse to A%n", name);
                                axonA.emit(signal + 1);
                            }));
                        }
                    });

            // 7. Subscribe to the conduit
            conduit.subscribe(synapseBuilder);

            // 8. Kickstart the simulation
            System.out.println("\n>>> Injecting initial stimulus into Neuron A");
            axonA.emit(1);

            // 9. Wait for the circuit to process all cascading signals
            circuit.await();

            System.out.println("\n=== Simulation Complete ===");

            // 10. Verify observations
            System.out.println("\n>>> Reservoir Observations:");
            reservoir.drain().forEach(capture -> System.out.printf("Observed: %s emitted %d%n",
                    capture.subject().name(), capture.emission()));

        } finally {
            circuit.close();
        }
    }
}

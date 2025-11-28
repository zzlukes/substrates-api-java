package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Cell;
import io.humainary.substrates.api.Substrates.Circuit;
import io.humainary.substrates.api.Substrates.Composer;
import io.humainary.substrates.api.Substrates.Conduit;
import io.humainary.substrates.api.Substrates.Flow;
import io.humainary.substrates.api.Substrates.Id;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Percept;
import io.humainary.substrates.api.Substrates.Pipe;
import io.humainary.substrates.api.Substrates.State;
import io.humainary.substrates.api.Substrates.Subject;
import io.humainary.substrates.api.Substrates.Subscriber;
import io.humainary.substrates.api.Substrates.Subscription;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SimpleCircuit implements Circuit {

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "circuit-thread");
        t.setDaemon(true);
        return t;
    });

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Name name = new SimpleName("circuit");

    @Override
    public void await() {
        if (Thread.currentThread().getName().equals("circuit-thread")) {
            throw new IllegalStateException("Cannot call await() from circuit thread");
        }
        try {
            executor.submit(() -> {
            }).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <P extends Percept, E> Conduit<P, E> conduit(Name name, Composer<E, ? extends P> composer) {
        return new SimpleConduit<>(name, composer, this);
    }

    @Override
    public <P extends Percept, E> Conduit<P, E> conduit(Name name, Composer<E, ? extends P> composer,
            Consumer<Flow<E>> configurer) {
        return new SimpleConduit<>(name, composer, this);
    }

    @Override
    public <I, E> Cell<I, E> cell(Name name, Composer<E, Pipe<I>> ingress,
            Composer<E, Pipe<E>> egress, Pipe<? super E> pipe) {
        throw new UnsupportedOperationException("Cells not implemented in this simple version yet");
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            executor.shutdown();
        }
    }

    @Override
    public Subscription subscribe(Subscriber<State> subscriber) {
        return new Subscription() {
            @Override
            public void close() {
            }

            @Override
            public Subject<Subscription> subject() {
                return new Subject<Subscription>() {
                    @Override
                    public Id id() {
                        return new Id() {
                        };
                    }

                    @Override
                    public Name name() {
                        return new SimpleName("subscription");
                    }

                    @Override
                    public State state() {
                        return new SimpleState();
                    }

                    @Override
                    public Class<Subscription> type() {
                        return Subscription.class;
                    }

                    @Override
                    public String toString() {
                        return "SimpleSubscription";
                    }
                };
            }
        };
    }

    @Override
    public <E> Pipe<E> pipe(Pipe<? super E> pipe) {
        return new SimplePipe<>(emission -> execute(() -> pipe.emit(emission)));
    }

    @Override
    public <E> Pipe<E> pipe(Pipe<? super E> pipe, Consumer<Flow<E>> configurer) {
        // Ignoring flow config for now
        return new SimplePipe<>(emission -> execute(() -> pipe.emit(emission)));
    }

    @Override
    public Subject<Circuit> subject() {
        return new Subject<Circuit>() {
            @Override
            public Id id() {
                return new Id() {
                };
            }

            @Override
            public Name name() {
                return name;
            }

            @Override
            public State state() {
                return new SimpleState();
            }

            @Override
            public Class<Circuit> type() {
                return Circuit.class;
            }

            @Override
            public String toString() {
                return "SimpleCircuit[" + name + "]";
            }
        };
    }

    // Internal method to execute tasks on the circuit thread
    void execute(Runnable task) {
        if (!closed.get()) {
            executor.execute(task);
        }
    }
}

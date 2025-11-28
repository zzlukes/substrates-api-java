package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Channel;
import io.humainary.substrates.api.Substrates.Flow;
import io.humainary.substrates.api.Substrates.Id;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Pipe;
import io.humainary.substrates.api.Substrates.Receptor;
import io.humainary.substrates.api.Substrates.Registrar;
import io.humainary.substrates.api.Substrates.State;
import io.humainary.substrates.api.Substrates.Subject;
import io.humainary.substrates.api.Substrates.Subscriber;
import io.humainary.substrates.api.Substrates.Subscription;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class SimpleChannel<E> implements Channel<E> {

    private final Name name;
    private final SimpleCircuit circuit;
    private final List<Pipe<E>> receptors = new CopyOnWriteArrayList<>();
    private final Subject<Channel<E>> subject;

    public SimpleChannel(Name name, SimpleCircuit circuit) {
        this.name = name;
        this.circuit = circuit;
        this.subject = new Subject<>() {
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
            public Class<Channel<E>> type() {
                return (Class) Channel.class;
            }

            @Override
            public String toString() {
                return "SimpleChannel[" + name + "]";
            }
        };
    }

    public Subscription subscribe(Subscriber<E> subscriber) {
        addSubscriber(subscriber);
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

    void addSubscriber(Subscriber<E> subscriber) {
        if (subscriber instanceof SimpleSubscriber) {
            SimpleSubscriber<E> simpleSubscriber = (SimpleSubscriber<E>) subscriber;
            Registrar<E> registrar = new Registrar<E>() {
                @Override
                public void register(Pipe<? super E> pipe) {
                    receptors.add(new SimplePipe<>(pipe::emit));
                }

                @Override
                public void register(Receptor<? super E> receptor) {
                    receptors.add(new SimplePipe<>(receptor::receive));
                }
            };
            simpleSubscriber.accept(subject, registrar);
        }
    }

    public Pipe<E> pipe() {
        return new SimplePipe<>(value -> circuit.execute(() -> {
            for (Pipe<E> receptor : receptors) {
                receptor.emit(value);
            }
        }));
    }

    public Pipe<E> pipe(Consumer<Flow<E>> configurer) {
        return pipe();
    }

    @Override
    public Subject<Channel<E>> subject() {
        return subject;
    }
}

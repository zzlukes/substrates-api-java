package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Capture;
import io.humainary.substrates.api.Substrates.Channel;
import io.humainary.substrates.api.Substrates.Id;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Reservoir;
import io.humainary.substrates.api.Substrates.Source;
import io.humainary.substrates.api.Substrates.State;
import io.humainary.substrates.api.Substrates.Subject;
import io.humainary.substrates.api.Substrates.Subscriber;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class SimpleReservoir<E> implements Reservoir<E> {

    private final BlockingQueue<Capture<E>> captures = new LinkedBlockingQueue<>();
    private final Name name;

    public SimpleReservoir(Source<E, ?> source) {
        this.name = new SimpleName("reservoir");
        Subscriber<E> subscriber = SimpleCortex.INSTANCE.subscriber(
                new SimpleName("reservoir.subscriber"),
                (subject, registrar) -> {
                    registrar.register(emission -> {
                        captures.add(new Capture<>() {
                            @Override
                            public E emission() {
                                return emission;
                            }

                            @Override
                            public Subject<Channel<E>> subject() {
                                return (Subject<Channel<E>>) subject;
                            }
                        });
                    });
                });
        source.subscribe(subscriber);
    }

    @Override
    public Stream<Capture<E>> drain() {
        List<Capture<E>> drained = new ArrayList<>();
        captures.drainTo(drained);
        return drained.stream();
    }

    @Override
    public Subject<Reservoir<E>> subject() {
        return new Subject<Reservoir<E>>() {
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
            public Class<Reservoir<E>> type() {
                return (Class) Reservoir.class;
            }

            @Override
            public String toString() {
                return "SimpleReservoir[" + name + "]";
            }
        };
    }
}

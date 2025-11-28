package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Channel;
import io.humainary.substrates.api.Substrates.Composer;
import io.humainary.substrates.api.Substrates.Conduit;
import io.humainary.substrates.api.Substrates.Id;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Percept;
import io.humainary.substrates.api.Substrates.State;
import io.humainary.substrates.api.Substrates.Subject;
import io.humainary.substrates.api.Substrates.Subscriber;
import io.humainary.substrates.api.Substrates.Subscription;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleConduit<P extends Percept, E> implements Conduit<P, E> {

    private final Name name;
    private final Composer<E, ? extends P> composer;
    private final SimpleCircuit circuit;
    private final ConcurrentHashMap<Name, SimpleChannel<E>> channels = new ConcurrentHashMap<>();
    private final List<Subscriber<E>> subscribers = new CopyOnWriteArrayList<>();

    public SimpleConduit(Name name, Composer<E, ? extends P> composer, SimpleCircuit circuit) {
        this.name = name;
        this.composer = composer;
        this.circuit = circuit;
    }

    @Override
    public P percept(Name name) {
        return composer.compose(channel(name));
    }

    @Override
    public Subscription subscribe(Subscriber<E> subscriber) {
        subscribers.add(subscriber);
        channels.values().forEach(channel -> channel.addSubscriber(subscriber));
        return new Subscription() {
            @Override
            public void close() {
                subscribers.remove(subscriber);
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

    private SimpleChannel<E> channel(Name name) {
        return channels.computeIfAbsent(name, n -> {
            SimpleChannel<E> ch = new SimpleChannel<>(n, circuit);
            subscribers.forEach(ch::addSubscriber);
            return ch;
        });
    }

    @Override
    public Subject<Conduit<P, E>> subject() {
        return new Subject<Conduit<P, E>>() {
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
            public Class<Conduit<P, E>> type() {
                return (Class) Conduit.class;
            }

            @Override
            public String toString() {
                return "SimpleConduit[" + name + "]";
            }
        };
    }
}

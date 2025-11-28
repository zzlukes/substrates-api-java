package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Channel;
import io.humainary.substrates.api.Substrates.Id;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Registrar;
import io.humainary.substrates.api.Substrates.State;
import io.humainary.substrates.api.Substrates.Subject;
import io.humainary.substrates.api.Substrates.Subscriber;
import java.util.function.BiConsumer;

public class SimpleSubscriber<E> implements Subscriber<E> {

    private final Name name;
    private final BiConsumer<Subject<Channel<E>>, Registrar<E>> callback;

    public SimpleSubscriber(Name name, BiConsumer<Subject<Channel<E>>, Registrar<E>> callback) {
        this.name = name;
        this.callback = callback;
    }

    public void accept(Subject<Channel<E>> subject, Registrar<E> registrar) {
        callback.accept(subject, registrar);
    }

    @Override
    public void close() {
    }

    @Override
    public Subject<Subscriber<E>> subject() {
        return new Subject<Subscriber<E>>() {
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
            public Class<Subscriber<E>> type() {
                return (Class) Subscriber.class;
            }

            @Override
            public String toString() {
                return "SimpleSubscriber[" + name + "]";
            }
        };
    }
}

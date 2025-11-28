package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Circuit;
import io.humainary.substrates.api.Substrates.Cortex;
import io.humainary.substrates.api.Substrates.Current;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Pipe;
import io.humainary.substrates.api.Substrates.Receptor;
import io.humainary.substrates.api.Substrates.Registrar;
import io.humainary.substrates.api.Substrates.Reservoir;
import io.humainary.substrates.api.Substrates.Scope;
import io.humainary.substrates.api.Substrates.Slot;
import io.humainary.substrates.api.Substrates.Source;
import io.humainary.substrates.api.Substrates.State;
import io.humainary.substrates.api.Substrates.Subject;
import io.humainary.substrates.api.Substrates.Subscriber;
import io.humainary.substrates.sdk.SimpleState.SimpleSlot;
import java.lang.reflect.Member;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleCortex implements Cortex {

    static final SimpleCortex INSTANCE = new SimpleCortex();

    private SimpleCortex() {
    }

    @Override
    public Circuit circuit() {
        return new SimpleCircuit();
    }

    @Override
    public Circuit circuit(Name name) {
        return new SimpleCircuit();
    }

    @Override
    public Name name(String value) {
        return new SimpleName(value);
    }

    @Override
    public Name name(Enum<?> path) {
        return new SimpleName(path.getDeclaringClass().getName() + "." + path.name());
    }

    @Override
    public Name name(Iterable<String> it) {
        return new SimpleName(String.join(".", it));
    }

    @Override
    public <T> Name name(Iterable<? extends T> it, Function<T, String> mapper) {
        StringBuilder sb = new StringBuilder();
        for (T t : it) {
            if (sb.length() > 0)
                sb.append(".");
            sb.append(mapper.apply(t));
        }
        return new SimpleName(sb.toString());
    }

    @Override
    public Name name(Iterator<String> it) {
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            if (sb.length() > 0)
                sb.append(".");
            sb.append(it.next());
        }
        return new SimpleName(sb.toString());
    }

    @Override
    public <T> Name name(Iterator<? extends T> it, Function<T, String> mapper) {
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            if (sb.length() > 0)
                sb.append(".");
            sb.append(mapper.apply(it.next()));
        }
        return new SimpleName(sb.toString());
    }

    @Override
    public Name name(Class<?> type) {
        return new SimpleName(type.getName());
    }

    @Override
    public Name name(Member member) {
        return new SimpleName(member.getDeclaringClass().getName() + "." + member.getName());
    }

    @Override
    public <E> Pipe<E> pipe(Receptor<? super E> receptor) {
        return new SimplePipe<>(receptor::receive);
    }

    @Override
    public <I, O> Pipe<I> pipe(Function<? super I, ? extends O> function, Pipe<? super O> pipe) {
        return new SimplePipe<>(input -> pipe.emit(function.apply(input)));
    }

    @Override
    public <E, S extends Source<E, S>> Reservoir<E> reservoir(Source<E, S> source) {
        return new SimpleReservoir<>(source);
    }

    @Override
    public <E> Subscriber<E> subscriber(Name name,
            BiConsumer<Subject<io.humainary.substrates.api.Substrates.Channel<E>>, Registrar<E>> subscriber) {
        return new SimpleSubscriber<>(name, subscriber);
    }

    @Override
    public Current current() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Scope scope() {
        return new SimpleScope();
    }

    @Override
    public Scope scope(Name name) {
        return new SimpleScope(name);
    }

    @Override
    public Slot<Boolean> slot(Name name, boolean value) {
        return new SimpleSlot<>(name, boolean.class, value);
    }

    @Override
    public Slot<Integer> slot(Name name, int value) {
        return new SimpleSlot<>(name, int.class, value);
    }

    @Override
    public Slot<Long> slot(Name name, long value) {
        return new SimpleSlot<>(name, long.class, value);
    }

    @Override
    public Slot<Double> slot(Name name, double value) {
        return new SimpleSlot<>(name, double.class, value);
    }

    @Override
    public Slot<Float> slot(Name name, float value) {
        return new SimpleSlot<>(name, float.class, value);
    }

    @Override
    public Slot<String> slot(Name name, String value) {
        return new SimpleSlot<>(name, String.class, value);
    }

    @Override
    public Slot<Name> slot(Enum<?> value) {
        return new SimpleSlot<>(name(value.getDeclaringClass()), Name.class, name(value.name()));
    }

    @Override
    public Slot<Name> slot(Name name, Name value) {
        return new SimpleSlot<>(name, Name.class, value);
    }

    @Override
    public Slot<State> slot(Name name, State value) {
        return new SimpleSlot<>(name, State.class, value);
    }

    @Override
    public State state() {
        return new SimpleState();
    }

    @Override
    public <E> Pipe<E> pipe() {
        return new SimplePipe<>(e -> {
        });
    }

    @Override
    public <E> Pipe<E> pipe(Class<E> type) {
        return new SimplePipe<>(e -> {
        });
    }

    @Override
    public <E> Pipe<E> pipe(Class<E> type, Receptor<? super E> receptor) {
        return new SimplePipe<>(receptor::receive);
    }
}

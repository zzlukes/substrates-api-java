package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Slot;
import io.humainary.substrates.api.Substrates.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SimpleState implements State {

    private final List<Slot<?>> slots;

    public SimpleState() {
        this.slots = Collections.emptyList();
    }

    private SimpleState(List<Slot<?>> slots) {
        this.slots = Collections.unmodifiableList(slots);
    }

    @Override
    public State compact() {
        return this;
    }

    @Override
    public State state(Name name, int value) {
        return add(new SimpleSlot<>(name, int.class, value));
    }

    @Override
    public State state(Name name, long value) {
        return add(new SimpleSlot<>(name, long.class, value));
    }

    @Override
    public State state(Name name, float value) {
        return add(new SimpleSlot<>(name, float.class, value));
    }

    @Override
    public State state(Name name, double value) {
        return add(new SimpleSlot<>(name, double.class, value));
    }

    @Override
    public State state(Name name, boolean value) {
        return add(new SimpleSlot<>(name, boolean.class, value));
    }

    @Override
    public State state(Name name, String value) {
        return add(new SimpleSlot<>(name, String.class, value));
    }

    @Override
    public State state(Name name, Name value) {
        return add(new SimpleSlot<>(name, Name.class, value));
    }

    @Override
    public State state(Name name, State value) {
        return add(new SimpleSlot<>(name, State.class, value));
    }

    @Override
    public State state(Slot<?> slot) {
        return add(slot);
    }

    @Override
    public State state(Enum<?> value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Stream<Slot<?>> stream() {
        return slots.stream();
    }

    @Override
    public <T> T value(Slot<T> slot) {
        for (Slot<?> s : slots) {
            if (s.name().equals(slot.name()) && s.type().equals(slot.type())) {
                return (T) s.value();
            }
        }
        return slot.value();
    }

    @Override
    public <T> Stream<T> values(Slot<? extends T> slot) {
        return slots.stream()
                .filter(s -> s.name().equals(slot.name()) && s.type().equals(slot.type()))
                .map(s -> (T) s.value());
    }

    @Override
    public Iterator<Slot<?>> iterator() {
        return slots.iterator();
    }

    private State add(Slot<?> slot) {
        List<Slot<?>> newSlots = new ArrayList<>(slots);
        newSlots.add(0, slot);
        return new SimpleState(newSlots);
    }

    static class SimpleSlot<T> implements Slot<T> {
        private final Name name;
        private final Class<T> type;
        private final T value;

        public SimpleSlot(Name name, Class<T> type, T value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        @Override
        public Name name() {
            return name;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public T value() {
            return value;
        }
    }
}

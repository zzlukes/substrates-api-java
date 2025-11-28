package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Pipe;
import java.util.function.Consumer;

public class SimplePipe<E> implements Pipe<E> {

    private final Consumer<E> emitter;

    public SimplePipe(Consumer<E> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void emit(E emission) {
        emitter.accept(emission);
    }

    @Override
    public void flush() {
        // No-op
    }
}

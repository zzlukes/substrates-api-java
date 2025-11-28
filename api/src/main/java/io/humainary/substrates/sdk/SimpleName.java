package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Name;
import java.lang.reflect.Member;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleName implements Name {

    private final String value;

    public SimpleName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Name))
            return false;
        return value.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public Name name(Name suffix) {
        return new SimpleName(value + SEPARATOR + suffix.toString());
    }

    @Override
    public Name name(String path) {
        return new SimpleName(value + SEPARATOR + path);
    }

    @Override
    public Name name(Enum<?> path) {
        return new SimpleName(value + SEPARATOR + path.getDeclaringClass().getName() + SEPARATOR + path.name());
    }

    @Override
    public Name name(Iterable<String> parts) {
        StringBuilder sb = new StringBuilder(value);
        for (String part : parts) {
            sb.append(SEPARATOR).append(part);
        }
        return new SimpleName(sb.toString());
    }

    @Override
    public <T> Name name(Iterable<? extends T> parts, Function<T, String> mapper) {
        StringBuilder sb = new StringBuilder(value);
        for (T part : parts) {
            sb.append(SEPARATOR).append(mapper.apply(part));
        }
        return new SimpleName(sb.toString());
    }

    @Override
    public Name name(Iterator<String> parts) {
        StringBuilder sb = new StringBuilder(value);
        while (parts.hasNext()) {
            sb.append(SEPARATOR).append(parts.next());
        }
        return new SimpleName(sb.toString());
    }

    @Override
    public <T> Name name(Iterator<? extends T> parts, Function<T, String> mapper) {
        StringBuilder sb = new StringBuilder(value);
        while (parts.hasNext()) {
            sb.append(SEPARATOR).append(mapper.apply(parts.next()));
        }
        return new SimpleName(sb.toString());
    }

    @Override
    public Name name(Class<?> type) {
        return new SimpleName(value + SEPARATOR + type.getName());
    }

    @Override
    public Name name(Member member) {
        return new SimpleName(value + SEPARATOR + member.getDeclaringClass().getName() + SEPARATOR + member.getName());
    }

    @Override
    public CharSequence path(Function<? super String, ? extends CharSequence> mapper) {
        return mapper.apply(value);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public void forEach(Consumer<? super Name> action) {
        action.accept(this);
    }

    @Override
    public CharSequence part() {
        return value;
    }
}

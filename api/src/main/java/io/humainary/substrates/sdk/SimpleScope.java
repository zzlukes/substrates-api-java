package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Closure;
import io.humainary.substrates.api.Substrates.Id;
import io.humainary.substrates.api.Substrates.Name;
import io.humainary.substrates.api.Substrates.Resource;
import io.humainary.substrates.api.Substrates.Scope;
import io.humainary.substrates.api.Substrates.State;
import io.humainary.substrates.api.Substrates.Subject;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SimpleScope implements Scope {

    private final Name name;

    public SimpleScope() {
        this.name = new SimpleName("scope");
    }

    public SimpleScope(Name name) {
        this.name = name;
    }

    @Override
    public void close() {
        // No-op for now
    }

    @Override
    public <R extends Resource> Closure<R> closure(R resource) {
        return consumer -> {
            try {
                consumer.accept(resource);
            } finally {
                resource.close();
            }
        };
    }

    @Override
    public <R extends Resource> R register(R resource) {
        return resource;
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
    public Subject<Scope> subject() {
        return new Subject<Scope>() {
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
            public Class<Scope> type() {
                return Scope.class;
            }

            @Override
            public String toString() {
                return "SimpleScope[" + name + "]";
            }
        };
    }

    @Override
    public int depth() {
        return 0;
    }

    @Override
    public Iterator<Scope> iterator() {
        return Stream.<Scope>empty().iterator();
    }

    @Override
    public CharSequence part() {
        return name.toString();
    }
}

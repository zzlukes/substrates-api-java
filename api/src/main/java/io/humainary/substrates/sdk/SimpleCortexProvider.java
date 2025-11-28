package io.humainary.substrates.sdk;

import io.humainary.substrates.api.Substrates.Cortex;
import io.humainary.substrates.spi.CortexProvider;

public class SimpleCortexProvider extends CortexProvider {

    @Override
    protected Cortex create() {
        return SimpleCortex.INSTANCE;
    }

}

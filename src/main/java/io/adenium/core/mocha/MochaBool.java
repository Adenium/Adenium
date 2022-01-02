package io.adenium.core.mocha;

import io.adenium.core.mocha.internal.MochaObject;

public class MochaBool extends MochaObject {
    private boolean bool;

    public MochaBool(boolean b) {
        this.bool = b;
    }

    @Override
    public boolean isTrue() {
        return bool;
    }
}

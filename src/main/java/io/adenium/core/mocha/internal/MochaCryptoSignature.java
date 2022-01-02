package io.adenium.core.mocha.internal;

import io.adenium.crypto.Signature;

public class MochaCryptoSignature extends MochaObject {
    private Signature signature;

    public MochaCryptoSignature(Signature signature) {
        this.signature = signature;
    }

    public Signature getSignature() {
        return signature;
    }
}

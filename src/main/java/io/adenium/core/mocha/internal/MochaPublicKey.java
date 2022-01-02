package io.adenium.core.mocha.internal;

import io.adenium.core.mocha.MochaBool;
import io.adenium.crypto.Key;

public class MochaPublicKey extends MochaObject {
    private Key             publicKey;

    public MochaPublicKey(Key key) {
        this.publicKey = key;
    }

    public MochaObject checkSignature(MochaCryptoSignature signature, byte signatureData[]) {
        return new MochaBool(signature.getSignature().checkSignature(signatureData, publicKey));
    }
}

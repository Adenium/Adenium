package io.adenium.papaya.runtime;

import java.math.BigInteger;

public class PapayaNumber extends PapayaObject {
    private final BigInteger    number;
    private boolean             signed;

    public PapayaNumber(long number, boolean signed) {
        this(fromLong(number, signed), signed);
    }

    public PapayaNumber(BigInteger number, boolean signed) {
        this.number                 = number;
        this.signed                 = signed;
    }

    @Override
    public BigInteger asInt() {
        return number;
    }

    protected static BigInteger fromLong(long number, boolean allowSignedOperations) {
        if (allowSignedOperations) {
            return new BigInteger(Long.toString(number));
        }

        return new BigInteger(Long.toUnsignedString(number));
    }
}

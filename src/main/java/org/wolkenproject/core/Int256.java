package org.wolkenproject.core;

import org.wolkenproject.utils.Utils;

public class Int256 {
    public int      data[];
    public boolean  signed;

    public Int256(long value) {
        this(value, true);
    }

    public Int256(long value, boolean signed) {
        this(convertLong(value), signed);
    }

    private static int[] convertLong(long value) {
        int result[] = new int[8];
        byte bytes[] = Utils.takeApartLong(value);

        result[6]   = Utils.makeInt(bytes, 0);
        result[7]   = Utils.makeInt(bytes, 4);

        return result;
    }

    private static long convertInts(int result[]) {
        byte bytes0[] = Utils.takeApart(result[6]);
        byte bytes1[] = Utils.takeApart(result[7]);

        return Utils.makeLong(Utils.concatenate(bytes0, bytes1));
    }

    private Int256(int data[], boolean signed) {
        this.data   = data;
        this.signed = signed;
    }

    public Int256 add(Int256 other) {
        int carry       = 0;
        int result[]    = new int[8];

        for (int x = 0; x < 8; x ++) {
            int i = 7 - x;

            for (int b = 0; b < 32; b ++) {
                int bit0    = Utils.getBit(data[i], b);
                int bit1    = Utils.getBit(other.data[i], b);

                int sum0    = (bit0 ^ bit1);
                int sum1    = sum0 ^ carry;
                carry       = (bit0 & bit1) | (carry & sum0);

                result[i]   = Utils.setBit(result[i], b, sum1);
            }
        }

        return new Int256(result, !(other.signed & signed));
    }

    public Int256 mul(Int256 other) {
        int carry       = 0;
        int result[]    = new int[8];

        for (int x = 0; x < 8; x ++) {
            int i = 7 - x;

            for (int b = 0; b < 32; b ++) {
                int bit0    = Utils.getBit(data[i], b);
                int bit1    = Utils.getBit(other.data[i], b);

                int sum0    = (bit0 ^ bit1);
                int sum1    = sum0 ^ carry;
                carry       = (bit0 & bit1) | (carry & sum0);

                result[i]   = Utils.setBit(result[i], b, sum1);
            }
        }

        return new Int256(result, !(other.signed & signed));
    }

    public Int256 sub(Int256 other) {
        int result[]    = new int[8];

        for (int i = 0; i < 8; i ++) {
            result[i]   = ~other.data[i];
        }

        return add(new Int256(result, !(other.signed & signed)));
    }

    public int[] getData() {
        return data;
    }

    public long asLong() {
        return convertInts(data);
    }
}

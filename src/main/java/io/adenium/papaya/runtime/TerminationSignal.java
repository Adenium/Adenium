package io.adenium.papaya.runtime;

public class TerminationSignal {
    private final long  gasLimit;
    private final long  gasPrice;
    private final long  gasUsed;
    private final int   stopCode;

    public TerminationSignal(long gasLimit, long gasPrice, long gasUsed, int stopCode) {
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.gasUsed = gasUsed;
        this.stopCode = stopCode;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public long getGasPrice() {
        return gasPrice;
    }

    public long getGasUsed() {
        return gasUsed;
    }

    public int getStopCode() {
        return stopCode;
    }

    public long getAdeniumCost() {
        return getGasUsed() * getGasPrice();
    }
}

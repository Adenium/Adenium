package io.adenium.exceptions;

public class InvalidAssetImplementation extends Exception {
    public InvalidAssetImplementation(String msg) {
        super(msg);
    }

    public InvalidAssetImplementation(Exception e) {
        super(e);
    }
}

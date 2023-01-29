package io.adenium.papaya.runtime;

import io.adenium.papaya.compiler.PapayaApplication;

public class PapayaRuntime {
    private PapayaApplication application;

    // Create a new runtime with this application as the source.
    public PapayaRuntime(PapayaApplication application) {
        this.application = application;
    }
}

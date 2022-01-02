package io.adenium.core.mocha.internal;

import io.adenium.core.mocha.Scope;
import io.adenium.exceptions.InvalidTransactionException;
import io.adenium.exceptions.MochaException;

public interface MochaCallable {
    MochaObject call(Scope scope) throws MochaException, InvalidTransactionException;
}

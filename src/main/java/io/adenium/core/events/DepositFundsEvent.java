package io.adenium.core.events;

import io.adenium.core.Context;
import io.adenium.core.Event;
import org.json.JSONObject;
import io.adenium.encoders.Base58;
import io.adenium.utils.Utils;

public class DepositFundsEvent extends Event {
    private byte address[];
    private long amount;

    public DepositFundsEvent(byte address[], long amount) {
        this.address    = address;
        this.amount     = amount;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().storeAccount(address,
                Context.getInstance().getDatabase().findAccount(address).deposit(amount));
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().storeAccount(address,
                Context.getInstance().getDatabase().findAccount(address).withdraw(amount));
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Deposit".getBytes(), address, Utils.takeApartLong(amount));
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("event", this.getClass().getName()).put("address", Base58.encode(address)).put("amount", amount);
    }
}

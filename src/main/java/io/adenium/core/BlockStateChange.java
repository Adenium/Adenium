package io.adenium.core;

import io.adenium.core.events.*;
import io.adenium.utils.Utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockStateChange {
    private Queue<byte[]>   transactionIds;
    private Queue<byte[]>   transactionEventIds;
    private List<Event>     transactionEvents;
    private int             blockHeight;

    public BlockStateChange(int blockHeight) {
        this.transactionIds         = new LinkedList<>();
        this.transactionEventIds    = new LinkedList<>();
        this.transactionEvents      = new LinkedList<>();
        this.blockHeight            = blockHeight;
    }

    public TransactionStateChange push() {
        return new TransactionStateChange(this);
    }

    public boolean checkAliasExists(long alias) {
        if (Context.getInstance().getDatabase().checkAccountExists(alias)) {
            return true;
        }

        for (Event event : transactionEvents) {
            if (event instanceof RegisterAliasEvent) {
                if (((RegisterAliasEvent) event).getAlias() == alias) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkAccountExists(byte address[]) {
        if (Context.getInstance().getDatabase().checkAccountExists(address)) {
            return true;
        }

        for (Event event : transactionEvents) {
            if (event instanceof NewAccountEvent) {
                if (Arrays.equals(((NewAccountEvent) event).getAddress(), address)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void addEvent(Event event) {
        transactionEvents.add(event);
        transactionEventIds.add(event.eventId());
    }

    public void addEvents(List<Event> events) {
        transactionEvents.addAll(events);
        for (Event event : events) {
            transactionEventIds.add(event.eventId());
        }
    }

    public BlockStateChangeResult getResult() {
        return new BlockStateChangeResult(transactionIds, transactionEventIds, transactionEvents);
    }

    public void addTransaction(byte[] transactionID) {
        transactionIds.add(transactionID);
    }

    public void createAccountIfDoesNotExist(byte address[]) {
        if (checkAccountExists(address)) {
            return;
        }

        addEvent(new NewAccountEvent(address));
    }

    // returns the balance for the account associated with 'address'
    // if 'afterDeposit' is true then DepositEvents from this state change are summed.
    // if 'afterWithdraw' is true then WithdrawEvents from this state change are summed.
    public long getAccountBalance(byte[] address, boolean afterDeposit, boolean afterWithdraw) {
        long balance = 0L;

        if (Context.getInstance().getDatabase().checkAccountExists(address)) {
            balance = Context.getInstance().getDatabase().findAccount(address).getBalance();
        }

        for (Event event : transactionEvents) {
            if (event instanceof DepositFundsEvent && afterDeposit && Arrays.equals(((DepositFundsEvent) event).getAddress(), address)) {
                balance += ((DepositFundsEvent) event).getAmount();
            } else if (event instanceof WithdrawFundsEvent && afterWithdraw && Arrays.equals(((WithdrawFundsEvent) event).getAddress(), address)) {
                balance -= ((WithdrawFundsEvent) event).getAmount();
            }

//          Minted coins cannot be used on the same block.
//          else if (event instanceof MintRewardEvent && afterDeposit && Arrays.equals(((MintRewardEvent) event).getAddress(), address)) {
//          }
        }

        return balance;
    }

    // returns the token balance of type 'UUID' for the account associated with 'address'
    // if 'afterDeposit' is true then DepositEvents from this state change are summed.
    // if 'afterWithdraw' is true then WithdrawEvents from this state change are summed.
    public BigInteger getAccountBalance(byte address[], byte uuid[], boolean afterDeposit, boolean afterWithdraw)
    {
        BigInteger balance = BigInteger.ZERO;

        if (Context.getInstance().getDatabase().checkAccountExists(address)) {
            balance = Context.getInstance().getDatabase().findAccount(address).getBalanceForToken(uuid);
        }

        for (Event event : transactionEvents) {
            if (event instanceof AssetTransferEvent && afterDeposit && Arrays.equals(((DepositFundsEvent) event).getAddress(), address)) {
//                balance += ((DepositFundsEvent) event).getAmount();
            }

//          Minted coins cannot be used on the same block.
//          else if (event instanceof MintRewardEvent && afterDeposit && Arrays.equals(((MintRewardEvent) event).getAddress(), address)) {
//          }
        }

        return balance;
    }

    public int getBlockHeight() {
        return blockHeight;
    }
}

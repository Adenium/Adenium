package io.adenium.network.messages;

import io.adenium.core.Context;
import io.adenium.core.transactions.Transaction;
import io.adenium.exceptions.WolkenException;
import io.adenium.serialization.SerializableI;
import io.adenium.network.Message;
import io.adenium.network.Node;
import io.adenium.network.ResponseMetadata;
import io.adenium.network.Server;
import io.adenium.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class RequestTransactions extends Message {
    private Set<byte[]> transactions;

    public RequestTransactions(int version, Collection<byte[]> transactions) {
        super(version, Flags.Request);
        this.transactions = new LinkedHashSet<>(transactions);
    }

    @Override
    public void executePayload(Server server, Node node) {
        Set<Transaction> transactions = new LinkedHashSet<>();
        for (byte[] txid : this.transactions)
        {
            Transaction transaction = Context.getInstance().getTransactionPool().getTransaction(txid);

            if (transaction != null)
            {
                transactions.add(transaction);
            }
        }

        // send the transactions
        node.sendMessage(new TransactionList(Context.getInstance().getNetworkParameters().getVersion(), transactions, getUniqueMessageIdentifier()));
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {
        Utils.writeInt(transactions.size(), stream);
        for (byte[] txid : transactions)
        {
            stream.write(txid);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException {
        byte buffer[] = new byte[4];
        stream.read(buffer);

        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            byte txid[] = new byte[Transaction.UniqueIdentifierLength];
            stream.read(txid);

            transactions.add(txid);
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) transactions;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RequestTransactions(getVersion(), transactions);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RequestTransactions.class);
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return (msg)->{
            boolean isCorrectType = msg instanceof BlockList;

            if (!isCorrectType) {
                return ResponseMetadata.ValidationBits.InvalidResponse | ResponseMetadata.ValidationBits.SpamfulResponse;
            }

            int response = 0;
            Collection<Transaction> transactions = msg.getPayload();

            int checked = 0;
            for (Transaction transaction : transactions) {
                if (this.transactions.contains(transaction.getHash())) {
                    checked ++;
                }
            }

            if (transactions.size() > this.transactions.size()) {
                response |= ResponseMetadata.ValidationBits.SpamfulResponse;
                response |= ResponseMetadata.ValidationBits.InvalidResponse;
            }

            if (checked != this.transactions.size()) {
                response |= ResponseMetadata.ValidationBits.PartialResponse;
                response |= ResponseMetadata.ValidationBits.InvalidResponse;
            }

            if (checked == this.transactions.size() && response != 0) {
                response |= ResponseMetadata.ValidationBits.EntireResponse;
            }

            return response;
        };
    }
}
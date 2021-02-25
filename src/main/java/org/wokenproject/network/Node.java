package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    private Socket          socket;
    private ReentrantLock   mutex;
    private Queue<Message>  messages;
    private MessageCache    messageCache;
    private int             errors;

    private BufferedInputStream     inputStream;
    private BufferedOutputStream    outputStream;

    public Node(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Node(Socket socket) throws IOException {
        this.socket         = socket;
        this.mutex          = new ReentrantLock();
        this.messages       = new ConcurrentLinkedQueue<>();
        this.messageCache   = new MessageCache();
        this.errors         = 0;
        this.inputStream    = new BufferedInputStream(socket.getInputStream(), Context.getInstance().getNetworkParameters().getBufferSize());
        this.outputStream   = new BufferedOutputStream(socket.getOutputStream(), Context.getInstance().getNetworkParameters().getBufferSize());
    }

    public void sendMessage(Message message) {
        mutex.lock();
        try{
            if (messageCache.shouldSend(message))
            {
                messages.add(message);
            }
        } finally {
            mutex.unlock();
        }
    }

    public Message listen() {
        try {
            // a loop that hangs the entire thread might be dangerous.
            //         while ((read = stream.read(messageHeader, read, messageHeader.length - read)) != messageHeader.length);
            byte messageHeader[] = new byte[20];

            int read = inputStream.read(messageHeader);
            if (read != messageHeader.length) {
                errors++;
                return null;
            }

            int version = Utils.makeInt(messageHeader);
            int flags   = Utils.makeInt(messageHeader, 4);
            int type    = Utils.makeInt(messageHeader, 8);
            int count   = Utils.makeInt(messageHeader, 12);
            int length  = Utils.makeInt(messageHeader, 16);

            byte content[] = new byte[length];

            read = inputStream.read(content); // while (read < content.length) { read += stream.read(content, read, content.length - read); }
            if (read != content.length) {
                errors++;
                return null;
            }

            return checkSpam(new Message(version, flags, type, count, content));
        } catch (IOException e) {
            errors ++;
            return null;
        }
    }

    private Message checkSpam(Message message) {
        messageCache.cacheReceivedMessage(message);
        return message;
    }

    public void send()
    {
        mutex.lock();
        try{
            while (!messages.isEmpty()) {
                Message message = messages.poll();
                message.writeToStream(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public void close() throws IOException {
        outputStream.flush();
        socket.close();
        inputStream.close();
        outputStream.close();
    }

    public int getTotalErrorCount() {
        return errors;
    }

    public double getSpamCount() {
        return messageCache.getAverageSpam();
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }
}

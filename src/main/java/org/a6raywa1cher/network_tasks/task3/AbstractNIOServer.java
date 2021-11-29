package org.a6raywa1cher.network_tasks.task3;

import org.a6raywa1cher.network_tasks.task2.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.Set;

public abstract class AbstractNIOServer implements Server {
    private static final Logger log = LoggerFactory.getLogger(AbstractNIOServer.class);

    protected final int port;
    protected Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    public AbstractNIOServer(int port) {
        this.port = port;
    }

    protected void initializeSelector() throws IOException {
        selector = Selector.open();
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.bind(new InetSocketAddress(port));
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void listen(boolean singleRequest) throws IOException {
        initializeSelector();
        log.info("Selector initialized");
        while (true) {
            int readyCount = selector.select();
            if (readyCount == 0) {
                continue;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // Remove key from set so we don't process it twice
                iterator.remove();
                try {
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) { // step 1: establish connection to a client
                        accept(key);
                    }
                    if (key.isReadable()) { // step 2: read data
                        readAndWrite(key);
                        if (singleRequest) return;
                    }
                } catch (Exception e) {
                    log.error("Exception occur during request processing", e);
                }
            }
        }
    }

    public void listen() throws IOException {
        listen(false);
    }

    private String getClientIp(SocketChannel channel) {
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        return remoteAddr.toString();
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);

        log.debug("{}: connected", getClientIp(channel));

        channel.register(this.selector, SelectionKey.OP_READ);
    }

    private void readAndWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String data = read(channel);
        if (!channel.isOpen() || !channel.isConnected()) {
            return;
        }
        String response = processRequest(data);
        if (!channel.isOpen() || !channel.isConnected()) {
            return;
        }
        write(channel, response);
        channel.close();
    }

    private String read(SocketChannel channel) throws IOException {
        int numRead = channel.read(buffer);

        if (numRead == -1) {
            log.debug("{}: connection closed", getClientIp(channel));
            channel.close();
            return null;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);

        log.debug("{}: received data", getClientIp(channel));

        return new String(data);
    }

    private void write(SocketChannel channel, String toWrite) throws IOException {
        byte[] bytes = toWrite.getBytes(StandardCharsets.UTF_8);
        buffer.clear();
        buffer.put(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        channel.write(buffer);
        log.debug("{}: written data", getClientIp(channel));
    }

    protected abstract String processRequest(String data);
}

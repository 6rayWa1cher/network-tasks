package org.a6raywa1cher.network_tasks.task3;

import org.a6raywa1cher.network_tasks.task2.Server;

import java.io.IOException;

public class NIOServer extends AbstractNIOServer {
    public NIOServer(int port) {
        super(port);
    }

    @Override
    protected String processRequest(String data) {
        return "Hello " + data.strip() + "!\n";
    }

    public static void main(String[] args) throws IOException {
        NIOServer nioServer = new NIOServer(Server.DEFAULT_PORT);
        nioServer.listen();
    }
}

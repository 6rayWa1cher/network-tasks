package org.a6raywa1cher.network_tasks.task2_3;

import lombok.extern.slf4j.Slf4j;
import org.a6raywa1cher.network_tasks.task2.AbstractBlockingServer;
import org.a6raywa1cher.network_tasks.task2.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class FJPServer extends AbstractBlockingServer {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    public FJPServer(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        FJPServer simpleServer = new FJPServer(Server.DEFAULT_PORT);
        simpleServer.listen();
    }

    @Override
    protected void processSocket(Socket socket) throws IOException {
        forkJoinPool.submit(() -> {
            try {
                this.processRequest(socket);
            } catch (IOException e) {
                log.error("IOException", e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("IOException", e);
                }
            }
        });
    }
}

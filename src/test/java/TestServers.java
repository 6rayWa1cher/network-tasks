import org.a6raywa1cher.network_tasks.task1.TextRequest;
import org.a6raywa1cher.network_tasks.task2.AbstractServer;
import org.a6raywa1cher.network_tasks.task2.SimpleClient;
import org.a6raywa1cher.network_tasks.task2.SimpleServer;
import org.a6raywa1cher.network_tasks.task2_2.MTServer;
import org.a6raywa1cher.network_tasks.task2_3.FJPServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestServers {
    int port = 25566;

    private void testServer(AbstractServer abstractServer) throws InterruptedException {
        Thread server = new Thread(() -> {
            try {
                abstractServer.listen(true);
            } catch (IOException e) {
                Assertions.fail(e);
            }
        });
        server.start();
        Thread client = new Thread(() -> {
            TextRequest<String> textRequest = new SimpleClient(null, port) {
                @Override
                protected String constructRequest() {
                    return "cat\n";
                }
            };
            try {
                String output = textRequest.call();
                Assertions.assertEquals("Hello cat!\n", output);
            } catch (IOException e) {
                Assertions.fail(e);
            }
        });
        client.start();
        client.join();
        server.join();
    }

    @Test
    void testSimpleServer() throws InterruptedException {
        testServer(new SimpleServer(port));
    }

    @Test
    void testMTServer() throws InterruptedException {
        testServer(new MTServer(port));
    }

    @Test
    void testFJPServer() throws InterruptedException {
        testServer(new FJPServer(port));
    }
}

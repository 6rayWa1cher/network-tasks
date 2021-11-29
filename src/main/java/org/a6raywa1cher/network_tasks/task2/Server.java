package org.a6raywa1cher.network_tasks.task2;

import java.io.IOException;

public interface Server {
    int DEFAULT_PORT = 25565;
    void listen() throws IOException;

    void listen(boolean singleRequest) throws IOException;
}

package org.a6raywa1cher.network_tasks.task1;

import java.io.IOException;

public interface TextRequest<T> {
    T call() throws IOException;
}

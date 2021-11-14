package org.a6raywa1cher.network_tasks.task1;

import org.a6raywa1cher.network_tasks.task1_extended.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public abstract class AbstractTextRequest<T> implements TextRequest<T> {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    protected String hostname;
    protected int port;

    public AbstractTextRequest(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public AbstractTextRequest(String url) throws MalformedURLException {
        URL parsed = new URL(url);
        this.hostname = parsed.getHost();
        this.port = parsed.getPort();
    }

    protected abstract String constructRequest();

    protected abstract T convertResponse(String convertedInput);

    public T call() throws IOException {

        logger.debug("Connecting to {}:{}", hostname, port);

        try (Socket socket = new Socket(hostname, port);
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            logger.debug("Connected to {}:{}", hostname, port);

            String request = constructRequest();

            if (request != null) {
                printWriter.print(request);

                printWriter.flush();

                logger.debug("Sent, awaiting an answer");
            }

            String convertedInput = receiveAnswer(reader);

            return convertResponse(convertedInput);
        }
    }

    protected String receiveAnswer(BufferedReader reader) throws IOException {
        String line;
        StringBuilder input = new StringBuilder();
        boolean first = true;

        while ((line = reader.readLine()) != null) {
            input.append(line).append('\n');
            logger.trace(line);
            if (first) {
                first = false;
                logger.debug("Received a first line");
            }
        }

        logger.debug("Received an answer");

        return input.toString();
    }
}

package org.a6raywa1cher.network_tasks.task1_extended;

import lombok.Builder;
import lombok.Data;
import org.a6raywa1cher.network_tasks.task1.TextRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Builder
public class HttpTextRequest implements TextRequest {
    public static final String CONTENT_LENGTH = "Content-Length";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0";
    private static final Pattern HTTP_HEADER_PATTERN = Pattern.compile(
            "HTTP/.{3} ([0-9]{3}) \\w+", Pattern.CASE_INSENSITIVE
    );
    private static final String END_LINE = "\r\n";
    private static final String END_LINE_REGEX = "\\r?\\n";
    private final HttpMethod method;

    private final URL url;

    private final List<Pair<String, String>> headers;

    private final String body;

    private HttpTextRequest(HttpMethod method, URL url, List<Pair<String, String>> headers, String body) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        addDefaultHeaders();
    }

    public HttpTextRequest(String url) throws MalformedURLException {
        this(HttpMethod.GET, new URL(url), new ArrayList<>(), null);
    }

    public HttpTextRequest(HttpMethod method, String url) throws MalformedURLException {
        this(method, new URL(url), new ArrayList<>(), null);
    }

    private void addHeaderIfAbsent(String headerName, String value) {
        if (headers.stream().noneMatch(p -> p.getKey().equals(headerName))) {
            headers.add(Pair.of(headerName, value));
        }
    }

    private void addDefaultHeaders() {
        addHeaderIfAbsent("Host", url.getHost());
        addHeaderIfAbsent("User-Agent", USER_AGENT);
        addHeaderIfAbsent("Accept", "*/*");
        if (isGoingToSendBody()) {
            addHeaderIfAbsent(
                    CONTENT_LENGTH, Integer.toString(body.getBytes(StandardCharsets.UTF_8).length)
            );
        }
    }

    private String getBody(String convertedInput) {
        return convertedInput.substring(convertedInput.indexOf(END_LINE + END_LINE) + END_LINE.length() * 2);
    }

    private int getStatus(String headLine) {
        Matcher matcher = HTTP_HEADER_PATTERN.matcher(headLine);
        matcher.find();
        return Integer.parseInt(matcher.group(1));
    }

    private boolean isSendingBodyPermitted() {
        return method != HttpMethod.GET && method != HttpMethod.OPTION;
    }

    private boolean isGoingToSendBody() {
        return isSendingBodyPermitted() && body != null;
    }

    public HttpResponse call() throws IOException {
        String hostname = url.getHost();
        int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();

        logger.debug("Connecting to {}:{}", hostname, port);

        try (Socket socket = new Socket(hostname, port);
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            logger.debug("Connected to {}:{}", hostname, port);

            String requestHead = constructRequestHead();

            logger.debug("Prepared request:");
            for (String s : requestHead.split(END_LINE_REGEX)) {
                logger.debug("{}", s);
            }
            if (body != null) {
                if (isSendingBodyPermitted()) {
                    logger.debug("[body]");
                } else {
                    logger.warn("body is omitted");
                }
            }

            printWriter.print(requestHead);
            printWriter.println();
            if (isGoingToSendBody()) {
                printWriter.print(body);
            }

            logger.debug("Sent, awaiting an answer");

            String convertedInput = receiveAnswer(reader);

            List<String> httpHead = getHttpHead(convertedInput);
            String headLine = httpHead.get(0);

            HttpResponse response = new HttpResponse(getStatus(headLine), getInputHeaders(httpHead), getBody(convertedInput));
            logger.info("{} {}: {}", method.toString(), getUrl().toString(), response.status());
            return response;
        }
    }

    private List<String> getHttpHead(String convertedInput) {
        String[] split = convertedInput.split(END_LINE_REGEX);
        return Arrays.stream(split)
                .takeWhile(s -> !"".equals(s))
                .collect(Collectors.toList());
    }

    private List<Pair<String, String>> getInputHeaders(List<String> httpHead) {
        return httpHead.stream()
                .skip(1) // skip the head line
                .map(s -> {
                    int colonPos = s.indexOf(':');
                    return Pair.of(s.substring(0, colonPos), s.substring(colonPos + 1).stripLeading());
                })
                .collect(Collectors.toList());
    }

    private int getContentLength(String convertedInput) {
        List<Pair<String, String>> inputHeaders = getInputHeaders(getHttpHead(convertedInput));
        return inputHeaders.stream()
                .filter(p -> p.getKey().equals(CONTENT_LENGTH))
                .map(Pair::getValue)
                .map(Integer::parseInt)
                .findAny()
                .orElse(-1);
    }

    private String receiveAnswer(BufferedReader reader) throws IOException {
        String line;
        StringBuilder input = new StringBuilder();
        boolean first = true;
//        boolean headersFound = false;
//        int contentLengthLeft = 0;

        while (
//                !(headersFound && contentLengthLeft <= 0) &&
                (line = reader.readLine()) != null
        ) {
            input.append(line).append(END_LINE);
//            logger.trace("{} {}", line, contentLengthLeft);
            logger.trace(line);
            if (first) {
                first = false;
                logger.debug("Received a first line");
            }
//            else if (line.equals("") &&
//                    !headersFound
//            ) {
//                headersFound = true;
//                contentLengthLeft = getContentLength(input.toString());
//            } else if (headersFound) {
//                contentLengthLeft -= line.getBytes(StandardCharsets.UTF_8).length;
//            }
        }

        logger.debug("Received an answer");

        return input.toString();
    }

    private String getResource() {
        String output = !url.getPath().equals("") ? url.getPath() : "/";
        if (url.getQuery() != null && !url.getQuery().equals("")) {
            output += '?' + url.getQuery();
        }
        return output;
    }

    private String constructRequestHead() {
        StringBuilder out = new StringBuilder();

        out.append(method.toString()).append(' ').append(getResource()).append(' ').append("HTTP/1.1").append(END_LINE);

        for (var header : headers) {
            out.append(header.getKey()).append(": ").append(header.getValue()).append(END_LINE);
        }

        return out.toString();
    }
}

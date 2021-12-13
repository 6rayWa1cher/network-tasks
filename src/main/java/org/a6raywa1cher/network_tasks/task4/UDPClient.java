package org.a6raywa1cher.network_tasks.task4;

import lombok.extern.slf4j.Slf4j;
import org.a6raywa1cher.network_tasks.task2.Server;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.*;
import java.util.Random;

@Slf4j
public class UDPClient implements AutoCloseable {
    private final DatagramSocket socket;
    private final InetAddress address;

    private byte[] buf;

    public UDPClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        try (UDPClient client = new UDPClient()) {
            Pair<Integer, Integer> integerIntegerPair = client.startProbe(5000, 200);
            int lastPacketId = integerIntegerPair.getLeft();
            int packetsReceived = integerIntegerPair.getRight();
            if (lastPacketId != packetsReceived) {
                log.error("ERROR FUCK!");
            }
        }
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, Server.DEFAULT_PORT);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    private String generateRandomString(Random random, int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private Pair<Integer, Integer> parse(String received) {
        System.out.println(received);
        return Pair.of(
                Integer.parseInt(received.substring(0, received.indexOf("|"))),
                Integer.parseInt(received.substring(received.indexOf("|") + 1))
        );
    }

    public Pair<Integer, Integer> startProbe(int count, long delay) {
        Random random = new Random();
        Pair<Integer, Integer> pair = null;
        for (int i = 1; i <= count; i++) {
            try {
                String msg = "<" + i + ">" + generateRandomString(random, random.nextInt(1000));
                System.out.println(msg);
                String received = sendEcho(msg);

                pair = parse(received);
                int lastPacketId = pair.getLeft();
                int packetsReceived = pair.getRight();
                log.info("lastPacketId={}, packetsReceived={}", lastPacketId, packetsReceived);

                Thread.sleep(delay);
            } catch (Exception e) {
                log.error("Error!", e);
            }
        }
        return pair;
    }

    public void close() {
        socket.close();
    }
}

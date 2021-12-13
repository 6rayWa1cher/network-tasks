package org.a6raywa1cher.network_tasks.task4;

import org.a6raywa1cher.network_tasks.task2.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        UDPServer server = new UDPServer();
        server.listen(Server.DEFAULT_PORT);
    }

    public void listen(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);

        byte[] buf = new byte[256];

        boolean running = true;

        int packetsReceived = 0;
        int lastPacketId = 0;

        Pattern pattern = Pattern.compile("<(\\d+)>.+", Pattern.MULTILINE);

        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int packetPort = packet.getPort();
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(received);

            Matcher matcher = pattern.matcher(received);

            if (matcher.matches()) {
                lastPacketId = Integer.parseInt(matcher.group(1));
                packetsReceived++;
            }

            if (received.equals("end")) {
                running = false;
            }

            String out = lastPacketId + "|" + packetsReceived;
            byte[] outBytes = out.getBytes(StandardCharsets.UTF_8);
            packet = new DatagramPacket(outBytes, outBytes.length, address, packetPort);
            socket.send(packet);
        }

        socket.close();
    }
}

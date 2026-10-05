package main.net;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private Consumer<NetworkPacket> onPacketReceived;

    public boolean connect(String hostIp, int port, Consumer<NetworkPacket> onPacketReceived) {
        this.onPacketReceived = onPacketReceived;
        try {
            socket = new Socket(hostIp, port);
            out = new ObjectOutputStream(socket.getOutputStream());

            new Thread(this::listenForPackets).start();
            System.out.println("✅ Успішно підключено до LAN Сервера!");
            return true;
        } catch (IOException e) {
            System.err.println("❌ Не вдалося підключитися до хоста: " + e.getMessage());
            return false;
        }
    }

    public void sendPacket(NetworkPacket packet) {
        if (out != null) {
            try {
                out.writeObject(packet);
                out.flush();
            } catch (IOException e) {
                System.err.println("❌ Помилка відправки пакета: " + e.getMessage());
            }
        }
    }

    private void listenForPackets() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (!socket.isClosed()) {
                NetworkPacket packet = (NetworkPacket) in.readObject();
                if (onPacketReceived != null) {
                    onPacketReceived.accept(packet);
                }
            }
        } catch (Exception e) {
            System.out.println("🔌 Відключено від сервера.");
        }
    }
}

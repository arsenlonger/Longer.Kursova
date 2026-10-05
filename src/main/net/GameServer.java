package main.net;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer implements Runnable {
    private static final int PORT = 9876;
    private final List<ObjectOutputStream> clientOutputs = new ArrayList<>();
    private boolean isRunning = true;

    public void startServer() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🌐 LAN Сервер бункера запущено на порту " + PORT);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("👥 Новий гравець підключився з IP: " + clientSocket.getInetAddress());

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputs.add(out);

                new Thread(new ClientHandler(clientSocket, out)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Помилка LAN Сервера: " + e.getMessage());
        }
    }

    public void broadcastPacket(NetworkPacket packet) {
        synchronized (clientOutputs) {
            for (ObjectOutputStream out : clientOutputs) {
                try {
                    out.writeObject(packet);
                    out.flush();
                } catch (IOException ignored) {}
            }
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final ObjectOutputStream out;

        public ClientHandler(Socket socket, ObjectOutputStream out) {
            this.socket = socket;
            this.out = out;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                while (isRunning) {
                    NetworkPacket packet = (NetworkPacket) in.readObject();
                    broadcastPacket(packet);
                }
            } catch (Exception e) {
                System.out.println("🔌 Гравець відключився.");
                synchronized (clientOutputs) {
                    clientOutputs.remove(out);
                }
            }
        }
    }
}

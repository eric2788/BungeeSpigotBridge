package com.ericlam.mc.bsbridge.bungee;

import net.md_5.bungee.api.ProxyServer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

class Responser extends Thread {
    private Socket socket;

    Responser(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {
            BufferedReader reader;
            PrintWriter writer;
            String line = "";
            while (true) {
                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                writer = new PrintWriter(socket.getOutputStream());
                line = reader.readLine();
                if (line == null){
                    ProxyServer.getInstance().getLogger().info("Server " + socket.getInetAddress() + " has disconnected with bungee.");
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }
                ProxyServer.getInstance().getLogger().info("[DEBUG] Received Message: " + line);
                UUID[] uuids = BungeePlugin.cyberKey.decrypt(line);
                if (uuids == null || uuids.length < 2) continue;
                UUID user = uuids[0];
                if (!BungeePlugin.queue.remove(user)) {
                    ProxyServer.getInstance().getLogger().warning("Unknown User Request " + user.toString());
                    writer.println(BungeePlugin.cyberKey.encrypt(UUID.randomUUID()));
                    writer.flush();
                    continue;
                }
                ProxyServer.getInstance().getLogger().info("Received: " + Arrays.toString(uuids));
                writer.println(BungeePlugin.cyberKey.encrypt(uuids[1]));
                writer.flush();
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            if (e instanceof SocketException) {
                ProxyServer.getInstance().getLogger().warning("Server" + socket.getInetAddress() + " Connection has disconnected: " + e.getMessage());
                return;
            }
            e.printStackTrace();
        }
    }

}

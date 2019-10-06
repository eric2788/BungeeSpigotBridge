package com.ericlam.mc.bsbridge.spigot;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Messager {
    private final PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;

    public Messager(Socket socket) throws IOException {
        this.socket = socket;
        writer = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
    }

    synchronized boolean verify(final UUID uuid, final UUID randomUUID) throws IOException {
        if (uuid == null || randomUUID == null) return false;
        Bukkit.getLogger().info("[DEBUG] Sending: " + uuid.toString() + " and " + randomUUID.toString());
        String msg = SpigotPlugin.cyberKey.encrypt(uuid, randomUUID);
        Bukkit.getLogger().info("[DEBUG] send-message: " + msg);
        writer.println(msg);
        writer.flush();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        String str = reader.readLine();
        if (str == null) {
            Bukkit.getLogger().warning("You have disconnected with bungee");
            return false;
        }
        if (str.isEmpty()) return false;
        return SpigotPlugin.cyberKey.verify(str, randomUUID);
    }

    void close() {
        writer.close();
    }
}

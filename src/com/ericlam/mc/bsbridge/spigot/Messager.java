package com.ericlam.mc.bsbridge.spigot;

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
        System.out.println("Sending: " + uuid.toString() + " and " + randomUUID.toString());
        String msg = SpigotPlugin.cyberKey.encrypt(uuid, randomUUID);
        System.out.println("send-message: " + msg);
        writer.println(msg);
        writer.flush();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        String str = reader.readLine();
        if (str == null) {
            System.out.println("已與伺服器中斷了連接");
            return false;
        }
        if (str.isEmpty()) return false;
        return SpigotPlugin.cyberKey.verify(str, randomUUID);
    }

    void close() {
        writer.close();
    }
}

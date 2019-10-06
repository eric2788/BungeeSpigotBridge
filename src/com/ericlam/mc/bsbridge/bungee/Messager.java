package com.ericlam.mc.bsbridge.bungee;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class Messager {
    private final PrintWriter writer;

    public Messager(Socket socket) throws IOException {
        writer = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
    }


    void message(UUID uuid){
        if (uuid == null) return;
        System.out.println("Sending: "+uuid.toString());
        String msg = BungeePlugin.cyberKey.encrypt(uuid);
        System.out.println("send-message: "+msg);
        writer.println(msg);
        writer.flush();

    }
}

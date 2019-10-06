package com.ericlam.mc.bsbridge.bungee;

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
                    System.out.println("分流 " + socket.getInetAddress() + " 已斷開連線。");
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }
                System.out.println("Received Message: "+line);
                UUID[] uuids = BungeePlugin.cyberKey.decrypt(line);
                if (uuids == null) continue;
                System.out.println("Received: " + Arrays.toString(uuids));
                writer.println(BungeePlugin.cyberKey.encrypt(uuids[1]));
                writer.flush();
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            if (e instanceof SocketException) {
                System.out.println("分流 " + socket.getInetAddress() + " 強制中斷了連線: " + e.getMessage());
                return;
            }
            e.printStackTrace();
        }
    }

}

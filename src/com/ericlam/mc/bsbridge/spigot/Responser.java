package com.ericlam.mc.bsbridge.spigot;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

class Responser {


    Responser(Socket socket) throws IOException {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {
            BufferedReader reader;
            String line = "";
            while (true) {
                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                line = reader.readLine();
                if (line == null){
                    System.out.println("Client disconnected");
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }
                System.out.println("Received Message: "+line);
                UUID uuid = SpigotPlugin.cyberKey.decrypt(line);
                if (uuid == null) continue;
                System.out.println("Received: " + uuid.toString());
                SpigotPlugin.uuidList.add(uuid);
            }
            reader.close();
        }
    }


}

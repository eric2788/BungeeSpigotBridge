package com.ericlam.mc.bsbridge.bungee;

import com.ericlam.mc.bsbridge.CyberKey;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class BungeePlugin extends Plugin {

    static CyberKey cyberKey;

    @Override
    public void onEnable() {
        try {
            ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            if (!getDataFolder().exists()) getDataFolder().mkdir();
            File file = new File(getDataFolder(), "server.yml");
            if (!file.exists()) {
                InputStream stream = getResourceAsStream("server.yml");
                Files.copy(stream, file.toPath());
            }
            Configuration config = provider.load(file);
            cyberKey = new CyberKey(new File(getDataFolder(), "cyberkey.aes"));
            int port = config.getInt("port");
            getProxy().getScheduler().runAsync(this, () -> {
                try {
                    cyberKey = new CyberKey(new File(getDataFolder(), "cyberkey.aes"));
                    ServerSocket socket = new ServerSocket(port);
                    launchConnection(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void launchConnection(ServerSocket serverSocket) {
        getLogger().info("正在監察端口 " + serverSocket.getLocalPort() + "....");
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
                continue;
            }
            getLogger().info("分流已連接：" + socket.getRemoteSocketAddress());
            new Responser(socket).start();
        }
    }
}

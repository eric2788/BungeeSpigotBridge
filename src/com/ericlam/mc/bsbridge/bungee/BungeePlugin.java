package com.ericlam.mc.bsbridge.bungee;

import com.ericlam.mc.bsbridge.CyberKey;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BungeePlugin extends Plugin implements Listener {

    static CyberKey cyberKey;
    static Set<UUID> queue = new HashSet<>();

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
            getProxy().getPluginManager().registerListener(this, this);
            getProxy().getScheduler().runAsync(this, () -> {
                try {
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

    @EventHandler
    public void onDisconnect(final PlayerDisconnectEvent e) {
        queue.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLogin(final ServerConnectEvent e) {
        queue.add(e.getPlayer().getUniqueId());
    }


    private void launchConnection(ServerSocket serverSocket) {
        getLogger().info("Listening the socket port " + serverSocket.getLocalPort() + "....");
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
                continue;
            }
            getLogger().info("Server Connected：" + socket.getRemoteSocketAddress());
            new Responser(socket).start();
        }
    }
}

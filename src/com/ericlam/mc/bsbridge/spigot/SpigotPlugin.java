package com.ericlam.mc.bsbridge.spigot;

import com.ericlam.mc.bsbridge.CyberKey;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SpigotPlugin extends JavaPlugin implements Listener {

    static List<UUID> uuidList = new LinkedList<>();
    static CyberKey cyberKey;

    private Responser responser;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        File serverConfig = new File(getDataFolder(), "server.yml");
        if (!serverConfig.exists()) saveResource("server.yml", true);
        int port = YamlConfiguration.loadConfiguration(serverConfig).getInt("port");
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                cyberKey = new CyberKey(new File(getDataFolder(), "cyberkey.aes"));
                ServerSocket socket = new ServerSocket(port);
                launchConnection(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void launchConnection(ServerSocket socket) throws IOException {
        try {
            getLogger().info("Listening port " + socket.getLocalPort() + "....");
            Socket server = socket.accept();
            getLogger().info("IP Connected：" + server.getRemoteSocketAddress());
            responser = new Responser(server);
        } catch (IOException e) {
            /*
            if (e instanceof EOFException) {
                System.out.println("Client has closed its connection");
                System.out.println("Waiting for new connection...");
                launchConnection(socket);
            } else {
                e.printStackTrace();
            }

             */
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (uuidList.remove(e.getPlayer().getUniqueId())) return;
        e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        e.setKickMessage("§eYou are not allowed to join without go through our own proxy.");
    }
}

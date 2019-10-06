package com.ericlam.mc.bsbridge.spigot;

import com.ericlam.mc.bsbridge.CyberKey;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.UUID;

public class SpigotPlugin extends JavaPlugin implements Listener {

    static CyberKey cyberKey;
    private Messager messager;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        File serverConfig = new File(getDataFolder(), "connect.yml");
        if (!serverConfig.exists()) saveResource("connect.yml", true);
        FileConfiguration config = YamlConfiguration.loadConfiguration(serverConfig);
        int port = config.getInt("port");
        String host = Optional.ofNullable(config.getString("host")).orElse("127.0.0.1");
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                cyberKey = new CyberKey(new File(getDataFolder(), "cyberkey.aes"));
                Socket socket = new Socket();
                getLogger().info("正在嘗試連線到 " + host + ":" + port);
                socket.connect(new InetSocketAddress(host, port), 30000);
                socket.setKeepAlive(true);
                socket.setSoTimeout(10000);
                if (socket.isConnected()) {
                    getLogger().info("伺服器連線成功");
                } else {
                    getLogger().info("伺服器連線失敗");
                    return;
                }
                messager = new Messager(socket);
            } catch (Exception e) {
                if (e instanceof SocketException) {
                    System.out.println("伺服器已中斷連接，請重啟伺服器");
                    return;
                }
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onDisable() {
        messager.close();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        try {
            if (messager.verify(e.getPlayer().getUniqueId(), UUID.randomUUID())) {
                getLogger().info("Verified Successfully.");
                return;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        getLogger().info("Verified Failed.");
        e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        e.setKickMessage("§eYou are not allowed to join without go through our own proxy.");
    }
}

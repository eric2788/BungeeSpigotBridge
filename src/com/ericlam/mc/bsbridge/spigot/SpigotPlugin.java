package com.ericlam.mc.bsbridge.spigot;

import com.ericlam.mc.bsbridge.CyberKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class SpigotPlugin extends JavaPlugin implements Listener {

    static CyberKey cyberKey;
    private Messager messager;
    private FileConfiguration config;
    private String unknownProxyMsg, verifyFailedMsg;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        File serverConfig = new File(getDataFolder(), "connect.yml");
        if (!serverConfig.exists()) saveResource("connect.yml", true);
        config = YamlConfiguration.loadConfiguration(serverConfig);
        unknownProxyMsg = ChatColor.translateAlternateColorCodes('&', Optional.ofNullable(config.getString("messages.unknown-proxy")).orElse("&eYou are not allowed to join without go through our own proxy."));
        verifyFailedMsg = ChatColor.translateAlternateColorCodes('&', Optional.ofNullable(config.getString("messages.verify-failed")).orElse("&eVerification Failed, Please try again."));
        launchConnection();
    }

    private void launchConnection() {
        int port = config.getInt("port");
        String host = Optional.ofNullable(config.getString("host")).orElse("127.0.0.1");
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            while (true) {
                try {
                    cyberKey = new CyberKey(new File(getDataFolder(), "cyberkey.aes"));
                    Socket socket = new Socket();
                    getLogger().info("Trying to connect with bungee " + host + ":" + port);
                    socket.connect(new InetSocketAddress(host, port), 30000);
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(10000);
                    if (socket.isConnected()) {
                        getLogger().info("Server Connected Successfully");
                    } else {
                        getLogger().log(Level.SEVERE, "Server Connection failed");
                        return;
                    }
                    messager = new Messager(socket);
                } catch (Exception e) {
                    getLogger().warning("Connection Error: " + e.getMessage());
                    continue;
                }
                break;
            }
        });
    }


    @Override
    public void onDisable() {
        if (messager != null) messager.close();
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
        try {
            if (messager.verify(e.getUniqueId(), UUID.randomUUID())) {
                getLogger().info("Verified Successfully.");
                return;
            }
        } catch (IOException ex) {
            getLogger().warning("Connection Error: " + ex.getMessage());
            launchConnection();
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, this.verifyFailedMsg);
            return;
        }
        getLogger().warning("Verify Failed.");
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, this.unknownProxyMsg);
    }
}

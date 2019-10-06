package com.ericlam.mc.bsbridge.bungee;

import com.ericlam.mc.bsbridge.CyberKey;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class BungeePlugin extends Plugin implements Listener {

    static CyberKey cyberKey;
    private Messager messager;

    @Override
    public void onEnable() {
        try{
            ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            if (!getDataFolder().exists()) getDataFolder().mkdir();
            File file = new File(getDataFolder(), "connect.yml");
            if (!file.exists()) {
                InputStream stream = getResourceAsStream("connect.yml");
                Files.copy(stream, file.toPath());
            }
            Configuration config = provider.load(file);
            cyberKey = new CyberKey(new File(getDataFolder(), "cyberkey.aes"));
            String host = config.getString("host");
            int port = config.getInt("port");
            getProxy().getScheduler().runAsync(this,()->{
                try{
                    Socket socket = new Socket();
                    getLogger().info("We are trying to connect to "+host+":"+port);
                    socket.connect(new InetSocketAddress(host, port), 20000);
                    if (socket.isConnected()) {
                        System.out.println(">> Connected successfully");
                    } else {
                        System.out.println(">> Cannot connect to that server");
                        return;
                    }
                    socket.setKeepAlive(true);
                    messager = new Messager(socket);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        getProxy().getPluginManager().registerListener(this, this);
    }



    @EventHandler
    public void onLogin(final PostLoginEvent e){
        messager.message(e.getPlayer().getUniqueId());
    }
}

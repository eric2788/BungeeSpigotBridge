# BungeeSpigotBridge
a Test Plugin to avoid Bungee UUID Spoof hack through preventing player using non-verified proxy to join your spigot servers.

## What's the plugin for ?
This plugin prevent player using non-verified proxy to join your spigot servers. Unlike any ipwhitelist plugin, this plugin using socket communication to ensure the connection is legal instead of checking the IP from player so that to prevent IP Spoofing for bypassing the ipwhitelist. **you can use it for anti-uuid spoofing and prevent ipwhitelist bypass.**

**THE PLUGIN WON'T WORK IF YOU ARE NOT ON BUNGEECORD NETWORK!**

## How's the plugin work ?
The plugin adds the player to a queue when joining with owner bungee.
Then when spigot server gonna find out whether the player is go through from owner bungee, it will send a socket message to owner bungee,
and the owner bungee will response whether the queue has contained the player. After verification, that player will be removed from the queue until next join.

## Installation
1. Install the jar into 
  - your bungeecord 
  - the spigot server which you have opened the port **(Potentially Scannable)**
2. launch the server and close it.
3. Now setup the yml and make the key with both bungeecord and spigot server you installed are same.
4. If your bungeecord and spigot are in different host, make sure you have already opened the port for socket
5. launch the server and test it.

## Compatible With
- Spigot
- Paper
- BungeeCord
- WaterFall
- **Offline Mode Bungee / WaterFall**


## Disadvantage
Well the only disadvantage I must say is using the socket connection that you need to open an extra port for socket. Please let me know if you want to have a redis version or you can fork the repo to make your own version.

## Download
  You can download from [here](http://www.mediafire.com/file/yl75fip8x3z6he7/BungeeSpigotBridge.jar/file)
  
  
## Showcase
  You can check here (https://youtu.be/2IszeOf7KwU)
  

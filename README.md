# BungeeSpigotBridge
a Test Plugin to avoid Bungee UUID Spoof hack

## What's the plugin for ?
This plugin prevent the uuid spoofing via socket connection. Unlike any anti uuid spoofing plugin, this plugin checks whether the player has gone through your own proxy to ensure the connection is legal instead of verifying the uuid.

## How's the plugin prevent ?
The plugin adds the player to a queue when joining with owner bungee.
Then when spigot server gonna find out whether the player is go through from owner bungee, it will send a socket message to owner bungee,
and the owner bungee will response whether the queue has contained the player. After verification, that player will be removed from the queue until next join.

## Installation
1. Install the jar into 
  - your bungeecord 
  - the spigot server which you have opened the port **(Always install into the server which bungee will first jump into!)**
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


## Draw backs:
 - You need to open an extra port for socket connection

## Download
  You can download from [here](http://www.mediafire.com/file/yl75fip8x3z6he7/BungeeSpigotBridge.jar/file)
  
  
## Showcase
  You can check here (https://youtu.be/2IszeOf7KwU)
  

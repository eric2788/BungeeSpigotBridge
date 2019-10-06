# BungeeSpigotBridge
a Test Plugin to avoid Bungee UUID Spoof hack

## How The Plugin Prevent ?
The plugin adds the player to a queue when joining with owner bungee.
Then when spigot server gonna find out whether the player is go through from owner bungee, it will send a socket message to owner bungee,
and the owner bungee will response whether the queue has contained the player. After verification, that player will be removed from the queue until next join.

## Compatible With
- Spigot
- Paper
- BungeeCord
- WaterFall
- **Offline Mode Bungee / WaterFall**


## Draw backs:
 - You need open an extra port for socket connection

## Download
  You can download from [here](http://www.mediafire.com/file/yl75fip8x3z6he7/BungeeSpigotBridge.jar/file)
  
  
## Showcase
  You can check here (https://youtu.be/2IszeOf7KwU)
  

package me.txmc.chatshit.chat;

import me.txmc.chatshit.Main;
import me.txmc.chatshit.Utils;
import me.txmc.chatshit.chat.commands.*;
import me.txmc.chatshit.chat.listeners.ChatListener;
import me.txmc.chatshit.chat.listeners.JoinLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ChatManager {
    private final HashMap<UUID, ChatInfo> map;
    private FileConfiguration config;
    private File ignoresFolder;

    public ChatManager() {
        map = new HashMap<>();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getIgnoresFolder() {
        return ignoresFolder;
    }

    public void init(Main plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        File tldFile = new File(dataFolder, "tlds.txt");
        if (!tldFile.exists()) Utils.unpackResource("tlds.txt", tldFile);
        ignoresFolder = new File(dataFolder, "IgnoreLists");
        if (!ignoresFolder.exists()) ignoresFolder.mkdir();
        config = plugin.getConfig();
        plugin.registerListener(new ChatListener(this, parseTLDS(tldFile)));
        plugin.registerListener(new JoinLeaveListener(this));
        plugin.getCommand("ignore").setExecutor(new IgnoreCommand(this));
        plugin.getCommand("msg").setExecutor(new MessageCommand(this));
        plugin.getCommand("reply").setExecutor(new ReplyCommand(this));
        plugin.getCommand("togglechat").setExecutor(new ToggleChatCommand(this));
        plugin.getCommand("unignore").setExecutor(new UnIgnoreCommand(this));
        if (!Bukkit.getOnlinePlayers().isEmpty()) Bukkit.getOnlinePlayers().forEach(this::registerPlayer);
    }

    private HashSet<String> parseTLDS(File tldFile) {
        try {
            HashSet<String> buf = new HashSet<>();
            BufferedReader reader = new BufferedReader(new FileReader(tldFile));
            reader.lines().filter(l -> !l.startsWith("#")).forEach(s -> buf.add(s.toLowerCase()));
            reader.close();
            return buf;
        } catch (Throwable t) {
            Utils.log("&cFailed to parse the TLD file please see the stacktrace below for more info!");
            t.printStackTrace();
            return null;
        }
    }

    public void destruct(Main plugin) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            ChatInfo ci = getInfo(p);
            ci.saveIgnores();
        });
    }

    public void reloadConfig(FileConfiguration config) {
        this.config = config;
    }

    public void registerPlayer(Player player) {
        map.put(player.getUniqueId(), new ChatInfo(player, this));
    }

    public void removePlayer(Player player) {
        map.remove(player.getUniqueId());
    }

    public ChatInfo getInfo(Player player) {
        return map.getOrDefault(player.getUniqueId(), null);
    }
}

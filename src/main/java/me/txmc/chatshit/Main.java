package me.txmc.chatshit;

import me.txmc.chatshit.chat.ChatManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    ChatManager chatManager;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().addHandler(new LoggerHandler());
        chatManager = new ChatManager();
        instance = this;
        chatManager.init(this);
    }

    @Override
    public void onDisable() {
        chatManager.destruct(this);
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}

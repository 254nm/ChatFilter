package me.txmc.chatshit.chat.listeners;

import me.txmc.chatshit.chat.ChatInfo;
import me.txmc.chatshit.chat.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {
    private final ChatManager manager;

    public JoinLeaveListener(ChatManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        manager.registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        ChatInfo info = manager.getInfo(event.getPlayer());
        if (info == null) return;
        info.saveIgnores();
        manager.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        ChatInfo info = manager.getInfo(event.getPlayer());
        if (info == null) return;
        info.saveIgnores();
        manager.removePlayer(event.getPlayer());
    }
}

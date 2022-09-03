package me.txmc.chatshit.chat.listeners;

import me.txmc.chatshit.Utils;
import me.txmc.chatshit.chat.ChatInfo;
import me.txmc.chatshit.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {
    private final ChatManager manager;
    private final HashSet<String> tlds;
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();


    public ChatListener(ChatManager manager, HashSet<String> tlds) {
        this.manager = manager;
        this.tlds = tlds;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        int cooldown = manager.getConfig().getInt("Cooldown");
        ChatInfo ci = manager.getInfo(player);
        if (ci.isChatLock()) {
            Utils.sendMessage(player, String.format("&3Please wait at least&r&a %d&r&3 seconds between chat messages", cooldown));
            return;
        }
        ci.setChatLock(true);
        service.schedule(() -> ci.setChatLock(false), cooldown, TimeUnit.SECONDS);
        String ogMessage = event.getMessage();
        if (blockedCheck(ogMessage)) {
            player.sendMessage("<" + player.getDisplayName() + "> " + ogMessage);
            Utils.log("&3Prevented&r&a %s&r&3 from sending a message that has banned words", player.getName());
            return;
        }
        if (domainCheck(ogMessage)) {
            player.sendMessage("<" + player.getDisplayName() + "> " + ogMessage);
            Utils.log("&3Prevented player&r&a %s&r&3 from sending a link / server ip", player.getName());
            return;
        }
        String message = (ogMessage.startsWith(">")) ? "<" + player.getDisplayName() + "> " + ChatColor.GREEN + ogMessage : "<" + player.getDisplayName() + "> " + ogMessage;
        Bukkit.getLogger().info(message);
        for (Player online : Bukkit.getOnlinePlayers()) {
            ChatInfo info = manager.getInfo(online);
            if (info == null) continue;
            if (info.isIgnoring(player.getUniqueId()) || info.isToggledChat()) continue;
            online.sendMessage(message);
        }
    }

    private boolean domainCheck(String message) {
        message = message.toLowerCase().replace("dot", ".").replace("d0t", ".");
        if (message.indexOf('.') == -1) return false;
        String[] split = message.trim().split("\\.");
        if (split.length == 2) {
            String possibleTLD = split[1];
            if (possibleTLD.contains("/")) possibleTLD = possibleTLD.substring(0, possibleTLD.indexOf("/"));
            return tlds.contains(possibleTLD);
        } else {
            for (String word : split) {
                if (word.contains("/")) word = word.substring(0, word.indexOf("/"));
                if (tlds.contains(word)) return true;
            }
        }
        return false;
    }

    private boolean blockedCheck(String message) {
        List<String> blocked = manager.getConfig().getStringList("Blocked");
        for (String blockedWord : blocked) {
            if (message.toLowerCase().contains(blockedWord)) return true;
        }
        return false;
    }
}

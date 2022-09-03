package me.txmc.chatshit.chat.commands;

import me.txmc.chatshit.Utils;
import me.txmc.chatshit.chat.ChatInfo;
import me.txmc.chatshit.chat.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleChatCommand implements CommandExecutor {
    private final ChatManager manager;

    public ToggleChatCommand(ChatManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ChatInfo info = manager.getInfo((Player) sender);
            if (info.isToggledChat()) {
                Utils.sendMessage(sender, "&aEnabled chat!");
            } else Utils.sendMessage(sender, "&cDisabled chat!");
            info.setToggledChat(!info.isToggledChat());
        } else Utils.sendMessage(sender, "&cYou must be a player");
        return true;
    }
}

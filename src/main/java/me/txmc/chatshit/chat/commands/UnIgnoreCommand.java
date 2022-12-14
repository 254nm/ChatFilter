package me.txmc.chatshit.chat.commands;

import me.txmc.chatshit.Utils;
import me.txmc.chatshit.chat.ChatInfo;
import me.txmc.chatshit.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnIgnoreCommand implements CommandExecutor {
    private final ChatManager manager;

    public UnIgnoreCommand(ChatManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                ChatInfo info = manager.getInfo((Player) sender);
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (info.isIgnoring(target.getUniqueId())) {
                    info.unignorePlayer(target.getUniqueId());
                    Utils.sendMessage(sender, "&3Successfully unignored player&r&a " + target.getName());
                } else Utils.sendMessage(sender, "&cYou arent ignoring " + target.getName());
            } else Utils.sendMessage(sender, "&cPlease include a player /unignore <playerName>");
        } else Utils.sendMessage(sender, "&cYou must be a player");
        return true;
    }
}

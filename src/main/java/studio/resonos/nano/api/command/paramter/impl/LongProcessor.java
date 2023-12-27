package studio.resonos.nano.api.command.paramter.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import studio.resonos.nano.api.command.paramter.Processor;

public class LongProcessor extends Processor<Long> {
    public Long process(CommandSender sender, String supplied) {
        try {
            return Long.parseLong(supplied);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "The value you entered '" + supplied + "' is an invalid long.");
            return 0L;
        }
    }
}

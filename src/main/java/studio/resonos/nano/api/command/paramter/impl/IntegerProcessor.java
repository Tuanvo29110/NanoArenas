package studio.resonos.nano.api.command.paramter.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import studio.resonos.nano.api.command.paramter.Processor;

public class IntegerProcessor extends Processor<Integer> {
    public Integer process(CommandSender sender, String supplied) {
        try {
            return Integer.parseInt(supplied);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "The value you entered '" + supplied + "' is an invalid integer.");
            return 0;
        }
    }
}

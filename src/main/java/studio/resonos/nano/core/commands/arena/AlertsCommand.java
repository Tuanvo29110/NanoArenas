package studio.resonos.nano.core.commands.arena;

import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.managers.AdminAlertManager;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.NanoArenas;


public class AlertsCommand {

    // usage: /alerts toggle  OR  /alerts on  OR  /alerts off
    @Command(names = {"nano alerts"}, permission = "nano.alerts", playerOnly = true)
    public void Command(Player player, @Param(name = "action", required = false) String action) {
        AdminAlertManager manager = NanoArenas.get().getManager();
        if (action == null || action.equalsIgnoreCase("toggle")) {
            boolean enabled = manager.toggle(player);
            player.sendMessage(CC.translate(enabled ? "&aArena alerts enabled." : "&cArena alerts disabled."));
            return;
        }

        if (action.equalsIgnoreCase("on")) {
            manager.setEnabled(player, true);
            player.sendMessage(CC.translate("&aArena alerts enabled."));
            return;
        }

        if (action.equalsIgnoreCase("off")) {
            manager.setEnabled(player, false);
            player.sendMessage(CC.translate("&cArena alerts disabled."));
            return;
        }

        player.sendMessage(CC.translate("&eUsage: /alerts toggle|on|off"));
    }
}
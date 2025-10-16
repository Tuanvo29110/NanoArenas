package studio.resonos.nano.core.arena.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import studio.resonos.nano.api.event.ArenaResetEvent;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.core.managers.AdminAlertManager;

public class ArenaResetBroadcastListener implements Listener {

    private final AdminAlertManager manager;

    public ArenaResetBroadcastListener(AdminAlertManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onArenaReset(ArenaResetEvent event) {
        String arenaName = event.getArena().getName();
        long duration = event.getDurationMillis();

        String message = ChatColor.translateAlternateColorCodes('&',
                "&8[&bNanoArenas&8] &fArena &b" + arenaName + "&f has been reset " + "(&e" + event.getSize() + "&f)" +"(&a" + duration + "ms&f).");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("nano.alerts")) continue;
            if (!manager.isEnabled(player)) continue;
            player.sendMessage(message);
        }
    }
}
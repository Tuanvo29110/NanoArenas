package studio.resonos.nano.core.managers;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which players have admin alerts enabled.
 * Default is OFF (players must toggle on with the command).
 */
public class AdminAlertManager {

    private final Map<UUID, Boolean> enabled = new ConcurrentHashMap<>();

    /**
     * Returns true if alerts are enabled for the player.
     * If a player has no entry, defaults to false.
     */
    public boolean isEnabled(Player player) {
        if (player == null) return false;
        return enabled.getOrDefault(player.getUniqueId(), false);
    }

    /**
     * Toggle the player's alert state and return the new state.
     */
    public boolean toggle(Player player) {
        if (player == null) return false;
        UUID id = player.getUniqueId();
        boolean next = !enabled.getOrDefault(id, false);
        enabled.put(id, next);
        return next;
    }

    /**
     * Explicitly set enabled state for a player.
     */
    public void setEnabled(Player player, boolean on) {
        if (player == null) return;
        enabled.put(player.getUniqueId(), on);
    }

    /**
     * Remove player entry (e.g. on logout) if desired.
     */
    public void remove(Player player) {
        if (player == null) return;
        enabled.remove(player.getUniqueId());
    }
}
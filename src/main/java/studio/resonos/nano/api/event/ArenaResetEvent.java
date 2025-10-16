package studio.resonos.nano.api.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import studio.resonos.nano.core.arena.Arena;

/**
 * Fired after an arena reset, includes how long the reset took (in milliseconds).
 */
@Getter
public class ArenaResetEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    /**
     * -- GETTER --
     *  Duration in milliseconds it took to perform the reset.
     */
    private final long durationMillis;
    /**
     * -- GETTER --
     *  Size of the schematic used to reset the arena, in bytes.
     */
    private final long size;

    public ArenaResetEvent(Arena arena, long durationMillis, long size) {
        this.arena = arena;
        this.durationMillis = durationMillis;
        this.size = size;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
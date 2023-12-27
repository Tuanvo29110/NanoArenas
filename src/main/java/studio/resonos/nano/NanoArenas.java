package studio.resonos.nano;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.api.command.CommandHandler;
import studio.resonos.nano.api.gui.SpiGUI;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.ArenaListener;
import studio.resonos.nano.core.util.file.type.BasicConfigurationFile;

import java.util.Arrays;
import java.util.Collections;

/**
 * @Author Athishh
 * Package: studio.resonos.arenas.core.arena.generator
 * Created on: 12/16/2023
 */

@Getter
@Setter
public class NanoArenas extends JavaPlugin {

    public static SpiGUI spiGUI;
    private static NanoArenas nanoArenas;
    @Getter
    private BasicConfigurationFile arenasConfig;

    public static NanoArenas get() {
        return nanoArenas;
    }

    @Override
    public void onEnable() {
        nanoArenas = this;
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        spiGUI = new SpiGUI(this);
        Arena.init();
        Collections.singletonList(
                new ArenaListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        registerProcessors();
        registerCommands();
        new BukkitRunnable() {
            @Override
            public void run() {
                NanoArenas.get().getLogger().info("Started Reset timer Task");
                scheduleArenaResets();
            }
        }.runTaskTimer(this, 0, 8400);
    }

    @Override
    public void onDisable() {
        Arena.getArenas().forEach(Arena::reset);
        Arena.getArenas().forEach(Arena::save);
    }

    private void registerProcessors() {
        CommandHandler.registerProcessors("studio.resonos.arenas.api.command.processors", this);
    }

    private void registerCommands() {
        CommandHandler.registerCommands("studio.resonos.arenas.core.commands.arena", this);
    }

    private void scheduleArenaResets() {
        for (Arena arena : Arena.getArenas()) {
            if (!(arena.getResetTime() > 0)) {
                return;
            }
            long resetDelay = arena.getResetTime();
            new BukkitRunnable() {
                @Override
                public void run() {
                    arena.reset();

                    scheduleArenaResets();
                }
            }.runTaskLater(this, resetDelay);

            break;
        }
    }

}

package studio.resonos.nano.core.arena.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.gui.buttons.SGButton;
import studio.resonos.nano.api.gui.menu.SGMenu;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.ItemBuilder;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.menu
 * Created on: 12/16/2023
 */
public class ArenaManagementMenu {

    public static void openMenu(Player player) {

        // Create a GUI with calculated rows
        SGMenu Menu = NanoArenas.spiGUI.create("&bArena Management &c[ADMIN]", 2);

        Menu.setAutomaticPaginationEnabled(true);
        if (Arena.getArenas().isEmpty()) {
            player.sendMessage(CC.translate("&cThere are no arenas"));
            return;
        }

        for (Arena arena : Arena.getArenas()) {
            Menu.addButton(new SGButton(new ItemBuilder(arena.getIcon())
                    .name("&9&l" + arena.getName())
                    .lore("")
                    .lore("&bArena Information:")
                    .lore("   &fIs Setup: &b" + (arena.isSetup() ? "&a✓" : "&c✗"))
                    .lore("   &fResetDelay: &b" + arena.getResetTime() + "s")
                    .lore("   &fNext Reset: &b" + NanoArenas.get().getResetScheduler().getRemainingSeconds(arena) + "s")
                    .lore("   &fStatus: &b" + (!arena.isAutoResetPaused() ? "&aAUTO" : "&cPAUSED"))
                    .lore("")
                    .lore("&b&lLEFT-CLICK &bto teleport to arena.")
                    .lore("&b&lRIGHT-CLICK &bto pause auto-resets.")
                    .lore("&b&lMIDDLE-CLICK &bto delete arena.")
                    .build())
                    .withListener((InventoryClickEvent click) -> {
                        switch (click.getClick()) {
                            case LEFT:
                                player.performCommand("arena teleport " + arena.getName());
                                break;
                            case RIGHT:
                                player.performCommand("arena pause " + arena.getName());
                                player.performCommand("arena manage");
                                break;
                            case MIDDLE:
                                player.performCommand("arena delete " + arena.getName());
                                player.performCommand("arena manage");
                                break;
                        }
                    }));

            // Show the GUI
            player.openInventory(Menu.getInventory());

        }
    }
}

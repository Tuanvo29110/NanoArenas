package studio.resonos.nano.api.gui.toolbar;

import org.bukkit.Material;
import org.bukkit.event.Event;
import studio.resonos.nano.api.gui.SpiGUI;
import studio.resonos.nano.api.gui.buttons.SGButton;
import studio.resonos.nano.api.gui.item.ItemBuilder;
import studio.resonos.nano.api.gui.menu.SGMenu;

/**
 * The default implementation of {@link SGToolbarBuilder}.
 * <br>
 * This class is used by default by SpiGUI, but you can override this class by
 * extending it and passing your custom implementation to
 * {@link SpiGUI#setDefaultToolbarBuilder(SGToolbarBuilder)}
 * (or to use it for a specific menu, pass it to
 * {@link SGMenu#setToolbarBuilder(SGToolbarBuilder)}).
 */
public class SGDefaultToolbarBuilder implements SGToolbarBuilder {

    @Override
    public SGButton buildToolbarButton(int slot, int page, SGToolbarButtonType type, SGMenu menu) {
        switch (type) {
            case PREV_BUTTON:
                if (menu.getCurrentPage() > 0) return new SGButton(new ItemBuilder(Material.LEVER)
                        .name("&c← &dPrevious Page")
                        .lore(
                                "&aClick to move back to",
                                "&apage " + menu.getCurrentPage() + ".")
                        .build()
                ).withListener(event -> {
                    event.setResult(Event.Result.DENY);
                    menu.previousPage(event.getWhoClicked());
                });
                else return null;

            case CURRENT_BUTTON:
                return new SGButton(new ItemBuilder(Material.CLOCK)
                        .name("&fPage &d" + (menu.getCurrentPage() + 1) + " &7of &d" + menu.getMaxPage())
                        .lore(
                                "&fCurrently viewing",
                                "&fpage &d" + (menu.getCurrentPage() + 1) + "."
                        ).build()
                ).withListener(event -> event.setResult(Event.Result.DENY));

            case NEXT_BUTTON:
                if (menu.getCurrentPage() < menu.getMaxPage() - 1) return new SGButton(new ItemBuilder(Material.LEVER)
                        .name("&dNext Page &d→")
                        .lore(
                                "&fClick to move forward to",
                                "&fpage &d" + (menu.getCurrentPage() + 2) + "."
                        ).build()
                ).withListener(event -> {
                    event.setResult(Event.Result.DENY);
                    menu.nextPage(event.getWhoClicked());
                });
                else return null;

            case UNASSIGNED:
            default:
                return null;
        }
    }

}

package studio.resonos.nano.core.arena;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import studio.resonos.nano.core.arena.selection.Selection;
import studio.resonos.nano.core.util.CC;

public class ArenaListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        ItemStack item = event.getItem();

        if (item != null && item.equals(Selection.SELECTION_WAND)) {
            Player player = event.getPlayer();
            Block clicked = event.getClickedBlock();
            int location = 0;

            Selection selection = Selection.createOrGetSelection(player);

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                selection.setPoint2(clicked.getLocation());
                location = 2;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                selection.setPoint1(clicked.getLocation());
                location = 1;
            }

            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);

            String message = CC.AQUA + (location == 1 ? "First" : "Second") +
                    " location " + CC.YELLOW + "(" + CC.GREEN +
                    clicked.getX() + CC.YELLOW + ", " + CC.GREEN +
                    clicked.getY() + CC.YELLOW + ", " + CC.GREEN +
                    clicked.getZ() + CC.YELLOW + ")" + CC.AQUA + " has been set!";

            if (selection.isFullObject()) {
                message += CC.RED + " (" + CC.YELLOW + selection.getCuboid().volume() + CC.AQUA + " blocks" +
                        CC.RED + ")";
            }

            player.sendMessage(message);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        for (Arena arena : Arena.getArenas()) {
            if (arena.contains(event.getToBlock().getLocation())) {
                arena.getChangedBlocks().add(event.getBlock().getState());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void explodeEvent(EntityExplodeEvent event) {

        for (Arena arena : Arena.getArenas()) {
            for (Block block : event.blockList()) {
                if (arena.contains(block.getLocation())) {
                    arena.getChangedBlocks().add(block.getState());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation();
        for (Arena arena : Arena.getArenas()) {
            if (arena.contains(loc)) {
                arena.getPlacedBlocks().add(loc);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        for (Arena arena : Arena.getArenas()) {
            if (arena.contains(loc)) {
                arena.getPlacedBlocks().remove(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        for (Arena arena : Arena.getArenas()) {
            if (arena.contains(event.getEntity().getLocation())) {
                arena.getEntities().add(event.getEntity());
            }
        }
    }

}

package studio.resonos.nano.core.arena;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.core.arena.impl.StandaloneArena;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.ItemBuilder;
import studio.resonos.nano.core.util.LocationUtil;
import studio.resonos.nano.core.util.cuboid.Cuboid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arena extends Cuboid {

    /*
     * Contains all arenas & kits of arenas
     * */
    @Getter
    private static final List<Arena> arenas = new ArrayList<>();
    @Getter
    private static final List<String> arenaNames = new ArrayList<>();

    @Getter private static final List<Entity> entities = new ArrayList<>();
    /*
     * Arena metadata
     * */
    @Getter
    protected String name, displayName;
    @Getter
    protected boolean active;
    @Setter
    protected ItemStack icon;
    @Setter
    protected Location spawn;
    @Getter
    @Setter
    protected int resetTime;
    @Getter
    @Setter
    private List<String> kits = new ArrayList<>();
    @Getter
    @Setter
    private List<Location> placedBlocks;
    @Getter
    @Setter
    private List<BlockState> changedBlocks;


    /*
     * Default arena constructor
     * */
    public Arena(String name, Location location1, Location location2) {
        super(location1, location2);
        this.name = name;
        this.displayName = CC.translate("&d&l" + name);
        this.icon = getUniqueIcon();
        this.placedBlocks = new ArrayList<>();
        this.changedBlocks = new ArrayList<>();
    }

    public static void init() {
        FileConfiguration configuration = NanoArenas.get().getArenasConfig().getConfiguration();

        if (configuration.contains("arenas")) {
            for (String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName;

                Location location1 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location1"));
                Location location2 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location2"));

                Arena arena;

                arena = new StandaloneArena(arenaName, location1, location2);

                if (configuration.contains(path + ".icon")) {
                    arena.setIcon(new ItemBuilder(Material.valueOf(configuration.getString(path + ".icon.material")))
                            .durability(configuration.getInt(path + ".icon.durability"))
                            .build());
                }
                if (configuration.contains(path + ".spawn")) {
                    arena.setSpawn(LocationUtil.deserialize(configuration.getString(path + ".spawn")));
                }
                if (configuration.contains(path + ".resetDelay")) {
                    arena.setResetTime(configuration.getInt(path + ".resetDelay"));
                }

                Arena.getArenaNames().add(arena.getName());
                Arena.getArenas().add(arena);
            }
        }

        NanoArenas.get().getLogger().info("Loaded " + Arena.getArenas().size() + " arenas");
    }

    public static Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null &&
                    arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public static Material getRandomMaterial() {
        // Add all possible materials to the array
        Material[] materials = Material.values();

        // Get a random material from the array
        Random random = new Random();
        return materials[random.nextInt(materials.length)];
    }

    public static boolean isIconUnique(Material icon) {
        // Check if the given icon is unique among all arenas
        for (Arena arena : arenas) {
            if (arena.getIcon().getType() == icon) {
                return false; // Icon is not unique
            }
        }
        return true; // Icon is unique

    }

    public static ItemStack getUniqueIcon() {
        Material randIcon;
        // Iterate through arenas and check for unique icons
        do {
            randIcon = getRandomMaterial();
        } while (!isIconUnique(randIcon));

        return new ItemStack(randIcon);
    }

    public ArenaType getType() {
        return ArenaType.DUPLICATE;
    }

    public boolean isSetup() {
        return getLowerCorner() != null && getUpperCorner() != null && spawn != null;
    }

    public Location getSpawn() {
        if (spawn == null) {
            return null;
        }

        return spawn.clone();
    }

    public void save() {

    }

    public void delete() {
        arenas.remove(this);
    }

    public ItemStack getIcon() {
        return this.icon.clone();
    }

    public void reset() {
        NanoArenas.get().getLogger().info("Resetting arena: " + this.getName());
        NanoArenas.get().getLogger().info("Blocks to reset: " + getPlacedBlocks().size() + getChangedBlocks().size());
        getChangedBlocks().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
        FaweAPI.getTaskManager().async(() -> {
            long setupTime = System.currentTimeMillis();
            com.sk89q.worldedit.world.World worldeez = BukkitAdapter.adapt(getSpawn().getWorld());
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldeez, Integer.MAX_VALUE);
            if (!this.getPlacedBlocks().isEmpty()) {
                editSession.setFastMode(true);

                for (Location location : this.getPlacedBlocks()) {
                    try {
                        editSession.setBlock(
                                new BlockVector3() {
                                    @Override
                                    public int getX() {
                                        return location.getBlockX();
                                    }

                                    @Override
                                    public int getY() {
                                        return location.getBlockY();
                                    }

                                    @Override
                                    public int getZ() {
                                        return location.getBlockZ();
                                    }
                                }, new BaseBlock(0, 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                editSession.flushQueue();
                this.getPlacedBlocks().clear();
            }
			/*if (!this.getChangedBlocks().isEmpty()) {
				editSession.setFastMode(true);

				for (BlockState blockState : this.getChangedBlocks()) {

					try {
						editSession.setBlock(
								new BlockVector3() {
									@Override
									public int getX() {
										return blockState.getX();
									}

									@Override
									public int getY() {
										return  blockState.getY();
									}
									@Override
									public int getZ() {
										return  blockState.getZ();
									}
								}, new BaseBlock( blockState.getBlock().getType().getId(),  blockState.getBlock().getData()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				editSession.flushQueue();
				this.getChangedBlocks().clear();
			}*/
            for (Entity entity: getEntities()) {
                if (Bukkit.getWorld(this.getWorld().getName()).getEntities().contains(entity)) {
                    entity.remove();
                }
            }
            this.getChangedBlocks().clear();
            long setupEndTime = System.currentTimeMillis();
            long durationSetup = setupEndTime - setupTime;
            NanoArenas.get().getLogger().info(durationSetup + " ms taken to reset arena: " + this.getName());
        });
    }
}

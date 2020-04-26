package com.sagaciousdevelopment.ExplodingSilkSpawners.handler.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sagaciousdevelopment.ExplodingSilkSpawners.Core;

public class SpawnerListener implements Listener{
	
	 private ArrayList<Location> getCircle(Location center, double radius, int amount)
	    {
	        World world = center.getWorld();
	        double increment = (2 * Math.PI) / amount;
	        ArrayList<Location> locations = new ArrayList<Location>();
	        for(int i = 0;i < amount; i++)
	        {
	            double angle = i * increment;
	            double x = center.getX() + (radius * Math.cos(angle));
	            double z = center.getZ() + (radius * Math.sin(angle));
	            locations.add(new Location(world, x, center.getY(), z));
	        }
	        return locations;
	    }
	
	private int starting_lives;
	private boolean silk;
	private int silkchance;
	
	private String silkbroken;
	private String silkpickup;
	private String livesremaining;
	private Random r = new Random();
	private List<ItemStack> drops;
	
	public SpawnerListener() {
		Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
		FileConfiguration conf = Core.getInstance().getConfig();
		starting_lives = conf.getInt("starting-lives");
		silk = conf.getBoolean("silktouch");
		silkchance = conf.getInt("silk-chance");
		silkbroken = ChatColor.translateAlternateColorCodes('&', conf.getString("silkbroken"));
		silkpickup = ChatColor.translateAlternateColorCodes('&', conf.getString("silkpickup"));
		livesremaining = ChatColor.translateAlternateColorCodes('&', conf.getString("livesremaining"));
		drops = new ArrayList<ItemStack>();
		for(String f : conf.getStringList("drops")) {
			Material m = Material.valueOf(f.split("/")[0]);
			Integer z = Integer.parseInt(f.split("/")[1]);
			if(m!=null) {
				drops.add(new ItemStack(m, z));
			}else {
				Core.getInstance().getLogger().info(f+" is null");
			}
		}
	}
	
	@EventHandler
	public void onSpawnerPlace(BlockPlaceEvent e) {
		if(e.getItemInHand().getType().equals(Material.MOB_SPAWNER)) {
			if(e.getItemInHand().hasItemMeta()) {
			ItemMeta is = e.getItemInHand().getItemMeta();
			if(is.hasLore()&&is.getLore().get(0).contains("Entity Type:")) {
						BlockState bs = e.getBlock().getState();
						CreatureSpawner cs = (CreatureSpawner)bs;
						cs.setSpawnedType(EntityType.valueOf(is.getLore().get(0).split(":")[1].toUpperCase().substring(1)));
							bs.update(true, true);
		}
			}
		}
	}
	
	@EventHandler
	public void onSpawnerBreak(BlockBreakEvent e) {
		if(e.getBlock().getType().equals(Material.MOB_SPAWNER)) {
			int s=0;
		if(starting_lives>0) {
			if(!Core.getInstance().sh.spawners.containsKey(e.getBlock().getLocation())) {Core.getInstance().sh.spawners.put(e.getBlock().getLocation(), starting_lives);}
			s = Core.getInstance().sh.spawners.get(e.getBlock().getLocation());
			e.getBlock().getWorld().createExplosion(e.getBlock().getLocation().getX(), e.getBlock().getLocation().getY(), e.getBlock().getLocation().getZ(), 1F, false, false);
			e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.EXPLOSION_HUGE, 1);
			for(Location l : getCircle(e.getBlock().getLocation(), 7, 55)) {
				l.getWorld().playEffect(l, Effect.FLAME, 1);
			}
			if(s>1) {
			e.setCancelled(true);
			Core.getInstance().sh.spawners.put(e.getBlock().getLocation(), s-1);
			e.getPlayer().sendMessage(livesremaining.replace("%lives%", ""+(s-1)));
			}else {
				Core.getInstance().sh.spawners.remove(e.getBlock().getLocation());
			}
		}
		if(silk&&s-1<1) {
			if(e.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE)&&e.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
				int f = r.nextInt(100);
				if(silkchance<=f) {
					e.setExpToDrop(0);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), Core.getInstance().sh.giveSpawner((CreatureSpawner)e.getBlock().getState()));
					e.getPlayer().sendMessage(silkpickup);
				}else {
					e.getPlayer().sendMessage(silkbroken);
				}
			}
		}
		if(s-1<1) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), new Runnable() {
				public void run() {
					for(ItemStack drop : drops) {
						e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
					}
				}
			}, 10L);
		}
		}
	}

}

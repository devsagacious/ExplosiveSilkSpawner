package com.sagaciousdevelopment.ExplodingSilkSpawners.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sagaciousdevelopment.ExplodingSilkSpawners.Core;
import com.sagaciousdevelopment.ExplodingSilkSpawners.handler.listener.SpawnerListener;

public class SpawnerHandler {
	
	public HashMap<Location, Integer> spawners = new HashMap<Location, Integer>();
	public SpawnerHandler() {
		if(Core.getInstance().getConfig().getInt("starting-lives")>0) {
		File f = new File(Core.getInstance().getDataFolder(), "spawners.yml");
		if(!f.exists()) {
			try {
				PrintWriter pw = new PrintWriter(new FileWriter(f));
				pw.println("spawnerlist: []");
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
		for(String s : conf.getStringList("spawnerlist")) {
			String[] z = s.split(",");
			Location loc = new Location(Bukkit.getWorld(z[0]), Double.parseDouble(z[1]), Double.parseDouble(z[2]), Double.parseDouble(z[3]));
			spawners.put(loc, Integer.parseInt(z[4]));
		}
		}
		new SpawnerListener();
	}
	
	public void save() {
		File q = new File(Core.getInstance().getDataFolder(), "spawners.yml");
		FileConfiguration conf = YamlConfiguration.loadConfiguration(q);
		List<String> f = new ArrayList<String>();
		for(Entry<Location, Integer> z : spawners.entrySet()) {
			Location l = z.getKey();
			String s = l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + z.getValue();
			f.add(s);
		}
		conf.set("spawnerlist", f);
		try {
			conf.save(q);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ItemStack giveSpawner(CreatureSpawner cs) {
		ItemStack is = new ItemStack(Material.MOB_SPAWNER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§eMob Spawner");
		im.setLore(new ArrayList<String>(Arrays.asList("§7Entity Type: " + StringUtils.capitalize(cs.getSpawnedType().name().toLowerCase()))));
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack giveSpawner(EntityType et) {
		ItemStack is = new ItemStack(Material.MOB_SPAWNER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§eMob Spawner");
		im.setLore(new ArrayList<String>(Arrays.asList("§7Entity Type: " + StringUtils.capitalize(et.name().toLowerCase()))));
		is.setItemMeta(im);
		return is;
	}

}

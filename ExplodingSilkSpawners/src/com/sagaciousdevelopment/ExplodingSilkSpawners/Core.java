package com.sagaciousdevelopment.ExplodingSilkSpawners;

import org.bukkit.plugin.java.JavaPlugin;

import com.sagaciousdevelopment.ExplodingSilkSpawners.command.CommandGivespawner;
import com.sagaciousdevelopment.ExplodingSilkSpawners.handler.SpawnerHandler;

/*
 * Credit to the following people:
 * @Mate on discord for the idea
 */

public class Core extends JavaPlugin{
	
	private static Core instance;
	public static Core getInstance() {
		return instance;
	}
	
	public SpawnerHandler sh;
	
	@Override
	public void onEnable() {
		instance=this;
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		sh = new SpawnerHandler();
		new CommandGivespawner();
	}
	
	@Override
	public void onDisable() {
		sh.save();
	}

}

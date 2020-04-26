package com.sagaciousdevelopment.ExplodingSilkSpawners.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sagaciousdevelopment.ExplodingSilkSpawners.Core;


public class CommandGivespawner implements CommandExecutor{
	private String recieved;
	
	public CommandGivespawner() {
		Core.getInstance().getCommand("givespawner").setExecutor(this);
		recieved = ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("spawner-recieved"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("explodingsilkspawners.givespawner")) {
			if(args.length<2) {
				sender.sendMessage("§cInvalid command syntax, use /givespawner <player> <entity> [amount]");
				return true;
			}
			Player p = Bukkit.getPlayerExact(args[0]);
			if(p==null||!p.isOnline()) {
				sender.sendMessage("§cPlayer " + args[0] + " has not been found!");
				return true;
			}
			EntityType e = EntityType.valueOf(args[1].toUpperCase());
			if(e==null) {
				sender.sendMessage("§cEntity " + args[1] + "§c does not exist!");
				return true;
			}
			int f = 1;
			if(args.length==3) {
			   try {
				   f=Integer.parseInt(args[2]);
			   }catch(NumberFormatException ez) {f=-1;}
			   if(f<0||f>64) {
				   sender.sendMessage("§cChoose a value between 1 and 64");
			   }
			}
			ItemStack is = Core.getInstance().sh.giveSpawner(e);
			is.setAmount(f);
			p.getInventory().addItem(is);
			p.sendMessage(recieved);
			sender.sendMessage("§cGave " + p.getName() + " spawners");
			return true;
		}
		return false;
	}

}

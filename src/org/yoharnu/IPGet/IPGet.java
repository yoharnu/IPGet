// Package
package org.yoharnu.IPGet;

// Imports
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * IPGet for Bukkit - Acquire IP of any online player
 * 
 * @author yoharnu
 */

// Starts the class
public class IPGet extends JavaPlugin {
	public IPPermissions permissions;

	@Override
	public void onDisable() {
		System.out.println("IPGet Disabled");
	}

	@Override
	public void onEnable() {

		// Get the information from the yml file.
		PluginDescriptionFile pdfFile = this.getDescription();
		permissions = new IPPermissions(this);

		// Print that the plugin has been enabled!
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		String[] trimmedArgs = args;
		String commandName = cmd.getName().toLowerCase();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (commandName.equalsIgnoreCase("ip")){
				if (permissions.canGetIP(player)){
					if(args.length==1){
						List<Player> Matches = getServer().matchPlayer(trimmedArgs[0]);
						if (Matches.size()>=1){
							Player Match = Matches.get(0);
							if(Match.getName()==player.getName()){
								sender.sendMessage("Your IP address is: " + player.getAddress());
							}
							else{
								sender.sendMessage(ChatColor.YELLOW + "Multiple players by that name.");
								for (int i=0; i<Matches.size(); i++){
									sender.sendMessage(Matches.get(i).getName() + "'s IP address is: " + Matches.get(i).getAddress());
								}
							}
						}
						else{
							sender.sendMessage(ChatColor.YELLOW + "That player is not online.");
						}
					}
					else if(args.length==0){
						sender.sendMessage("Your IP address is: " + player.getAddress());
					}
					else{
						sender.sendMessage(ChatColor.YELLOW + "Usage: /ip <player>");
					}
				}
				else{
					sender.sendMessage("You do not have permission to do that.");
				}
				return true;
			}
		} 
		else {
			sender.sendMessage("You cannot execute that command from the console.");
		}
		return false;
	}
}

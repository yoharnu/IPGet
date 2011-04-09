// Package
package org.yoharnu.IPGet;

// Imports
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yoharnu.IPGet.IPPlayerListener;

/**
 * IPGet for Bukkit - Acquire IP of any online player
 * 
 * @author yoharnu
 */

// Starts the class
public class IPGet extends JavaPlugin {
	private final IPPlayerListener playerListener = new IPPlayerListener(this);
	public IPPermissions permissions;
	public static final Logger logger = Logger.getLogger(IPGet.class.getName());
	
	@Override
	public void onDisable() {
		System.out.println("IPGet Disabled");
	}

	@Override
	public void onEnable() {

		// Get the information from the yml file.
		PluginDescriptionFile pdfFile = this.getDescription();
		permissions = new IPPermissions(this);
		
		// Create the pluginmanager
		PluginManager pm = getServer().getPluginManager();

		// Create listeners
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);

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
				if (permissions.canGetSelf(player) || permissions.canGetOther(player)){
					if(args.length==1){
						List<Player> Matches = getServer().matchPlayer(trimmedArgs[0]);
						if (Matches.size()>0){
							Player Match = Matches.get(0);
							if(Match.getName()==player.getName() && permissions.canGetSelf(player)){
								sender.sendMessage("Your IP address is: " + player.getAddress());
							}
							else if(permissions.canGetOther(player)){
								if(Matches.size()>1){
									sender.sendMessage(ChatColor.YELLOW + "Multiple players by that name.");
								}
								for (int i=0; i<Matches.size(); i++){
									sender.sendMessage(Matches.get(i).getName() + "'s IP address is: " + Matches.get(i).getAddress());
								}
							}
							else{
								sender.sendMessage("You don't have Permission to do that");
							}
						}
						else{
							sender.sendMessage(ChatColor.YELLOW + "That player is not online.");
						}
					}
					else if(args.length==0 && permissions.canGetSelf(player)){
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

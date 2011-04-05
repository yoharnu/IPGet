// Package
package org.yoharnu.IPGet;

// Imports
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import org.yoharnu.IPGet.IPGetPlayerListener;


/**
 * IPGet for Bukkit - Acquire IP of any online player
 * 
 * @author yoharnu
 */

// Starts the class
public class IPGet extends JavaPlugin {

	// Links the IPGetPlayerListener
	private final IPGetPlayerListener playerListener = new IPGetPlayerListener(this);

	@Override
	public void onDisable() {
		System.out.println("IPGet Disabled");
	}

	@Override
	public void onEnable() {

		// Create the pluginmanager
		PluginManager pm = getServer().getPluginManager();

		// Create listeners
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,
				Event.Priority.Normal, this);

		// Get the information from the yml file.
		PluginDescriptionFile pdfFile = this.getDescription();

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
				if (player.isOp()){
					if(args.length==1){
						List<Player> Matches = getServer().matchPlayer(trimmedArgs[0]);
						if (Matches.size()>=1){
							Player Match = Matches.get(0);
							if(Match.getName()==player.getName()){
								sender.sendMessage("Your IP address is: " + player.getAddress());
							}
							else{
								for (int i=0; i<Matches.size(); i++){
									sender.sendMessage("Multiple players by that name.");
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
			else {
				player.sendMessage(ChatColor.YELLOW
						+ "The command you are trying is not enabled by the admin.");
				return true;
			}
		} else {
			sender.sendMessage("Cannot execute that command, I don't know who you are!");
		}
		return false;
	}
}

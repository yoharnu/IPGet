// Package
package org.yoharnu.IPGet;

// Imports
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import org.bukkit.util.config.Configuration;

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

	// register File and config
	private File myFile = new File("plugins/IPGet/IPGet.yml");
	private File myDirectory = new File ("plugins/IPGet");
	public Configuration config;

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

		// Check for existence of the plugin folder
		
		if (!myDirectory.exists()) {
			myDirectory.mkdir();
		}
		
		// Initialize config
		if (myFile.exists()) {
			config = new Configuration(myFile);
			config.load();
		} else {
			// Create config file in folder of server
			System.out
					.println("There is no config file for IPGet - creating 'plugins/IPGet/IPGet.yml' ...");

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						myFile.getCanonicalFile()));

				// Write standard config
				writer.write("ip_enabled: true");
				writer.newLine();
				writer.write("# Do not change please");
				writer.newLine();
				writer.write("config_version: 1");
				writer.close();

			} catch (IOException e) {
				System.out.println(e.getLocalizedMessage());
			}

			config = new Configuration(myFile);
			config.load();

		}
		
		if (! config.getProperty("config_version").equals(1)) {
			// Check if config is up to date
			
			System.out.println("IPConfig: IMPORTANT!");
			System.out.println("IPConfig: Wrong version of config file detected.");
			System.out.println("IPConfig: Delete your current config file and let the plugin rebuild it automatically on the next start!");
			System.out.println("IPConfig: Otherwise the plugin will start to behave strangely");
	
		}

	}

	// Function to handle our commands and the config
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		String[] trimmedArgs = args;
		String commandName = cmd.getName().toLowerCase();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (commandName.equalsIgnoreCase("ip")
					&& config.getString("ip_enabled").equals("true")) {
				if (player.isOp()){
					if(args.length>0){
						List<Player> Matches = getServer().matchPlayer(trimmedArgs[0]);
						if (Matches.size()==1){
							Player Match = Matches.get(0);
							if(Match.getName()==player.getName()){
								sender.sendMessage("Your IP address is: " + player.getAddress());
							}
							else{
								sender.sendMessage(Match.getName() + "'s IP address is: " + Match.getAddress());
							}
						}
						else if (Matches.size()>1){
							sender.sendMessage(ChatColor.YELLOW + "There is more than one player by that name. Cannot return IP.");
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
/*
	// Used when debugging
	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
*/
}

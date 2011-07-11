// Package
package org.yoharnu.IPGet;

// Imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * IPGet for Bukkit - Acquire IP of any online player
 * 
 * @author yoharnu
 */
// Starts the class
public class IPGet extends JavaPlugin {

    private final IPPlayerListener playerListener = new IPPlayerListener(this);
    private static final Logger logger = Logger.getLogger(IPGet.class.getName());
    private IPPermissions permissions;
    private FileHandler filehandler;
    private IPGetConfig config;
    private static IPGet plugin;

    @Override
    public void onDisable() {
        writelog("IPGet Disabled!", false);
    }

    @Override
    public void onEnable() {

        // Get the information from the yml file.
        PluginDescriptionFile pdfFile = this.getDescription();

        // Create the pluginmanager
        PluginManager pm = getServer().getPluginManager();

        // Register listeners
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);

        // Start the permissions Handlers
        permissions = new IPPermissions(this);

        // Starts the file handler
        try {
            filehandler = new FileHandler(this.getDataFolder());
        } catch (IOException ex) {
            writelog("Cannot open filehandler! " + ex.getMessage(), true);
        }
        
        // Starts the configuration handler
        try {
            config = new IPGetConfig(this.getDataFolder());
        } catch (IOException ex) {
            writelog("Cannot open config! " + ex.getMessage(), true);
        }

        // Set plugin reference
        plugin = this;

        // Print that the plugin has been enabled!
        writelog(pdfFile.getName() + " version "
                + pdfFile.getVersion() + " is enabled!", false);
    }

    /**
     * Returns the plugin
     * @return 
     */
    public static IPGet getPlugin() {
        return plugin;
    }

    /**
     * Returns the filehandler
     * @return 
     */
    public FileHandler getFilehandler() {
        return filehandler;
    }

    /**
     * Returns the config
     * @return 
     */
    public IPGetConfig getConfig() {
        return config;
    }

    /**
     * Writes a log message
     * @param message
     * @param error 
     */
    static void writelog(String message, boolean error) {
        if (error) {
            logger.severe(new StringBuilder("[").append("IPGet").append("] - ").append(message).toString());
        } else {
            logger.info(new StringBuilder("[").append("IPGet").append("] - ").append(message).toString());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        String commandName = cmd.getName().toLowerCase();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (commandName.equalsIgnoreCase("ip")) {

                // Check array length if there is an argument
                if (args.length == 0) {
                    // Check permissions
                    if (permissions.hasPermission(player, getConfig().getSelfnode())) {
                        // Print a formatted ip address
                        sender.sendMessage("Your IP address is: " + FileHandler.formatIP(player.getAddress().toString()));
                    } else {
                        sender.sendMessage("You don't have Permission to do that");
                    }
                } else if (args.length == 1) {
                    // Get target player
                    Player targetPlayer = getServer().getPlayer(args[0]);
                    // See if player exists
                    if (targetPlayer == null) {
                        player.sendMessage(ChatColor.YELLOW + "No such player.");
                        return true;
                    }
                    // Check for permissions
                    if (permissions.hasPermission(player, getConfig().getOthernode())) {
                        // Prints out ip
                        sender.sendMessage(targetPlayer.getName() + " IP address is: " + FileHandler.formatIP(targetPlayer.getAddress().toString()));
                    } else {
                        sender.sendMessage("You don't have Permission to do that");
                    }
                } else {
                    //Invalid amount of arguments, print out usage message
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /ip [player]");
                }
                return true;
            } else if (commandName.equalsIgnoreCase("iplist")) {

                // Check arguments length
                if (args.length == 1) {
                    // Get player
                    Player targetPlayer = getServer().getPlayer(args[0]);
                    // See if player exits
                    if (targetPlayer == null) {
                        // Player doesn't exist

                        // Check for permissions
                        if (!permissions.hasPermission(player, getConfig().getOthernode())) {
                            sender.sendMessage("You don't have Permission to do that");
                            return true;
                        }

                        //Print message
                        player.sendMessage("*** Player not online ***");

                        // Get UserIplist with the users, where ignoring the case
                        ArrayList<IIP> iplist = getFilehandler().getUserIplist(args[0], true);
                        // See if got results
                        if (iplist != null && iplist.size() > 0) {
                            // Got results, print
                            player.sendMessage("Listing ip's which " + args[0] + " to login:");
                            for (IIP iip : iplist) {
                                player.sendMessage(ChatColor.YELLOW + iip.getIp() + " on '" + iip.getDateString() + "'");
                            }
                        } else {
                            // No result
                            player.sendMessage(ChatColor.YELLOW + "Player not found!");
                        }
                        return true;
                    }

                    //Player is online, check for permissions
                    if ((targetPlayer.getName().equalsIgnoreCase(player.getName()) && !permissions.hasPermission(player, getConfig().getSelfnode())) || !permissions.hasPermission(player, getConfig().getOthernode())) {
                        sender.sendMessage("You don't have Permission to do that");
                        return true;
                    }

                    // Get list
                    ArrayList<IIP> iplist = getFilehandler().getUserIplist(targetPlayer.getName(), false);

                    // Print results, (dont need extra null check, as the player is logged in)
                    player.sendMessage("Listing ip's which " + targetPlayer.getName() + " to login:");
                    for (IIP iip : iplist) {
                        player.sendMessage(ChatColor.YELLOW + iip.getIp() + " on '" + iip.getDateString() + "'");
                    }
                } else {
                    //Invalid amount of arguments, print out usage message
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /iplist [player]");
                }

                return true;

            } else if (commandName.equalsIgnoreCase("ipusers")) {

                // Check arguments length
                if (args.length == 1) {

                    // Check if argument contains an IP
                    if (args[0].contains(".")) {
                        // Check permissions
                        if (!permissions.hasPermission(player, getConfig().getOthernode())) {
                            sender.sendMessage("You don't have Permission to do that");
                            return true;
                        }

                        // Get userlist
                        ArrayList<String> userlist = getFilehandler().getIpUserList(args[0]);
                        // Check if any results
                        if (userlist != null && userlist.size() > 0) {
                            //Results found, printing
                            player.sendMessage("Listing users with the ip '" + FileHandler.formatIP(args[0]) + "':");
                            for (String name : userlist) {
                                player.sendMessage(ChatColor.YELLOW + name);
                            }
                        } else {
                            // No results, printing message
                            player.sendMessage(ChatColor.YELLOW + "IP not found!");
                        }
                        return true;
                    }

                    // Get target player
                    Player targetPlayer = getServer().getPlayer(args[0]);
                    // See if player exists
                    if (targetPlayer == null) {
                        // Check permissions
                        if (!permissions.hasPermission(player, getConfig().getOthernode())) {
                            sender.sendMessage("You don't have Permission to do that");
                            return true;
                        }

                        // Print out message
                        player.sendMessage("*** Player not online ***");

                        // Get list with ip's , where ignoring the case
                        ArrayList<IIP> iplist = getFilehandler().getUserIplist(args[0], true);
                        // Check if list is not empty
                        if (iplist != null && iplist.size() > 0) {
                            // Loop trough all ip's
                            for (IIP ipp : iplist) {
                                // gets a list of users with the given IP
                                ArrayList<String> userlist = getFilehandler().getIpUserList(ipp.getIp());
                                // There is atleast one, so no need for null check, print results
                                player.sendMessage("Listing users with the ip '" + FileHandler.formatIP(targetPlayer.getAddress().toString()) + "':");
                                for (String name : userlist) {
                                    player.sendMessage(ChatColor.YELLOW + name);
                                }
                            }
                        } else {
                            // Player not found
                            player.sendMessage(ChatColor.YELLOW + "Player not found!");
                        }
                        return true;
                    }

                    // Check permissions
                    if ((targetPlayer.getName().equalsIgnoreCase(player.getName()) && !permissions.hasPermission(player, getConfig().getSelfnode())) || !permissions.hasPermission(player, getConfig().getOthernode())) {
                        sender.sendMessage("You don't have Permission to do that");
                        return true;
                    }

                    // Get list with users on the target's ip
                    ArrayList<String> userlist = getFilehandler().getIpUserList(targetPlayer.getAddress().toString());
                    // Will always have at least one (target itself) so no need for null check, Print results
                    player.sendMessage("Listing users with the ip '" + FileHandler.formatIP(targetPlayer.getAddress().toString()) + "':");
                    for (String name : userlist) {
                        player.sendMessage(ChatColor.YELLOW + name);
                    }
                } else {
                    // Invalid amount of arguments, print out usage message
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /ipusers [player]");
                }
                return true;
            }
        } else {
            // Block server commands
            sender.sendMessage("You cannot execute that command from the console.");
        }
        return false;
    }

    /**
     * Prints message to server and all allowed players
     * @param message 
     */
    public void sendWarningMessage(String message) {
        // Check if message is not server log only
        if (!getConfig().getWarningnode().equalsIgnoreCase("server")) {
            // Get all connected players
            for (Player player : getServer().getOnlinePlayers()) {
                // Check if they have permissions to get the warning
                if (permissions.hasPermission(player, getConfig().getWarningnode())) {
                    // Send warning
                    player.sendMessage(message);
                }
            }
        }
        // Send warning to server log/console
        writelog(message, false);
    }
}

// Package
package org.yoharnu.IPGet;

// Imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
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
    public IPPermissions permissions;
    public static final Logger logger = Logger.getLogger(IPGet.class.getName());
    private FileHandler filehandler;
    private static IPGet plugin;

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
        try {
            filehandler = new FileHandler(this.getDataFolder());
        } catch (IOException ex) {
            Logger.getLogger(IPGet.class.getName()).log(Level.SEVERE, null, ex);
        }
        plugin = this;
    }

    public static IPGet getPlugin() {
        return plugin;
    }

    public FileHandler getFilehandler() {
        return filehandler;
    }

//    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
//        String[] trimmedArgs = args;

        String commandName = cmd.getName().toLowerCase();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (commandName.equalsIgnoreCase("ip")) {

                if (args.length == 0) {
                    if (permissions.canGetSelf(player)) {
                        sender.sendMessage("Your IP address is: " + FileHandler.formatIP(player.getAddress().toString()));
                    } else {
                        sender.sendMessage("You don't have Permission to do that");
                    }
                } else if (args.length == 1) {
                    Player targetPlayer = getServer().getPlayer(args[0]);
                    if (targetPlayer == null) {
                        player.sendMessage(ChatColor.YELLOW + "No such player.");
                        return true;
                    }
                    if (permissions.canGetOther(player)) {
                        sender.sendMessage(targetPlayer.getName() + " IP address is: " + FileHandler.formatIP(targetPlayer.getAddress().toString()));
                    } else {
                        sender.sendMessage("You don't have Permission to do that");
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /ip [player]");
                }

                return true;
            } else if (commandName.equalsIgnoreCase("iplist")) {

                if (args.length == 1) {
                    Player targetPlayer = getServer().getPlayer(args[0]);
                    if (targetPlayer == null) {
                        player.sendMessage("*** Player not online ***");

                        ArrayList<IIP> iplist = getFilehandler().getUserIplist(args[0], true);
                        if (iplist != null && iplist.size() > 0) {
                            player.sendMessage("Listing ip's which " + args[0] + " to login:");
                            for (IIP iip : iplist) {
                                player.sendMessage(ChatColor.YELLOW + iip.getIp() + " on '" + iip.getDateString() + "'");
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Player not found!");
                        }
                        return true;
                    }

                    if (targetPlayer.getName().equalsIgnoreCase(player.getName()) && !permissions.canGetSelf(player)) {
                        sender.sendMessage("You don't have Permission to do that");
                        return true;
                    } else if (!permissions.canGetOther(player)) {
                        sender.sendMessage("You don't have Permission to do that");
                        return true;
                    }

                    ArrayList<IIP> iplist = getFilehandler().getUserIplist(targetPlayer.getName(), false);
                    player.sendMessage("Listing ip's which " + targetPlayer.getName() + " to login:");
                    for (IIP iip : iplist) {
                        player.sendMessage(ChatColor.YELLOW + iip.getIp() + " on '" + iip.getDateString() + "'");
                    }

                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /iplist [player]");
                }

                return true;

            } else if (commandName.equalsIgnoreCase("ipusers")) {

                if (args.length == 1) {
                    Player targetPlayer = getServer().getPlayer(args[0]);
                    if (targetPlayer == null) {
                        player.sendMessage(ChatColor.YELLOW + "Player not found!");
                        return true;
                    }

                    if (targetPlayer.getName().equalsIgnoreCase(player.getName()) && !permissions.canGetSelf(player)) {
                        sender.sendMessage("You don't have Permission to do that");
                        return false;

                    } else if (!permissions.canGetOther(player)) {
                        sender.sendMessage("You don't have Permission to do that");
                        return false;
                    }

                    ArrayList<String> userlist = getFilehandler().getIpUserList(targetPlayer.getAddress().toString());
                    player.sendMessage("Listing users with the ip '" + FileHandler.formatIP(targetPlayer.getAddress().toString()) + "':");
                    for (String name : userlist) {
                        player.sendMessage(ChatColor.YELLOW + name);
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /ipusers [player]");
                }
                return true;

//                if (permissions.canGetSelf(player) || permissions.canGetOther(player)) {
//                    if (args.length == 1) {
//                        List<Player> Matches = getServer().matchPlayer(trimmedArgs[0]);
//                        if (Matches.size() > 0) {
//                            Player Match = Matches.get(0);
//                            if (Match.getName() == player.getName() && permissions.canGetSelf(player)) {
//                                sender.sendMessage("Your IP address is: " + player.getAddress());
//                            } else if (permissions.canGetOther(player)) {
//                                if (Matches.size() > 1) {
//                                    sender.sendMessage(ChatColor.YELLOW + "Multiple players by that name.");
//                                }
//                                for (int i = 0; i < Matches.size(); i++) {
//                                    sender.sendMessage(Matches.get(i).getName() + "'s IP address is: " + Matches.get(i).getAddress());
//                                }
//                            } else {
//                                sender.sendMessage("You don't have Permission to do that");
//                            }
//                        } else {
//                            //sender.sendMessage(ChatColor.YELLOW + "That player is not online.");
//                            File configFile = new File(this.getDataFolder(), "logs/" + trimmedArgs[0] + ".log");
//                            if (!configFile.exists()) {
//                                sender.sendMessage(ChatColor.YELLOW + "No such player.");
//                            } else {
//                                FileInputStream fstream = null;
//                                try {
//                                    fstream = new FileInputStream(configFile);
//                                } catch (FileNotFoundException e1) {
//                                }
//                                DataInputStream in = new DataInputStream(fstream);
//                                String lastIP = "";
//                                String newline = System.getProperty("line.separator");
//                                try {
//                                    while (in.available() != 0) {
//                                        lastIP = in.readLine() + newline;
//                                    }
//                                } catch (IOException e1) {
//                                }
//                                sender.sendMessage(ChatColor.YELLOW + "That player is not online.");
//                                sender.sendMessage(ChatColor.YELLOW + "Last known IP:");
//                                sender.sendMessage(lastIP);
//                            }
//                        }
//                    } else if (args.length == 0 && permissions.canGetSelf(player)) {
//                        sender.sendMessage("Your IP address is: " + player.getAddress());
//                    } else {
//                        sender.sendMessage(ChatColor.YELLOW + "Usage: /ip <player>");
//                    }
//                } else {
//                    sender.sendMessage("You do not have permission to do that.");
//                }
            }
        } else {
            sender.sendMessage("You cannot execute that command from the console.");
        }
        return false;
    }
}

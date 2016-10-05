package me.hretsam.ipnotify.commands;

import java.util.ArrayList;
import me.hretsam.ipnotify.FileHandler;
import me.hretsam.ipnotify.IPObject;
import me.hretsam.ipnotify.IPNotify;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Hretsam
 */
public class CommandIpUsers implements IPCommand {

    @Override
    public void run(IPNotify parent, CommandSender sender, String command, String[] args) {
        // Checks if the sender is a player, so it will filter out colours for the server console
        boolean useColour = (sender instanceof Player);

        // Check arguments length
        if (args.length == 1) {

            String ip = null;

            // Check if argument contains an IP
            if (args[0].contains(".")) {
                // Check permissions
                if (!parent.getPermissions().hasPermission(sender, parent.getConfig().getOthernode())) {
                    sender.sendMessage("You don't have Permission to do that");
                    return;
                }
                // Print out message
                sender.sendMessage("*** Raw IP address found ***");

                // Set ip variable
                ip = args[0];
            }
            if (ip == null) {
                // Get target player
                Player targetPlayer = parent.getServer().getPlayer(args[0]);
                // See if player exists
                if (targetPlayer == null) {
                    // Check permissions
                    if (!parent.getPermissions().hasPermission(sender, parent.getConfig().getOthernode())) {
                        sender.sendMessage("You don't have Permission to do that");
                        return;
                    }
                    // This one needs to be seperate! Due to the list op IPs of the user

                    // Print out message
                    sender.sendMessage("*** Player not online ***");

                    // Get list with ip's , where ignoring the case
                    ArrayList<IPObject> iplist = parent.getFilehandler().getUserIplist(parent.getFilehandler().checkCaseIndependant(args[0]), parent.getConfig().getMaxIpListSize());
                    // Check if list is not empty
                    if (iplist != null && iplist.size() > 0) {
                        // Loop trough all ip's
                        for (IPObject ipp : iplist) {
                            // gets a list of users with the given IP
                            ArrayList<String> userlist = parent.getFilehandler().getIpUserList(ipp.getIp());
                            // There is atleast one, so no need for null check, print results
                            sender.sendMessage("Listing users with the ip '" + ipp.getIp() + "':");
                            // String builder 
                            StringBuilder sb = new StringBuilder();
                            // Loop trough all names in the list
                            for (String name : userlist) {

                                // Checks if there should be a , added
                                if (sb.length() > 0) {
                                    sb.append(", ");
                                }
                                // Checks the length of the string, to make sure no data is lost
                                if (sb.length() + 2 + name.length() > 280) {
                                    // List to long, print results and reset list
                                    sender.sendMessage(sb.toString());
                                    sb = new StringBuilder().append((useColour ? ChatColor.YELLOW : "")).append(name);
                                } else {
                                    // String length is ok, append name
                                    sb.append(name);
                                }

                            }
                            // Print the rest of the string
                            sender.sendMessage(sb.toString());
                        }
                    } else {
                        // Player not found
                        sender.sendMessage((useColour ? ChatColor.YELLOW : "") + "Player not found!");
                    }
                    // Makes sure this command stops here
                    return;
                } else {

                    // If the sender is a player, check if the name target is himself
                    if (sender instanceof Player
                            && (targetPlayer.getName().equalsIgnoreCase(((Player) sender).getName())
                            && !parent.getPermissions().hasPermission(sender, parent.getConfig().getSelfnode()))) {
                        sender.sendMessage("You don't have Permission to do that");
                        return;
                    } else {
                        // Check if got permission (checks server to for future server permission adjustment)
                        if (!parent.getPermissions().hasPermission(sender, parent.getConfig().getOthernode())) {
                            sender.sendMessage("You don't have Permission to do that");
                            return;
                        }
                    }

                    // Set the variable
                    ip = FileHandler.formatIP(targetPlayer.getAddress().toString());
                }
            }

            // Get list with users on the target's ip
            ArrayList<String> userlist = parent.getFilehandler().getIpUserList(ip);
            // Will always have at least one (target itself) so no need for null check, Print results
            sender.sendMessage("Listing users with the ip '" + ip + "':");

            // String builder 
            StringBuilder sb = new StringBuilder();
            // Loop trough all names in the list
            for (String name : userlist) {

                // Checks if there should be a , added
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                // Checks the length of the string, to make sure no data is lost
                if (sb.length() + 2 + name.length() > 280) {
                    // List to long, print results and reset list
                    sender.sendMessage(sb.toString());
                    sb = new StringBuilder().append((useColour ? ChatColor.YELLOW : "")).append(name);
                } else {
                    // String length is ok, append name
                    sb.append(name);
                }

            }
            
            // Make it so it says "No players found" when no players found
            if (sb.length() == 0) {
                sb.append((useColour ? ChatColor.YELLOW : "")).append("No players found!");
            }

            // Print the rest of the string
            sender.sendMessage(sb.toString());
            return;
        } else {
            // Invalid amount of arguments, print out usage message
            sender.sendMessage((useColour ? ChatColor.YELLOW : "") + "Usage: /ipusers [player]");
        }
    }
}

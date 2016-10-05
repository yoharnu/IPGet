package me.hretsam.ipnotify.commands;

import java.util.ArrayList;
import me.hretsam.ipnotify.IPObject;
import me.hretsam.ipnotify.IPNotify;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Hretsam
 */
public class CommandIpList implements IPCommand {

    @Override
    public void run(IPNotify parent, CommandSender sender, String command, String[] args) {
        // Checks if the sender is a player, so it will filter out colours for the server console
        boolean useColour = (sender instanceof Player);

        // Check arguments length
        if (args.length == 1) {
            // Get player
            Player targetPlayer = parent.getServer().getPlayer(args[0]);
            // See if player exits
            if (targetPlayer == null) {
                // Player doesn't exist

                // Check for permissions
                if (!parent.getPermissions().hasPermission(sender, parent.getConfig().getOthernode())) {
                    sender.sendMessage("You don't have Permission to do that");
                    return;
                }

                //Print message
                sender.sendMessage("*** Player not online ***");

                // Get UserIplist with the users, where ignoring the case
                ArrayList<IPObject> iplist = parent.getFilehandler().getUserIplist(parent.getFilehandler().checkCaseIndependant(args[0]), parent.getConfig().getMaxIpListSize());
                // See if got results
                if (iplist != null && iplist.size() > 0) {
                    // Got results, print
                    sender.sendMessage("Listing ip's which " + args[0] + " to login:");
                    for (IPObject iip : iplist) {
                        sender.sendMessage((useColour ? ChatColor.YELLOW : "") + iip.getIp() + " on '" + iip.getDateString() + "'");
                    }
                } else {
                    // No result
                    sender.sendMessage((useColour ? ChatColor.YELLOW : "") + "Player not found!");
                }
                return;
            }

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

            // Get list
            ArrayList<IPObject> iplist = parent.getFilehandler().getUserIplist(targetPlayer.getName(), parent.getConfig().getMaxIpListSize());

            // Print results, (dont need extra null check, as the player is logged in)
            sender.sendMessage("Listing ip's which " + targetPlayer.getName() + " to login:");
            for (IPObject iip : iplist) {
                sender.sendMessage((useColour ? ChatColor.YELLOW : "") + iip.getIp() + " on '" + iip.getDateString() + "'");
            }
        } else {
            //Invalid amount of arguments, print out usage message
            sender.sendMessage((useColour ? ChatColor.YELLOW : "") + "Usage: /iplist [player]");
        }
        return;
    }
}

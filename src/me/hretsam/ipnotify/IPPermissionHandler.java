package me.hretsam.ipnotify;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.command.CommandSender;

public class IPPermissionHandler {

    private PermissionHandler permissions = null;

    public IPPermissionHandler(IPNotify plugin) {
        Plugin theYetiPermissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (theYetiPermissions != null) {
            permissions = ((com.nijikokun.bukkit.Permissions.Permissions) theYetiPermissions).getHandler();
        }
    }

    /**
     * Checks if the sender has access to the node
     * 
     * Server always got access!
     * @param 
     * @param key
     * @return 
     */
    public boolean hasPermission(CommandSender sender, String key) {
        // Check if both are not null
        if (key == null && sender == null) {
            return false;
        }
        // Check if sender is a playyer
        if (sender instanceof Player) {
            // check key
            if (key.equalsIgnoreCase("none")) {
                return false;
            }
            // check key
            if (key.equalsIgnoreCase("all")) {
                return true;
            }
            // check key
            if (key.equalsIgnoreCase("op")) {
                return ((Player) sender).isOp();
            }
            // check permissions
            if (permissions != null) {
                // check permissions key
                return permissions.has(((Player) sender), key);
            }
            // No key found, return
            return false;
        }
        // Sender is server, return true!
        return true;
    }
}

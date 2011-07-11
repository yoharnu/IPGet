package org.yoharnu.IPGet;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

public class IPPermissions {

    private PermissionHandler permissions;

    public IPPermissions(IPGet plugin) {
        Plugin theYetiPermissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (theYetiPermissions != null) {
            permissions = ((com.nijikokun.bukkit.Permissions.Permissions) theYetiPermissions).getHandler();
        }
    }
    
    /**
     * Checks if the player has access to the node
     * @param player
     * @param key
     * @return 
     */
    public boolean hasPermission(Player player, String key) {
        if (key == null && player == null) {
            return false;
        }
        if (key.equalsIgnoreCase("none")) {
            return false;
        }
        if (key.equalsIgnoreCase("all")) {
            return true;
        }
        if (key.equalsIgnoreCase("op")) {
            return player.isOp();
        }
        if (permissions != null) {
            return permissions.has(player, key);
        }
        return false;
    }
}

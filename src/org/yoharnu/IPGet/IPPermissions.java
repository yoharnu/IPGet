package org.yoharnu.IPGet;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.*;

public class IPPermissions extends Permissions {
	private static PermissionHandler permissions;

	public IPPermissions(IPGet plugin) {
		Plugin theYetiPermissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
		if (theYetiPermissions != null) {
			permissions = ((com.nijikokun.bukkit.Permissions.Permissions) theYetiPermissions).getHandler();
		}
	}
	public boolean canGetSelf(Player player){
		if (permissions != null){
            return permissions.has(player, "IPGet.self");
		}
        else{
            return player.isOp();
        }
	}
	public boolean canGetOther(Player player){
		if (permissions != null){
            return permissions.has(player, "IPGet.other");
		}
        else{
            return player.isOp();
        }
	}
}

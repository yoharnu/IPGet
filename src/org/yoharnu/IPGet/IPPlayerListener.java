package org.yoharnu.IPGet;

import java.util.Date;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class IPPlayerListener extends PlayerListener {

    public static IPGet plugin;

    public IPPlayerListener(IPGet instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Get player
        Player player = event.getPlayer();

        // Check if should do warning, and if player is new
        if (plugin.getConfig().doWarnOnFirstJoin() && !plugin.getFilehandler().isUserAlreadyLogged(player.getName())) {
            // Get all connected players to the ip of the new user
            List<String> users = plugin.getFilehandler().getIpUserList(player.getAddress().toString());
            // If none (player itself not added yet) no warning
            if (users != null && users.size() > 0) {
                plugin.sendWarningMessage(ChatColor.RED + "[IPGet] The IP of player " + player.getName() + " is used by " + users.size() + " other user(s)!");
            }
        }

        // Add player to log file
        plugin.getFilehandler().addIp(player.getName(), player.getAddress().toString(), new Date().getTime());
    }
}

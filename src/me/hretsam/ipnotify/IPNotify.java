// Package
package me.hretsam.ipnotify;

// Imports
import java.io.IOException;
import java.util.logging.Logger;
import me.hretsam.ipnotify.commands.CommandAIpBan;
import me.hretsam.ipnotify.commands.CommandIP;
import me.hretsam.ipnotify.commands.CommandIpCheck;
import me.hretsam.ipnotify.commands.CommandIpList;
import me.hretsam.ipnotify.commands.CommandIpUsers;
import me.hretsam.ipnotify.commands.IPCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * IPNotify for Bukkit - Advanced IP logger
 * 
 * @author yoharnu
 */
// Starts the class
public class IPNotify extends JavaPlugin {

    private final IPPlayerListener playerListener = new IPPlayerListener(this);
    private static final Logger logger = Logger.getLogger(IPNotify.class.getName());
    private IPPermissionHandler permissions;
    private FileHandler filehandler;
    private IPConfig config;
    private static IPNotify plugin;

    @Override
    public void onDisable() {
        writelog("IPNotify Disabled!", false);
    }

    @Override
    public void onEnable() {

        // Get the information from the yml file.
        PluginDescriptionFile pdfFile = this.getDescription();

        // Create the pluginmanager
        PluginManager pm = getServer().getPluginManager();

        // Register listeners
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);

        // Start the permissions Handlers
        permissions = new IPPermissionHandler(this);

        // Starts the file handler
        try {
            filehandler = new FileHandler(this.getDataFolder());
        } catch (IOException ex) {
            writelog("Cannot open filehandler! " + ex.getMessage(), true);
        }

        // Starts the configuration handler
        try {
            config = new IPConfig(this.getDataFolder());
        } catch (IOException ex) {
            writelog("Cannot open config! " + ex.getMessage(), true);
        }

        // Set plugin reference
        plugin = this;

        // Check if there are still old logs
        if (Converter.hasOldFiles(this.getDataFolder())) {
            // Convert old logs
            Converter.convert(getDataFolder());
        }

        // Print that the plugin has been enabled!
        writelog(pdfFile.getName() + " version "
                + pdfFile.getVersion() + " is enabled!", false);
    }

    /**
     * Returns the plugin
     * @return 
     */
    public static IPNotify getPlugin() {
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
    public IPConfig getConfig() {
        return config;
    }

    /**
     * Returns permissions
     */
    public IPPermissionHandler getPermissions() {
        return permissions;
    }

    /**
     * Writes a log message
     * @param message
     * @param error 
     */
    static void writelog(String message, boolean error) {
        if (error) {
            logger.severe(new StringBuilder("[").append("IPNotify").append("] - ").append(message).toString());
        } else {
            logger.info(new StringBuilder("[").append("IPNotify").append("] - ").append(message).toString());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // Get command
        // Make a IPCommand object
        IPCommand command = null;

        // Check for commands
        if (commandLabel.equalsIgnoreCase("ip")) {
            command = new CommandIP();
        } else if (commandLabel.equalsIgnoreCase("ipusers")) {
            command = new CommandIpUsers();
        } else if (commandLabel.equalsIgnoreCase("iplist")) {
            command = new CommandIpList();
        } else if (commandLabel.equalsIgnoreCase("ipcheck")) {
            command = new CommandIpCheck();
        } else if (commandLabel.equalsIgnoreCase("aipban")) {
            command = new CommandAIpBan();
        }

        // Check if the command is found
        if (command != null) {
            // If found run it
            command.run(plugin, sender, commandLabel, args);
            // Return true;
            return true;
        }
        // none found return false;
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

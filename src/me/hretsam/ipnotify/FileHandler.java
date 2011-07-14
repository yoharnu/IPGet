package me.hretsam.ipnotify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author Hretsam
 */
public class FileHandler {

    /** Filename of the playerlog */
    private static final String filename = "players.yml";
    /** Loaded userfilelog */
    private Configuration userlog;
    private static IIPComparator comparator;

    public FileHandler(File datafolder) throws IOException {
        // Checks if directory already exists
        if (!datafolder.exists()) {
            // makes directory
            datafolder.mkdirs();
        }
        // Loads the location of the file
        File userLogFile = new File(datafolder.getAbsolutePath() + File.separator + filename);
        // check if exists
        if (!userLogFile.exists()) {
            // If not exists create new file
            userLogFile.createNewFile();
        }
        // Load file into configuration (yml reader)
        userlog = new Configuration(userLogFile);
        // Loads the file 
        userlog.load();

        //inits the comparator
        comparator = new IIPComparator();
    }

    /**
     * Adds the ip to the user's IP list
     * @param username
     * @param ip
     * @param datelong 
     */
    public void addIp(String username, String ip, long datelong) {
        ip = formatIP(ip);

        // Makes sure it wont mistake the ip for nodes in the yml file
        ip = ip.replaceAll("\\.", "_");

        // Gets the old date value
        long oldDateLong = Long.valueOf(userlog.getString("users." + username + ".ip." + ip, "0"));

        // Check if new date is actually more new then the old value
        if (datelong > oldDateLong) {
            // Sets the ip (use the ip node for future adding of other nodes
            userlog.setProperty("users." + username + ".ip." + ip, datelong);
            // Saves file
            userlog.save();
        }

    }

    /**
     * Returns a list with all of the ip logged to this user
     * @param username
     * @param forceCaseCheck (use this when your not sure the casing is right)
     * @return 
     */
    public ArrayList<IPObject> getUserIplist(String username, int maxSize) {
        // Get all ip's
        List<String> keys = userlog.getKeys("users." + username + ".ip");
        // Check if there are ip's
        if (keys == null) {
            return null;
        }
        // Create the return list
        ArrayList<IPObject> iplist = new ArrayList<IPObject>(keys.size());
        // Go past every ip to get the data, and put it in the list
        for (String ip : keys) {
            iplist.add(new IPObject(ip, Long.parseLong(userlog.getString("users." + username + ".ip." + ip, "0"))));
        }

        Collections.sort(iplist, comparator);

        // return the list
        return new ArrayList<IPObject>(iplist.subList(0, (iplist.size() < maxSize ? iplist.size() : maxSize)));
    }

    /**
     * Returns a list with all of the usernames used by the given ip
     * @param username
     * @return 
     */
    public ArrayList<String> getIpUserList(String ip) {
        // Extra IP cleaning check
        ip = formatIP(ip);
        // Makes sure it wont mistake the ip for nodes in the yml file
        ip = ip.replaceAll("\\.", "_");

        // Get all ip's
        List<String> keys = userlog.getKeys("users");
        // Check if there are any keys
        if (keys == null) {
            return null;
        }
        // Create the return list
        ArrayList<String> usernamelist = new ArrayList<String>();

        // Loops trough all logged users
        for (String username : keys) {
            // Gets the ips logged for the current user
            List<String> ipList = userlog.getKeys("users." + username + ".ip");
            // Check if there are ip's
            if (ipList == null) {
                continue;
            }
            // Check if the ip is in the list
            if (ipList.contains(ip)) {
                // Add to the list
                usernamelist.add(username);
            }
        }
        // Return the list
        return usernamelist;
    }

    /**
     * Formats the ip address to remove the slash and port
     * @param ip
     * @return ip itself
     */
    public static String formatIP(String ip) {
        // Remove the slash at the start
        if (ip.contains("/")) {
            // Remove the slash
            ip = ip.substring(1);
        }
        // Check if the ip still contains the port
        if (ip.contains(":")) {
            // Remove the port
            ip = ip.split(":")[0];
        }
        return ip;
    }

    /**
     * Returns the username that is equal to the given username
     * but where the casing is not the same
     * @param username
     * @return 
     */
    public String checkCaseIndependant(String username) {
        // Get all ip's
        List<String> keys = userlog.getKeys("users");
        // Check if there are ip's
        if (keys == null) {
            return null;
        }
        // loop trough all names
        for (String name : keys) {
            if (name.equalsIgnoreCase(username)) {
                return name;
            }
        }
        return username;
    }

    /**
     * Checks if the users is already logged
     * @param username
     * @return 
     */
    public boolean isUserAlreadyLogged(String username) {
        // Get all ip's
        List<String> keys = userlog.getKeys("users");

        // Checks if the username is in the list
        if (keys.contains(username)) {
            return true;
        }
        // if not in the list, return false
        return false;
    }

    /**
     * Gets a list of players who's used an IP that is banned directly or indirectly (usernames)
     * @return 
     */
    public List<String> getIndirectlyBannedUserList() {

        // Get IP list
        List<String> bannedIps = getMCBannedIpsList();
        List<String> bannedNames = getMCBannedNamesList();

        // Get banned names
        for (String name : bannedNames) {
            // Get right cased name
            name = checkCaseIndependant(name);
            // Get all ip's
            List<String> keys = userlog.getKeys("users." + name + ".ip");
            // Check if there are ip's
            if (keys == null) {
                continue;
            }
            // If any logged ips add them to the bannedIps list
            for (String key : keys) {
                // Set ip syntax
                key.replaceAll("_", "\\.");
                // Add
                bannedIps.add(key);
            }
        }

        // Create a new list for the names of players who aren't banned
        // But an/the ip they used is.
        List<String> namesWithBannedIpUsage = new ArrayList<String>();
        // Loops trough all ips that are in the bannedIps list
        for (String ip : bannedIps) {
            // Gets all names that are used by the given ipo
            for (String name : getIpUserList(ip)) {
                // Convert to lowercase (thats how mc stores it)
                name = name.toLowerCase();
                // Check if the name is not already banned
                if (!bannedNames.contains(name) && !namesWithBannedIpUsage.contains(name)) {
                    // Add to list
                    namesWithBannedIpUsage.add(name);
                }
            }
        }

        //Return the list
        return namesWithBannedIpUsage;
    }

    /**
     * This returns a list with all ips that are in the banned-ips.txt
     * It uses the reader as its in the ServerConfigurationManager
     * @return 
     */
    private List<String> getMCBannedIpsList() {
        // Make a list
        List<String> bannedIps = new ArrayList<String>();
        try {
            //Open reader and get the file (from a mc method)
            BufferedReader bufferedreader = new BufferedReader(
                    new FileReader(((CraftServer) IPNotify.getPlugin().getServer()).getHandle().server.a("banned-ips.txt")));
            // Init input string
            String s = "";
            // Reader
            while ((s = bufferedreader.readLine()) != null) {
                // Add line to list
                bannedIps.add(s.trim().toLowerCase());
            }
            // Close reader
            bufferedreader.close();
        } catch (Exception exception) {
            IPNotify.writelog("Failed to load ip ban list: " + exception, true);
        }
        return bannedIps;
    }

    /**
     * This returns a list with all names that are in the banned-player.txt
     * It uses the reader as its in the ServerConfigurationManager
     * @return 
     */
    private List<String> getMCBannedNamesList() {
        // Make a list
        List<String> bannedNames = new ArrayList<String>();
        try {
            //Open reader and get the file (from a mc method)
            BufferedReader bufferedreader = new BufferedReader(
                    new FileReader(((CraftServer) IPNotify.getPlugin().getServer()).getHandle().server.a("banned-players.txt")));
            // Init input string
            String s = "";
            // Reader
            while ((s = bufferedreader.readLine()) != null) {
                // Add line to list
                bannedNames.add(s.trim().toLowerCase());
            }
            // Close reader
            bufferedreader.close();
        } catch (Exception exception) {
            IPNotify.writelog("Failed to load ban list: " + exception, true);
        }
        // Return
        return bannedNames;
    }
}

/**
 * Comparator for the IIP class
 * @author Hretsam
 */
class IIPComparator implements Comparator<IPObject> {

    /**
     * Compares 2 IPP objects on their date, to see which one is more recent
     * @param o1
     * @param o2
     * @return 
     */
    @Override
    public int compare(IPObject o1, IPObject o2) {
        if (o1.getDateLong() < o2.getDateLong()) {
            return 1;
        } else {
            return 0;
        }
    }
}

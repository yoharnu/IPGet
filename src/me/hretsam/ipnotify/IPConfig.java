package me.hretsam.ipnotify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author Hretsam
 */
public class IPConfig {

    private final String configfilename = "config.yml";
    private String selfnode;
    private String othernode;
    private String warningnode;
    private String dateSyntax;
    private boolean warnOnFirstJoin;
    private int maxIpListSize;

    public IPConfig(File datafolder) throws IOException {
        if (!datafolder.exists()) {
            datafolder.mkdirs();
        }
        File configfile = new File(datafolder.getAbsolutePath() + File.separator + configfilename);

        if (!configfile.exists()) {

            // Copys the config file from within this jar (default package) and writes it to the datafolder.
            configfile.createNewFile();
            try {
                InputStream stream = IPNotify.class.getResourceAsStream("/config.yml");
                OutputStream out = new FileOutputStream(configfile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = stream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                stream.close();
                out.close();
                IPNotify.writelog("Config file not found, created new file", false);
            } catch (IOException iex) {
                IPNotify.writelog("Cannot create config file! " + iex.getMessage(), true);
                IPNotify.getPlugin().getServer().getPluginManager().disablePlugin(IPNotify.getPlugin());
            }
        }

        // Loads the config file
        Configuration config = new Configuration(configfile);
        config.load();

        setupConfig(config);
    }

    /**
     * This loads all values of the config file
     * @param config 
     */
    private void setupConfig(Configuration config) {

        // Checks for the config version
        if (config.getInt("configversion", 0) < 2) {
            IPNotify.writelog("Your using an old config file, please update!", true);
        }

        // Loads all values, default value only used when key not found!
        dateSyntax = config.getString("date syntax", "dd-MMM-yyyy hh:mm");

        selfnode = config.getString("self node", "IPNotify.self");
        othernode = config.getString("other node", "IPNotify.other");
        warningnode = config.getString("warning node", "IPNotify.warning");

        warnOnFirstJoin = config.getBoolean("warn on first join", true);

        maxIpListSize = config.getInt("max iplist size", 6);

    }

    public String getConfigfilename() {
        return configfilename;
    }

    public String getDateSyntax() {
        return dateSyntax;
    }

    public String getOthernode() {
        return othernode;
    }

    public String getSelfnode() {
        return selfnode;
    }

    public String getWarningnode() {
        return warningnode;
    }

    public boolean doWarnOnFirstJoin() {
        return warnOnFirstJoin;
    }

    public int getMaxIpListSize() {
        return maxIpListSize;
    }
}

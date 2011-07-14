package me.hretsam.ipnotify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Hretsam
 */
public class Converter {

    public static boolean hasOldFiles(File datafolder) {
        //Look for the old IPGet Folder!
        File logsfolder = new File(datafolder.getAbsoluteFile().getParent() + File.separator + "IPGet" + File.separator + "logs");
        if (!logsfolder.exists()) {
            return false;
        }
        return true;
    }

    public static void convert(final File datafolder) {
        new Thread(new Runnable() {

            @Override
            public void run() {
//                File logsfolder = new File(datafolder + File.separator + "logs");
                File logsfolder = new File(datafolder.getAbsoluteFile().getParent() + File.separator + "IPGet" + File.separator + "logs");

                File[] logfiles = logsfolder.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        if (!name.startsWith("$") && name.endsWith(".log")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                FileReader fr;
                BufferedReader br;
                String input;

                DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String username, strdate, ip;
                String[] split, datesplit;
                Date date;

                IPNotify.writelog("Starting convertion of files.", false);
                IPNotify.writelog("Files waiting for convertion: " + logfiles.length, false);

                for (File file : logfiles) {

                    username = file.getName();
                    if (username.contains(".")) {
                        username = username.substring(0, username.length() - 4);
                    }

                    try {
                        fr = new FileReader(file);
                        br = new BufferedReader(fr);

                        try {
                            while ((input = br.readLine()) != null) {
                                split = input.split(" - ");
                                if (split.length != 2) {
                                    continue;
                                }
                                ip = FileHandler.formatIP(split[1]);
                                split = split[0].split(":");

                                datesplit = split[0].trim().split(" ");
                                strdate = (datesplit[1].length() == 1 ? "0" + datesplit[1] : datesplit[1]) + "-" + (datesplit[0].length() == 1 ? "0" + datesplit[0] : datesplit[1]) + "-" + datesplit[2];
                                strdate = strdate + " " + (split[1].length() == 2 ? "0" + split[1].substring(1) : split[1].substring(1)) + ":" + (split[2].length() == 1 ? "0" + split[2] : split[2]);
                                strdate = strdate.trim();

                                date = null;
                                try {
                                    date = format.parse(strdate);
                                } catch (ParseException ex) {
                                    IPNotify.writelog("Date Parse Error! " + ex.getMessage(), true);
                                }

                                IPNotify.getPlugin().getFilehandler().addIp(username, ip, date.getTime());
                            }
                        } catch (IOException ex) {
                            IPNotify.writelog("IOException! " + ex.getMessage(), true);
                        }
                        try {
                            fr.close();
                        } catch (IOException ex) {
                        }
                    } catch (FileNotFoundException ex) {
                        IPNotify.writelog("FileNotFoundException! " + ex.getMessage(), true);
                    }

                    file.renameTo(new File(file.getParent() + File.separator + "$" + file.getName()));
                }

                IPNotify.writelog("Convertion done", false);
                logsfolder.renameTo(new File(logsfolder.getParent() + File.separator + "oldlogs"));
            }
        }).start();
    }
}

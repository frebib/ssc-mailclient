package net.frebib.sscmailclient;

import net.frebib.util.IOHelper;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class SettingsManager {
    public static final String SETTINGS_DIR = "settings/";
    public static final String ACCOUNTS_DIR = SETTINGS_DIR + "accounts/";
    public static final String SETTINGS_FILE = "mailclient.properties";
    public static final String DEFAULT_ACCOUNT = "default";
    public static final String EXT = ".properties";

    public static Properties loadSettings() throws IOException {
        return IOHelper.loadProperties(SETTINGS_DIR + SETTINGS_FILE);
    }
    public static void saveSettings(Properties props) throws IOException {
        IOHelper.saveProperties(props, SETTINGS_DIR + SETTINGS_FILE);
    }
    public static Properties loadAccount() throws IOException {
        return loadAccount(DEFAULT_ACCOUNT);
    }
    public static boolean accountExists(String accountName) {
        return new File(ACCOUNTS_DIR + accountName + EXT).exists();
    }

    public static File[] getAccounts() {
        File dir = new File(ACCOUNTS_DIR);
        if (!dir.exists())
            return new File[0];
        return dir.listFiles((dir1, name) -> name.endsWith(EXT));
    }

    public static Properties loadAccount(String accountName) throws IOException {
        return IOHelper.loadProperties(ACCOUNTS_DIR + accountName + EXT);
    }
    public static void saveAccount(Properties account, String accountName) throws IOException {
        IOHelper.saveProperties(account, ACCOUNTS_DIR + accountName + EXT);
    }
}

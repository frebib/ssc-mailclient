package net.frebib.sscmailclient;

import net.frebib.sscmailclient.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class MailClient {
    public static final Log log = new Log(Level.FINER).setLogOutput(
            new SimpleDateFormat("'log/mailclient'yyyy-MM-dd hh-mm-ss'.log'")
            .format(new Date()));

    public MailClient() {
    }

    public void run() {
        try {
            throw new Exception("HELLO FUCKING WORLD");
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static void main(String[] args) {
        log.info("MailClient initialised");
        MailClient mc = new MailClient();
        mc.run();

        log.close();
    }
}

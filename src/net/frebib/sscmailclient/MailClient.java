package net.frebib.sscmailclient;

import net.frebib.sscmailclient.gui.MailClientFrame;
import net.frebib.sscmailclient.gui.MailClientView;
import net.frebib.sscmailclient.gui.ThreadedJFrame;
import net.frebib.util.Log;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

public class MailClient {
    public static final Log log = new Log(Level.FINER).setLogOutput(
            new SimpleDateFormat("'log/mailclient'yyyy-MM-dd hh-mm-ss'.log'")
            .format(new Date()));

    private MailClientView view;
    private MailClientFrame frame;
    private static ThreadedJFrame frameThread;

    public MailClient() {
        view = new MailClientView();
        frame = new MailClientFrame("JavaMail Client", view);
        frameThread = new ThreadedJFrame(frame, "FrameThread");
    }

    public void run() {
        //frameThread.setVisible(true);
        frame.dispose();

        Properties acc1;
        try {
            acc1 = SettingsManager.loadAccount("jxg415");
        } catch (IOException e) {
            log.exception(e);
            return;
        }

        IMAPProvider provider = new IMAPProvider(acc1);
        try {
            provider.connect();
            // Do stuff and things


        } catch (MessagingException e) {
            log.exception(e);
            return;
        }
    }

    public static void main(String[] args) {
        Thread.currentThread().setName("MailClient");
        log.info("MailClient initialised");
        MailClient mc = new MailClient();
        mc.run();

        Runtime.getRuntime().addShutdownHook(new Thread("Exit") {
            @Override
            public void run() {
                log.info("MailClient exiting");
                log.close();
            }
        });
    }
}

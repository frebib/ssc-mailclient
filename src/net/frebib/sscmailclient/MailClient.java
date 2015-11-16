package net.frebib.sscmailclient;

import net.frebib.sscmailclient.gui.MailClientFrame;
import net.frebib.sscmailclient.gui.MailClientView;
import net.frebib.sscmailclient.gui.ThreadedJFrame;
import net.frebib.util.Log;
import net.frebib.util.task.Worker;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

public class MailClient {
    public static final Log LOG = new Log(Level.FINER).setLogOutput(
            new SimpleDateFormat("'log/mailclient'yyyy-MM-dd hh-mm-ss'.log'")
            .format(new Date()));

    private MailClientView view;
    private MailClientFrame frame;

    private Mailbox mailbox;

    public MailClient() {
    }

    public void run() {
        Properties acc1;
        try {
            acc1 = SettingsManager.loadAccount("jxg415");
        } catch (IOException e) {
            LOG.exception(e);
            return;
        }

        mailbox = new Mailbox(new IMAPProvider(acc1), new SMTPProvider(acc1));
        view = new MailClientView(mailbox);
        frame = new MailClientFrame("JavaMail Client", view, mailbox);

        // Open incoming mailbox connection
        new Worker<>()
            .todo((d, p) -> mailbox.connect())
                .error(LOG::exception)
                .done(n -> {
                    LOG.info("Mailbox connected");

                    // Fetch emails in inbox
                    new Worker<Email[]>()
                            .todo((d, p) -> mailbox.fetchMessages("inbox", p))
                            .progress((p, m) -> System.out.println(p + "/" + m))
                            .start();
                }).start();

        // Start the window thread
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            public void windowOpened(WindowEvent e) { }
            public void windowClosed(WindowEvent e) { }
            public void windowIconified(WindowEvent e) { }
            public void windowDeiconified(WindowEvent e) { }
            public void windowActivated(WindowEvent e) { }
            public void windowDeactivated(WindowEvent e) { }
        });
    }

    /**
     * It's 7.01am. Comments can come later.
     * I'M GOING TO SLEEP
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("MailClient");
        LOG.info("MailClient initialised");
        MailClient mc = new MailClient();
        mc.run();

        Runtime.getRuntime().addShutdownHook(new Thread("Exit") {
            @Override
            public void run() {
                try {
                    // Safely close down connections
                    mc.mailbox.close();
                    LOG.info("MailClient exiting");
                    LOG.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

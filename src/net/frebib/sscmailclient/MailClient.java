package net.frebib.sscmailclient;

import net.frebib.sscmailclient.gui.MailClientFrame;
import net.frebib.sscmailclient.gui.MailClientView;
import net.frebib.sscmailclient.gui.ThreadedJFrame;
import net.frebib.util.Log;
import net.frebib.util.task.Worker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

public class MailClient {
    public static final Log LOG = new Log(Level.FINER).setLogOutput(
            new SimpleDateFormat("'log/mailclient'yyyy-MM-dd hh-mm-ss'.log'")
            .format(new Date()));

    private ThreadedJFrame frameThread;
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
        frameThread = new ThreadedJFrame(frame, "FrameThread");

        new Worker<>()
                .todo(() -> mailbox.connect())
                .error(LOG::exception)
                .done(n -> {
                    LOG.info("Mailbox connected");

                    new Worker<Email[]>()
                            .todo(() -> mailbox.fetchMessages("inbox"))
                            .start();
                }).start();

        // Do stuff and things
        frameThread.setVisible(true);
    }

    public static void main(String[] args) {
        Thread.currentThread().setName("MailClient");
        LOG.info("MailClient initialised");
        MailClient mc = new MailClient();
        mc.run();

        Runtime.getRuntime().addShutdownHook(new Thread("Exit") {
            @Override
            public void run() {
                LOG.info("MailClient exiting");
                LOG.close();
            }
        });
    }
}

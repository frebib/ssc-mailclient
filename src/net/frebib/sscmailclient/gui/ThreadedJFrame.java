package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.MailClient;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ThreadedJFrame implements WindowListener {
    public JFrame frame;
    public Thread thread;
    private Runnable run;

    private String threadName;

    public ThreadedJFrame(JFrame frame, String threadName) {
        this.frame = frame;
        this.threadName = threadName;
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(this);

        run = new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        };
    }

    public void setVisible(boolean visible) {
        if (visible)
            show();
        else
            hide();
    }

    public void show() {
        thread = new Thread(run, threadName);
        thread.start();
    }

    public void hide() {
        frame.setVisible(false);
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        MailClient.LOG.info("Window \"" + frame.getTitle() + "\" opened");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        MailClient.LOG.info("Window \"" + frame.getTitle() + "\" closing");
        frame.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        MailClient.LOG.info("Window \"" + frame.getTitle() + "\" closed");
    }
    public void windowIconified(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
}

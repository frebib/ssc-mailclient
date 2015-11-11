package net.frebib.sscmailclient.gui;

import javax.swing.*;

public class MailClientFrame extends JFrame {
    private MailClientView view;

    public MailClientFrame(String title, MailClientView view) {
        super(title);
        this.view = view;

        add(view);
        pack();
    }
}

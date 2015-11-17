package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Mailbox;
import net.frebib.util.task.Worker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MailClientFrame extends JFrame {
    private MailClientView view;
    private MailClientControls controls;

    private ComposeFrame composer;

    private Mailbox mailbox;

    public MailClientFrame(String title, MailClientView view, Mailbox mailbox) {
        super(title);
        this.mailbox = mailbox;
        this.view = view;
        this.controls = new MailClientControls(this);
        this.composer = new ComposeFrame(this, mailbox);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(controls, c);

        c.gridy = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(view, c);

        setMinimumSize(new Dimension(1000, 600));
        setPreferredSize(getMinimumSize());
        pack();
        setLocationRelativeTo(null);
    }

    private class MailClientControls extends JPanel {
        private JButton btnFilter, btnCompose;
        private JTextFieldHint txtSearch;

        public MailClientControls(Component parent) {
            btnCompose = new JButton("Compose");
            btnCompose.addActionListener(e -> {
                composer.setLocationRelativeTo(parent);
                composer.setVisible(true);
            });

            txtSearch = new JTextFieldHint("Search");
            txtSearch.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        mailbox.searchIn(mailbox.getCurrent(), txtSearch.getText());
                    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        mailbox.searchIn(mailbox.getCurrent(), "");
                        txtSearch.setText("");
                        txtSearch.getParent().requestFocus();
                    }
                }
                @Override
                public void keyTyped(KeyEvent e) {
                }
                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            setBorder(BorderFactory.createEmptyBorder(12, 8, 2, 8));
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = GridBagConstraints.RELATIVE;
            c.gridy = 0;
            c.weightx = 0;
            c.weighty = 1;
            c.ipadx = 6;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.LINE_START;
            add(btnCompose, c);

            c.weightx = 0.8;
            c.anchor = GridBagConstraints.CENTER;
            add(new JPanel(), c);

            c.weightx = 0.4;
            c.anchor = GridBagConstraints.LINE_END;
            add(txtSearch, c);

            pack();
        }
    }
}

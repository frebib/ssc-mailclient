package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Mailbox;

import javax.swing.*;
import java.awt.*;

public class MailClientFrame extends JFrame {
    private MailClientView view;
    private MailClientControls controls;

    private ComposeFrame composer;

    public MailClientFrame(String title, MailClientView view, Mailbox mailbox) {
        super(title);
        this.view = view;
        this.controls = new MailClientControls();
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

        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(800, 600));
        pack();
    }

    private class MailClientControls extends JPanel {
        private JButton btnCompose;
        private JTextFieldHint txtSearch;

        public MailClientControls() {
            btnCompose = new JButton("Compose");
            //btnCompose.addActionListener(e -> ((JFrame)this.getRootPane().getParent()).dispose());
            btnCompose.addActionListener(e -> {
                composer.setLocationRelativeTo(this);
                composer.setVisible(true);
            });

            txtSearch = new JTextFieldHint("Search");

            setBorder(BorderFactory.createEmptyBorder(12, 8, 2, 8));
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = GridBagConstraints.RELATIVE;
            c.gridy = 0;
            c.weightx = 0.1;
            c.weighty = 1;
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

package net.frebib.sscmailclient.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Comparator;
import java.util.List;

public class ComposeFrame extends JFrame {
    private JPanel mainPanel, headerPanel, attachPanel;
    private JLabel lTo, lCc, lBcc, lSubj;
    private JTextField tTo, tCc, tBcc, tSubj;
    private JTextArea txtBody;
    private JButton btnSend;

    public ComposeFrame() {
        super("Compose an Email");

        attachPanel = new AttachmentFrame();
        btnSend = ((AttachmentFrame) attachPanel).btnSend;

        lTo = new JLabel("To", SwingConstants.RIGHT);
        lCc = new JLabel("Cc", SwingConstants.RIGHT);
        lBcc = new JLabel("Bcc", SwingConstants.RIGHT);
        lSubj = new JLabel("Subject", SwingConstants.RIGHT);

        tTo = new JTextField();
        tCc = new JTextField();
        tBcc = new JTextField();
        tSubj = new JTextField();

        headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.ipadx = c.ipady = 4;
        c.insets = new Insets(0, 0, 8, 12);

        headerPanel.add(lTo, c);
        headerPanel.add(lCc, c);
        headerPanel.add(lBcc, c);
        c.insets = new Insets(0, 0, 0, 12);
        headerPanel.add(lSubj, c);

        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 8, 0);

        headerPanel.add(tTo, c);
        headerPanel.add(tCc, c);
        headerPanel.add(tBcc, c);
        headerPanel.add(tSubj, c);

        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 0, 0);
        headerPanel.add(attachPanel, c);

        //c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        //c.weighty = 0.3;

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.add(headerPanel, c);

        txtBody = new JTextArea();

        c.weighty = 0.65;
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.BOTH;
        mainPanel.add(txtBody, c);

        add(mainPanel);

        pack();
    }
}
package net.frebib.sscmailclient.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ComposeFrame extends JFrame {
    private JPanel mainPanel, headerPanel;
    private JLabel lTo, lCc, lBcc, lSubj;
    private JTextField tTo, tCc, tBcc, tSubj;
    private JButton btnAttach, btnSend;
    private JList<File> attachList;
    private JTextArea txtBody;

    public ComposeFrame() {
        super("Compose an Email");

        lTo = new JLabel("To");
        lCc = new JLabel("Cc");
        lBcc = new JLabel("Bcc");
        lSubj = new JLabel("Subject");

        tTo = new JTextField();
        tCc = new JTextField();
        tBcc = new JTextField();
        tSubj = new JTextField();

        headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.ipadx = c.ipady = 4;

        headerPanel.add(lTo, c);
        headerPanel.add(lCc, c);
        headerPanel.add(lBcc, c);
        headerPanel.add(lSubj, c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        headerPanel.add(tTo, c);
        headerPanel.add(tCc, c);
        headerPanel.add(tBcc, c);
        headerPanel.add(tSubj, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        //c.weighty = 0.3;

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.add(headerPanel, c);

        txtBody = new JTextArea();

        c.weighty = 0.65;
        c.fill = GridBagConstraints.BOTH;
        mainPanel.add(txtBody, c);

        add(mainPanel);

        pack();
    }
}
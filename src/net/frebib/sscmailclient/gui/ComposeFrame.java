package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.MailClient;
import net.frebib.sscmailclient.Mailbox;
import net.frebib.sscmailclient.UnsentEmail;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComposeFrame extends JDialog implements WindowListener {
    private JPanel mainPanel, headerPanel, attachPanel;
    private JLabel lTo, lCc, lBcc, lSubj;
    private JTextField tTo, tCc, tBcc, tSubj;
    private JTextArea txtBody;
    private JButton btnSend;

    private Mailbox mailbox;

    public ComposeFrame(Frame parent, Mailbox mailbox) {
        super(parent, "Compose an Email", true);
        addWindowListener(this);

        this.mailbox = mailbox;

        attachPanel = new AttachmentFrame();
        btnSend = ((AttachmentFrame) attachPanel).btnSend;
        btnSend.addActionListener(e -> sendClick(e));

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
        c.insets = new Insets(4, 0, 0, 0);
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

        setMinimumSize(new Dimension(700, 700));
        setPreferredSize(getMinimumSize());
        pack();
        setLocationRelativeTo(parent);
    }

    private void sendClick(ActionEvent e) {
        List<String> errors = new ArrayList<>();
        String[] recipients;

        if (tTo.getText().isEmpty())
            errors.add("The email must have at least one recipient");
        else
            errors.addAll(checkAddressErrors(tTo.getText()));

        errors.addAll(checkAddressErrors(tCc.getText()));
        errors.addAll(checkAddressErrors(tBcc.getText()));

        String[] arr = new String[errors.size()];
        errors.toArray(arr);

        if (errors.size() > 0) {
            JOptionPane.showMessageDialog(this, String.join("\n", arr));
            return;
        }

        if (tSubj.getText().isEmpty() && txtBody.getText().isEmpty())
            JOptionPane.showMessageDialog(this, "You can't send a blank email.\n" +
                    "Please fill in either the subject or message body");

        if (tSubj.getText().isEmpty()) {
            int answer = JOptionPane.showOptionDialog(this,
                    "Are you sure you want to send the email without a subject?",
                    "Are you sure?", JOptionPane.YES_OPTION | JOptionPane.CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (answer != JOptionPane.YES_OPTION)
                return;
        }

        if (txtBody.getText().isEmpty()) {
            int answer = JOptionPane.showOptionDialog(this,
                    "Are you sure you want to send the email without body text?",
                    "Are you sure?", JOptionPane.YES_OPTION | JOptionPane.CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (answer != JOptionPane.YES_OPTION)
                return;
        }

        // Can send!
        try {
            UnsentEmail email = new UnsentEmail(makeAddresses(tTo.getText()),
                    txtBody.getText(), tSubj.getText());
            if (!tCc.getText().isEmpty())
                email.setCc(makeAddresses(tCc.getText()));
            if (!tBcc.getText().isEmpty())
                email.setBcc(makeAddresses(tBcc.getText()));

            email.setAttachments(new ArrayList<File>(((AttachmentFrame)attachPanel).attachList.keySet()));

            mailbox.send(email);

            // Close the form after email is sent
            this.setVisible(false);
            this.dispose();
        } catch (Exception ex) {
            MailClient.LOG.exception(ex);
        }
    }

    private Address[] makeAddresses(String addresses) throws AddressException {
        String[] strings = cutAddresses(addresses);
        Address[] addr = new Address[strings.length];
        for (int i = 0; i < strings.length; i++)
            addr[i] = new InternetAddress(strings[i]);
        return addr;
    }

    private String[] cutAddresses(String addresses) {
        List<String> ls = Arrays.asList(addresses.split("[,;]"));
        return ls.stream().filter(s -> !s.isEmpty())
                          .map(String::trim)
                          .toArray(String[]::new);
    }

    private List<String> checkAddressErrors(String addr) {
        String[] addresses = cutAddresses(addr);
        List<String> errors = new ArrayList<>(addresses.length);
        for (String s : addresses) {
            try {
                new InternetAddress(s).validate();
            } catch (AddressException ex) {
                errors.add("There is an error in the email address \"" + s + "\"");
            }
        }
        return errors;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        MailClient.LOG.info("Window \"" + this.getTitle() + "\" opened");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        MailClient.LOG.info("Window \"" + this.getTitle() + "\" closing");
        this.dispose();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
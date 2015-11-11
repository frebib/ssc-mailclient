package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.MailClient;
import net.frebib.sscmailclient.Mailbox;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MailClientView extends JPanel implements ListSelectionListener {
    private JSplitPane splitPane;
    private JList<Email> emailList;
    private EmailListModel listModel;
    private JTextField emailPreview;
    private JScrollPane leftScroll, rightScroll;
    private JButton btnCompose;
    private JTextFieldHint txtSearch;

    private final Mailbox mailbox;

    public MailClientView(Mailbox mailbox) {
        super();

        this.mailbox = mailbox;
        init();
    }

    private void init() {
        listModel = new EmailListModel();
        // TODO: Remove this
        listModel.add(mailbox.getMessages("inbox"));

        emailList = new JList<>();
        emailList.addListSelectionListener(this);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emailList.setModel(listModel);
        emailList.setCellRenderer(new EmailCellRenderer());

        leftScroll = new JScrollPane(emailList);
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        emailPreview = new JTextField();
        rightScroll = new JScrollPane(emailPreview);

        splitPane = new JSplitPane();
        splitPane.setLeftComponent(leftScroll);
        splitPane.setRightComponent(rightScroll);

        btnCompose = new JButton("Compose");
        //btnCompose.addActionListener(e -> ((JFrame)this.getRootPane().getParent()).dispose());

        txtSearch = new JTextFieldHint("Search");

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(btnCompose, BorderLayout.NORTH);
        //add(txtSearch, BorderLayout.NORTH);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Email em = emailList.getSelectedValue();
        emailPreview.setText(em.getBody());
    }
}

package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.Mailbox;
import net.frebib.util.task.Worker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class MailClientView extends JPanel implements ListSelectionListener {
    private JSplitPane splitPane;
    private JList<Email> emailList;
    private EmailListModel listModel;
    private JTextArea emailPreview;
    private JScrollPane leftScroll, rightScroll;
    private JButton btnCompose;
    private JTextFieldHint txtSearch;

    private final Mailbox mailbox;

    private Worker<String> emailGrabber;

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

        emailPreview = new JTextArea();
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
        if (emailGrabber != null && !emailGrabber.isComplete())
            emailGrabber.cancel();

        emailGrabber = new Worker<String>()
                .todo(() -> emailList.getSelectedValue().getBody())
                .onComplete(s -> emailPreview.setText(s))
                .start();
    }
}

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

    private final Mailbox mailbox;

    private Worker<String> emailGrabber;
    private Email grabbing;

    public MailClientView(Mailbox mailbox) {
        super();
        this.mailbox = mailbox;
        init();

        mailbox.addObserver(listModel);
    }

    private void init() {
        listModel = new EmailListModel();

        emailList = new JList<>();
        emailList.addListSelectionListener(this);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emailList.setModel(listModel);
        emailList.setCellRenderer(new EmailCellRenderer());

        leftScroll = new JScrollPane(emailList);
        leftScroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 2));
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        emailPreview = new JTextArea();
        rightScroll = new JScrollPane(emailPreview);
        rightScroll.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 8));

        splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.15);
        splitPane.setLeftComponent(leftScroll);
        splitPane.setRightComponent(rightScroll);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Email em = emailList.getSelectedValue();
        em.setRead(true);
        listModel.updateElem(em);
        emailList.setSelectedValue(em, true);

        if (grabbing != null && !em.equals(grabbing))
            if (emailGrabber != null && !emailGrabber.isComplete())
                emailGrabber.cancel();

        emailGrabber = new Worker<String>()
                .get(() -> emailList.getSelectedValue().getBody())
                .done(s -> emailPreview.setText(s))
                .start();
    }
}

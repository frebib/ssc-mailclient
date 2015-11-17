package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.Mailbox;
import net.frebib.util.task.Worker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MailClientView extends JPanel implements ListSelectionListener {
    private JSplitPane splitPane;
    private JList<Email> emailList;
    public EmailListModel listModel;
    private JTextArea emailPreview;
    private JScrollPane leftScroll, rightScroll;

    private JPopupMenu rightClickMenu;

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
        emailList = new JList<>();
        listModel = new EmailListModel(emailList);

        emailList.addListSelectionListener(this);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emailList.setModel(listModel);
        emailList.setCellRenderer(new EmailCellRenderer());

        leftScroll = new JScrollPane(emailList);
        leftScroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 2));
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        emailPreview = new JTextArea();
        emailPreview.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        emailPreview.setEditable(false);
        rightScroll = new JScrollPane(emailPreview);
        rightScroll.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 8));

        splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.15);
        splitPane.setLeftComponent(leftScroll);
        splitPane.setRightComponent(rightScroll);

        rightClickMenu = new JPopupMenu();
        emailList.addMouseListener(new RightClickHandler());

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
                .todo((c, p) -> c.onComplete(emailList.getSelectedValue().getBody()))
                .done(s -> emailPreview.setText(s))
                .start();
    }

    private class RightClickHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            onMouseEvent(e);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            onMouseEvent(e);
        }
        private void onMouseEvent(MouseEvent e) {
            if (e.isPopupTrigger()) {
                Email em = listModel.getElementAt(emailList.locationToIndex(e.getPoint()));
                initMenu(em);
                emailList.setSelectedValue(em, true);
                rightClickMenu.show(emailList, e.getX(), e.getY());
            }
        }

        public void initMenu(Email em) {
            rightClickMenu.removeAll();
            JMenuItem itm;
            itm = new JMenuItem("Reply");
            itm.addActionListener(e -> reply(em));
            rightClickMenu.add(itm);
            itm = new JMenuItem("Forward");
            itm.addActionListener(e -> forward(em));
            rightClickMenu.add(itm);
            itm = new JMenuItem("Delete");
            itm.addActionListener(e -> delete(em));
            rightClickMenu.add(itm);
            rightClickMenu.add(new JSeparator());
            itm = new JMenuItem("Mark as Unread");
            itm.addActionListener(e -> markAsUnread(em));
            rightClickMenu.add(itm);
            JMenu subm = new JMenu("Set Flags");
            for (Email.CustomFlag cf : Email.CustomFlag.values()) {
                itm = new JMenuItem(cf.flag);
                itm.addActionListener(e -> flags(em, cf));
                subm.add(itm);
            }
            rightClickMenu.add(subm);
        }
        private void reply(Email e) {

        }
        private void forward(Email e) {

        }
        private void markAsUnread(Email e) {
            e.setRead(false);
            emailList.clearSelection();
            emailPreview.setText("");
            listModel.updateElem(e);
        }
        private void flags(Email e, Email.CustomFlag flag) {
            e.toggleCustomFlag(flag);
            listModel.updateElem(e);
        }
        private void delete(Email e) {
            e.delete();
            listModel.remove(e);
        }
    }
}

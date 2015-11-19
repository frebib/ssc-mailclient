package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.Mailbox;
import net.frebib.util.task.Worker;

import javax.mail.Folder;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MailClientView extends JPanel implements ListSelectionListener, ItemListener {
    private JList<Email> emailList;
    public EmailListModel listModel;
    private JTextArea emailPreview;
    private JComboBox<Folder> cmbFolder;

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

        JPanel topPane = new JPanel(new BorderLayout());
        JPanel leftPane = new JPanel(new BorderLayout());
        cmbFolder = new JComboBox<>();
        cmbFolder.addItemListener(this);
        topPane.add(cmbFolder, BorderLayout.CENTER);
        topPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JButton btnReload = new JButton("↻");
        btnReload.addActionListener(e -> {
            new Worker<>()
                    .todo((d, p) -> {
                        listModel.clear();
                        mailbox.reloadFolder(mailbox.getCurrent());
                    }).start();
        });
        topPane.add(btnReload, BorderLayout.EAST);

        JScrollPane leftScroll = new JScrollPane(emailList);
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        leftPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 2));
        leftPane.add(leftScroll, BorderLayout.CENTER);
        leftPane.add(topPane, BorderLayout.NORTH);

        emailPreview = new JTextArea();
        emailPreview.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        emailPreview.setEditable(false);
        JScrollPane rightScroll = new JScrollPane(emailPreview);
        rightScroll.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 8));

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.15);
        splitPane.setLeftComponent(leftPane);
        splitPane.setRightComponent(rightScroll);

        rightClickMenu = new JPopupMenu();
        emailList.addMouseListener(new RightClickHandler());

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    public void setFolders(List<Folder> folders) {
        if (folders != null)
            folders.stream().forEach(cmbFolder::addItem);
    }
    public void setCurrent(Folder current) {
        cmbFolder.setSelectedItem(current);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Email em = emailList.getSelectedValue();
        if (em == null)
            return;
        em.setRead(true);
        listModel.updateElem(em);

        if (grabbing != null && !em.equals(grabbing))
            if (emailGrabber != null && !emailGrabber.isComplete())
                emailGrabber.cancel();

        grabbing = emailList.getSelectedValue();
        emailGrabber = new Worker<String>("Body Grabber")
                .todo((c, p) -> c.onComplete(grabbing.getBody()))
                .done(s -> emailPreview.setText(s))
                .start();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (cmbFolder.getSelectedItem() == null)
            return;

        listModel.clear();
        new Worker<>("Folder Fetcher")
                .todo((d, p) -> listModel.add(mailbox.getEmails((Folder) cmbFolder.getSelectedItem())))
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
                emailList.setSelectedValue(em, false);
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
        // Right click menu handlers
        private void reply(Email e) {
            // TODO: Load compose form with reply
        }
        private void forward(Email e) {
            // TODO: Load compose form with forward
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

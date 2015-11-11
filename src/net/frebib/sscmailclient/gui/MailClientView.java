package net.frebib.sscmailclient.gui;

import javax.swing.*;
import java.awt.*;

public class MailClientView extends JPanel {
    private JSplitPane splitPane;
    private JList<JLabel> emailList;
    private JTextField emailPreview;
    private JScrollPane leftScroll, rightScroll;
    private JButton btnCompose;
    private JTextFieldHint txtSearch;

    public MailClientView() {
        super();
        init();
    }

    private void init() {
        emailList = new JList<JLabel>();
        leftScroll = new JScrollPane(emailList);

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
}

package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.MailClient;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AttachmentFrame extends JPanel implements ActionListener, MouseListener {
    private JPanel attachPanel;
    public LinkedHashMap<File, JPanel> attachList;
    public JButton btnAttach, btnSend;

    public AttachmentFrame() {
        super(new GridBagLayout());

        attachList = new LinkedHashMap<>();
        attachPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnSend = new JButton("Send!");

        btnAttach = new JButton("Attach");
        btnAttach.addActionListener(this);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 8;
        c.insets = new Insets(0, 0, 0, 12);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.LINE_START;
        add(btnAttach, c);

        c.gridx = 1;
        c.ipadx = 0;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        add(new JScrollPane(attachPanel), c);

        c.gridx = 2;
        c.ipadx = 20;
        c.weightx = 0;
        c.insets = new Insets(0, 24, 0, 0);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.LINE_END;
        add(btnSend, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MailClient.LOG.fine("Attach button clicked");
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);

        int ret = chooser.showOpenDialog(this.getParent());
        if (ret != JFileChooser.APPROVE_OPTION)
            return;

        File[] files = chooser.getSelectedFiles();
        for (File f : files) {
            MailClient.LOG.finer("File \"" + f.getName() + "\" attached");
            JPanel fpanel = getFilePanel(f);
            attachList.put(f, fpanel);
        }
        updateView();
    }
    private void updateView() {
        attachPanel.removeAll();
        for (JPanel jp : attachList.values())
            attachPanel.add(jp);

        attachPanel.revalidate();
    }
    private JPanel getFilePanel(File f) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        JLabel label = new JLabel(f.getName());
        panel.add(label);
        panel.addMouseListener(this);
        return panel;
    }

    private void menuClick(JPanel source) {
        // TODO: Add right click menu
    }

    @Override
    public void mousePressed(MouseEvent e) {
        menuClick((JPanel)e.getSource());
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        menuClick((JPanel)e.getSource());
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
}

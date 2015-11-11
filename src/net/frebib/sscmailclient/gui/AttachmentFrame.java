package net.frebib.sscmailclient.gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AttachmentFrame extends JPanel {
    private JList<File> attachList;
    public JButton btnAttach, btnSend;

    public AttachmentFrame() {
        super(new GridBagLayout());
        attachList = new JList<>(new AttachmentListModel());
        attachList.setCellRenderer(new AttachmentCellRenderer());
        attachList.setAlignmentX(JList.HORIZONTAL_WRAP);
        attachList.setLayout(new FlowLayout());

        btnAttach = new JButton("Attach");
        btnAttach.addActionListener(e -> {});

        btnSend = new JButton("Send!");
        btnSend.addActionListener(e -> {});

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
        add(attachList, c);

        c.gridx = 2;
        c.ipadx = 20;
        c.weightx = 0;
        c.insets = new Insets(0, 24, 0, 0);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.LINE_END;
        add(btnSend, c);

    }

    private class AttachmentListModel extends AbstractListModel<File> implements Comparator<File> {
        private List<File> files;

        public AttachmentListModel() {
            files = new ArrayList<>();
        }

        public void add(File f) {
            files.add(f);
            files.sort(this);
            fireContentsChanged(this, 0, files.size());
        }

        public void remove(File f) {
            remove(f);
        }

        public void remove(int i) {
            files.remove(i);
            fireContentsChanged(this, i, i);
        }

        @Override
        public int getSize() {
            return files.size();
        }

        @Override
        public File getElementAt(int i) {
            return files.get(i);
        }

        @Override
        public int compare(File f1, File f2) {
            return -f1.getName().compareTo(f2.getName());
        }
    }

    private class AttachmentCellRenderer extends JPanel implements ListCellRenderer<File> {
        private JLabel label;

        public AttachmentCellRenderer() {
            //UIDefaults lf = UIManager.getLookAndFeel().getDefaults();
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(4, 4, 4, 4),
                    BorderFactory.createSoftBevelBorder(BevelBorder.RAISED)
            ));
        }

        public Component getListCellRendererComponent(JList<? extends File> list, File f,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            label.setText(f.getName());
            return this;
        }
    }
}

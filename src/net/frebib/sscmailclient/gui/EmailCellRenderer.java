package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.MailClient;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;

public class EmailCellRenderer extends JPanel implements ListCellRenderer<Email> {
    private final Color BG_COL, FG_COL, HL_BG_COL, HL_FG_COL;
    private final Font DEF_FONT;

    private JLabel subject, sender, date;

    public EmailCellRenderer() {
        subject = new JLabel();
        sender = new JLabel();
        date = new JLabel();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(subject);
        add(sender);
        add(date);

        UIDefaults lf = UIManager.getLookAndFeel().getDefaults();
        BG_COL = this.getBackground();
        FG_COL = subject.getForeground();
        HL_BG_COL = lf.getColor("List.selectionBackground");
        HL_FG_COL = lf.getColor("List.selectionForeground");

        DEF_FONT = subject.getFont();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Email> list, Email em,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) setColors(HL_BG_COL, HL_FG_COL);
        else setColors(BG_COL, FG_COL);

        subject.setText(em.getSubject());

        boolean read = !em.isRead();
        MailClient.LOG.info(em.getSubject() + ", seen: " + Boolean.toString(read));
        subject.setFont(DEF_FONT.deriveFont(
                DEF_FONT.getStyle() | (read ? Font.BOLD : Font.PLAIN),
                DEF_FONT.getSize() * 1.4f)
        );

        sender.setText(em.getFrom().toString());
        date.setText(em.getReceivedDate().toString());

        return this;
    }

    private void setColors(Color bg, Color fg) {
        setBackground(bg);
        subject.setForeground(fg);
        sender.setForeground(fg);
        date.setForeground(fg);
    }
}

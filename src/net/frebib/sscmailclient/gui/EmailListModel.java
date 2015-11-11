package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.MailClient;

import javax.mail.MessagingException;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class EmailListModel extends AbstractListModel<Email> implements Comparator<Email> {
    private ArrayList<Email> emails;

    public EmailListModel() {
        emails = new ArrayList<>();
    }

    public void add(Email... email) {
        Collections.addAll(emails, email);
        Collections.sort(emails, this);
        fireContentsChanged(this, 0, emails.size());
    }
    public void update() {
        updateAt(0, emails.size());
    }
    public void updateElem(Email email) {
        updateAt(emails.indexOf(email));
    }
    public void updateAt(int i) {
        updateAt(i, i);
    }
    public void updateAt(int i, int j) {
        fireContentsChanged(this, i, j);
        Collections.sort(emails, this);
    }
    public void clear() {
        emails.clear();
        fireContentsChanged(this, 0, emails.size());
    }
    @Override
    public int getSize() {
        return emails.size();
    }
    @Override
    public Email getElementAt(int i) {
        return emails.get(i);
    }

    @Override
    public int compare(Email e1, Email e2) {
        boolean r1 = e1.isRead(),
                r2 = e2.isRead(),
                r3 = e1.isRecent(),
                r4 = e2.isRecent();

        if (r1 ^ r2)
            return Boolean.compare(r1, r2);

        if (r3 ^ r4)
            return Boolean.compare(r3, r4);

        return e2.getReceivedDate().compareTo(e1.getReceivedDate());
    }
}

package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.MailClient;

import javax.mail.MessagingException;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


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
    public void updateElem(Email email) {
        updateAt(emails.indexOf(email));
    }
    public void updateAt(int i) {
        updateAt(i, i);
    }
    public void updateAt(int i, int j) {
        fireContentsChanged(this, i, j);
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
    public int compare(Email o1, Email o2) {
        if (!o1.isRead() && o2.isRead())
            return -1;
        else if (o1.isRead() && !o2.isRead())
            return 1;
        return o1.getReceivedDate().compareTo(o2.getReceivedDate());
    }
}

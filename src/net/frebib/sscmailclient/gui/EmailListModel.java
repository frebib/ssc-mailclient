package net.frebib.sscmailclient.gui;

import net.frebib.sscmailclient.Email;
import net.frebib.sscmailclient.MailClient;
import net.frebib.util.IndexedTreeSet;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;


public class EmailListModel extends AbstractListModel<Email> implements Comparator<Email>, Observer {
    private IndexedTreeSet<Email> emails;

    public EmailListModel() {
        emails = new IndexedTreeSet<>(this);
    }

    public void add(Email e) {
        e.addObserver(this);
        emails.add(e);
        fireContentsChanged(this, 0, emails.size());
    }
    public void add(Email... em) {
        List<Email> ems = Arrays.asList(em);
        ems.stream().forEach(el -> el.addObserver(this));
        this.emails.addAll(ems);
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
    }
    public void clear() {
        emails.clear();
        fireContentsChanged(this, 0, emails.size());
    }
    public void remove(Email e) {
        int i = emails.indexOf(e);
        emails.remove(e);
        fireContentsChanged(this, i, i);
    }
    public void remove(int i) {
        emails.remove(i);
        fireContentsChanged(this, i, i);
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
        if (e1 == null || e2 == null)
            return 0;

        boolean r1 = e1.isRead(),
                r2 = e2.isRead(),
                r3 = e1.isRecent(),
                r4 = e2.isRecent();

        if (r1 ^ r2)
            return Boolean.compare(r1, r2);

        if (r3 ^ r4)
            return Boolean.compare(r3, r4);

        if (e1.getReceivedDate() == null || e2.getReceivedDate() == null)
            return 0;

        return e2.getReceivedDate().compareTo(e1.getReceivedDate());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == null)
            return;

        if (arg instanceof Email)
            add((Email) arg);
        else if (arg instanceof Email[])
            add((Email[]) arg);
        else
            MailClient.LOG.info("Unexpected type on update... :" + arg.toString());
    }
}

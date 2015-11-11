package net.frebib.sscmailclient;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.util.Collections;
import java.util.Observable;

public class Mailbox extends Observable {
    private MailProvider mail;
    private SendProvider send;

    private Store store;
    private Email[] emails;

    public Mailbox(MailProvider mail, SendProvider send) {
        if (mail == null && send == null)
            throw new IllegalArgumentException("Must have either a mail or send provider");
        this.mail = mail;
        this.send = send;
    }

    public void connect() throws MessagingException {
        if (mail != null)
            store = mail.connect();
        if (send != null)
            send.connect();
    }

    public Folder getFolder(String path) {
        try {
            return store.getFolder(path);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public Email[] getMessages(String path) {
        try {
            Folder folder = getFolder(path);
            if (!folder.isOpen())
                folder.open(Folder.READ_WRITE);

            Message[] msgs = folder.getMessages();
            emails = new Email[msgs.length];
            for (int i=0; i < msgs.length; i++)
                emails[i] = new Email(msgs[i]);

            return emails;
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public Folder[] getFolderList() {
        try {
            return store.getDefaultFolder().list();
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }
}

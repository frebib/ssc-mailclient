package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.Collections;
import java.util.Observable;

public class Mailbox extends Observable {
    private MailProvider mail;
    private SendProvider send;

    private Store store;
    private Email[] emails;

    private Transport transport;

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
            transport = send.connect();
    }

    public Folder getFolder(String path) {
        try {
            return store.getFolder(path);
        } catch (Exception e) {
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
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public Folder[] getFolderList() {
        try {
            return store.getDefaultFolder().list();
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public void send(UnsentEmail email) {
        try {
            transport.sendMessage(email.prepare(), email.getRecipients());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

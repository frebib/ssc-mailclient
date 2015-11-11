package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.Observable;

public class Mailbox extends Observable {
    private MailProvider mail;
    private SendProvider send;

    private Store store;
    private Message[] emails;

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

    public Folder getFolder(String path) throws MessagingException {
        return store.getFolder(path);
    }

    public Message[] getMessages(String path) throws MessagingException {
        Folder folder = getFolder(path);
        if (!folder.isOpen())
            folder.open(Folder.READ_WRITE);
        return folder.getMessages();
    }

    public Folder[] getFolderList() throws MessagingException {
        return store.getDefaultFolder().list();
    }
}

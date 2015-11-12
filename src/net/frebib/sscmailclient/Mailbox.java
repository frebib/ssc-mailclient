package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.Observable;

public class Mailbox extends Observable {
    private MailProvider mail;
    private SendProvider send;

    private Session sendSession;
    private Store store;

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
        if (send != null) {
            transport = send.connect();
            sendSession = send.getSession();
        }
        if (store != null && store.isConnected())
            MailClient.LOG.fine("Mail connected");
        if (transport != null & transport.isConnected())
            MailClient.LOG.fine("Send connected");
    }

    public Folder getFolder(String path) {
        try {
            MailClient.LOG.finer("Loading folder \"" + path + "\"");
            return store.getFolder(path);
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public void fetchMessages(String path) {
        setChanged();
        notifyObservers(getMessages(path));
    }

    public Email[] getMessages(String path) {
        try {
            Folder folder = getFolder(path);
            if (!folder.isOpen())
                folder.open(Folder.READ_WRITE);

            Message[] msgs = folder.getMessages();
            Email[] emails = new Email[msgs.length];
            for (int i = 0; i < msgs.length; i++)
                emails[i] = new Email(msgs[i]);

            return emails;
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public Folder[] getFolderList() {
        try {
            MailClient.LOG.finer("Loading default folder");
            return store.getDefaultFolder().list();
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public void send(UnsentEmail email) {
        try {
            Message msg = email.prepare(sendSession);
            MailClient.LOG.info("Sending email: " + msg.getSubject());
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
    }
}

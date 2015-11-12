package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Mailbox extends Observable {
    private MailProvider mail;
    private SendProvider send;

    private Session sendSession;
    private Store store;

    private Transport transport;

    private List<Folder> openFolders;

    public Mailbox(MailProvider mail, SendProvider send) {
        if (mail == null && send == null)
            throw new IllegalArgumentException("Must have either a mail or send provider");
        this.mail = mail;
        this.send = send;

        this.openFolders = new ArrayList<>();
    }

    public void connect() throws MessagingException {
        if (mail != null)
            store = mail.connect();
        if (store != null && store.isConnected())
            MailClient.LOG.fine("Mail connected");
    }

    public void close() throws MessagingException {
        for (Folder f : openFolders)
            f.close(true);
        if (store != null)
            store.close();
        if (transport != null)
            transport.close();
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
    public void closeFolder(Folder f) {
        try {
            openFolders.remove(f);
            f.close(true);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
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
            if (!openFolders.contains(folder))
                openFolders.add(folder);

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
            if (send != null && (transport == null || sendSession == null)) {
                transport = send.connect();
                sendSession = send.getSession();
                if (transport != null & transport.isConnected())
                    MailClient.LOG.fine("Send connected");
            }

            Message msg = email.prepare(sendSession);
            MailClient.LOG.info("Sending email: " + msg.getSubject());
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
    }
}

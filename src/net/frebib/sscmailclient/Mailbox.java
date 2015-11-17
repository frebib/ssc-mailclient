package net.frebib.sscmailclient;

import net.frebib.util.task.Progress;
import net.frebib.util.task.Worker;

import javax.mail.*;
import javax.mail.search.BodyTerm;
import javax.mail.search.HeaderTerm;
import javax.mail.search.OrTerm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

public class Mailbox extends Observable {
    private MailProvider mail;
    private SendProvider send;

    private Session sendSession;
    private Store store;

    private Transport transport;

    private List<Folder> folders;
    private Folder current;

    public Mailbox(MailProvider mail, SendProvider send) {
        if (mail == null && send == null)
            throw new IllegalArgumentException("Must have either a mail or send provider");
        this.mail = mail;
        this.send = send;

        this.folders = new ArrayList<>();
    }

    public void connect() throws Exception {
        if (mail != null)
            store = mail.connect();
        if (store != null && store.isConnected())
            MailClient.LOG.fine("Mail connected");

        MailClient.LOG.finer("Loading default folder");
        folders = Arrays.asList(store.getDefaultFolder().list());
    }

    public void close() throws MessagingException {
        for (Folder f : folders)
            if (f.isOpen())
                f.close(true);
        if (store != null)
            store.close();
        if (transport != null)
            transport.close();
    }

    public Folder getCurrent() {
        return current;
    }
    public Folder getFolder(String path) {
        try {
            MailClient.LOG.finer("Loading folder \"" + path + "\"");
            for (Folder f : folders)
                if (f.getName().toLowerCase().equals(path.toLowerCase())) {
                    System.out.println(path.toLowerCase());
                    current = f;
                    break;
                }
            return current;
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }
    public void closeFolder(Folder f) {
        try {
            f.close(true);
            current = null;
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
    }

    public void reloadFolder(Folder f) {
        String name = f.getFullName();
        closeFolder(f);
        fetchMessages(name, null);
    }

    public void fetchMessages(String path, Progress p) {
        fetchMessages(getFolder(path), p);
    }
    public void fetchMessages(Progress p) {
        fetchMessages(getCurrent(), p);
    }
    public void fetchMessages(Folder f, Progress p) {
        try {
            if (!f.isOpen())
                f.open(Folder.READ_WRITE);
            fetchMessages(f.getMessages(), p);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
    }
    public void fetchMessages(Message[] mails, Progress p) {
        setChanged();
        for (int i = 0; i < mails.length; i++) {
            setChanged();
            notifyObservers(new Email(mails[i]));

            if (p != null)
                p.onProgress(i + 1, mails.length);
        }
    }

    public Message[] getMessages(String path) {
        return getMessages(getFolder(path));
    }
    public Message[] getMessages(Folder folder) {
        try {
            if (!folder.isOpen())
                folder.open(Folder.READ_WRITE);
            return folder.getMessages();
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }
    public Email[] getEmails(Folder folder) {
        return convertToEmail(getMessages(folder), null);
    }

    public Email[] convertToEmail(Message[] msgs, Progress p) {
        Email[] emails = new Email[msgs.length];
        for (int i = 0; i < msgs.length; i++) {
            emails[i] = new Email(msgs[i]);
            if (p != null)
                p.onProgress(i, emails.length);
        }
            return emails;
    }

    public List<Folder> getFolderList() {
        try {
            return folders;
        } catch (Exception e) {
            MailClient.LOG.exception(e);
        }
        return null;
    }

    public void searchIn(Folder folder, String term) {
        try {
            Email[] mail;
            if (term.isEmpty())
                mail = getEmails(folder);
            else
                mail = convertToEmail(folder.search(new OrTerm(
                        new BodyTerm(term), new HeaderTerm("*", term))), null);
            setChanged();
            notifyObservers(mail);
        } catch (MessagingException e) {
            MailClient.LOG.exception(e);
        }
    }

    public void send(UnsentEmail email) {
        new Worker<>()
            .todo((d, p) -> {
                if (send != null && (transport == null || sendSession == null)) {
                    transport = send.connect();
                    sendSession = send.getSession();
                    if (transport != null & transport.isConnected())
                        MailClient.LOG.fine("Send connected");

                    Message msg = email.prepare(sendSession);
                    MailClient.LOG.info("Sending email: " + msg.getSubject());
                    transport.sendMessage(msg, msg.getAllRecipients());
                }
            }).error(MailClient.LOG::exception)
            .start();
    }
}

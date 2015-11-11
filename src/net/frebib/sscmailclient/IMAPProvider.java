package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.Properties;

public class IMAPProvider implements MailProvider {
    private Properties props;
    private Session session;
    private Store store;

    public IMAPProvider(Properties props) throws IllegalArgumentException {
        if (props == null)
            throw new IllegalArgumentException("Properties cannot be null");

        props.setProperty("mail.store.protocol", "imaps");
        this.props = props;
        this.session = Session.getInstance(props);
        try {
            this.store = session.getStore("imaps");
        } catch (NoSuchProviderException e) {
            MailClient.log.exception(e);
        }
    }

    @Override
    public void connect() throws MessagingException {
        store.connect(props.getProperty("mail.host"),
                      props.getProperty("mail.user"),
                      props.getProperty("mail.password")
        );
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Folder getFolder(String path) throws MessagingException {
        return store.getFolder(path);
    }

    @Override
    public Message[] getMessages(String path) throws MessagingException {
        Folder folder = getFolder(path);
        if(!folder.isOpen())
            folder.open(Folder.READ_WRITE);
        return folder.getMessages();
    }

    @Override
    public Folder[] getFolderList() throws MessagingException {
        return store.getDefaultFolder().list();
    }
}

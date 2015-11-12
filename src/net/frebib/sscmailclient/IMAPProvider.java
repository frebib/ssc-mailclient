package net.frebib.sscmailclient;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class IMAPProvider implements MailProvider {
    private final Properties props;
    private final Session session;
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
            MailClient.LOG.exception(e);
        }
    }

    @Override
    public Store connect() throws MessagingException {
        if (!store.isConnected())
            store.connect(props.getProperty("mail.host"),
                          props.getProperty("mail.user"),
                          props.getProperty("mail.password")
            );
        return store;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Store getStore() {
        return store;
    }
}

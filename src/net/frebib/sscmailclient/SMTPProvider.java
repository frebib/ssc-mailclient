package net.frebib.sscmailclient;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

public class SMTPProvider implements SendProvider {
    private final Properties props;
    private final Session session;
    private Transport transport;

    public SMTPProvider(Properties props) {
        if (props == null)
            throw new IllegalArgumentException("Properties cannot be null");

        props.setProperty("mail.smtp.port", "587");
        this.props = props;
        this.session = Session.getInstance(props);
        try {
            this.transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            MailClient.LOG.exception(e);
        }
    }

    @Override
    public Transport connect() throws MessagingException {
        if (!transport.isConnected())
            transport.connect(props.getProperty("mail.smtp.host"),
                              props.getProperty("mail.user"),
                              props.getProperty("mail.password")
            );
        return transport;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Transport getTransport() {
        return transport;
    }
}

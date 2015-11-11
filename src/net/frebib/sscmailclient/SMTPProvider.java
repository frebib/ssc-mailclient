package net.frebib.sscmailclient;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class SMTPProvider implements SendProvider {
    @Override
    public Store connect() throws MessagingException {
        return null;
    }

    @Override
    public Session getSession() {
        return null;
    }

    @Override
    public Store getStore() {
        return null;
    }
}

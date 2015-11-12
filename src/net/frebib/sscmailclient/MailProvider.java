package net.frebib.sscmailclient;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public interface MailProvider {
    Store connect() throws MessagingException;
    Session getSession();
    Store getStore();
}

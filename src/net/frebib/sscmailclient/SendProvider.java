package net.frebib.sscmailclient;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

public interface SendProvider {
    Transport connect() throws MessagingException;
    Session getSession();
    Transport getTransport();
}

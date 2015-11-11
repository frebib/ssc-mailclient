package net.frebib.sscmailclient;

import javax.mail.*;
import java.util.Properties;

public interface MailProvider {
    Store connect() throws MessagingException;
    Session getSession();
    Store getStore();
}

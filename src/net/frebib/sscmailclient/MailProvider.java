package net.frebib.sscmailclient;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.Properties;

public interface MailProvider {
    Session getSession();
    void connect() throws MessagingException;
    Folder[] getFolderList() throws MessagingException;
    Folder getFolder(String path) throws MessagingException;
    Message[] getMessages(String path) throws MessagingException;
}

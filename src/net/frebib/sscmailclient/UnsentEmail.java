package net.frebib.sscmailclient;

import javax.mail.Address;
import javax.mail.Message;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UnsentEmail {
    private Address[] to, cc, bcc;
    private String subject, body;
    private List<File> attachments;

    public UnsentEmail(Address[] to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachments = new ArrayList<>();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<File> attachments) {
        this.attachments = attachments;
    }

    public Message prepare() {
        return null;
    }

    public Address[] getRecipients() {
        return null;
    }
}

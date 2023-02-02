package com.application.mypetandroid.utils.email;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail extends AsyncTask<Void, Void, Void> {

    private static final Logger logger = Logger.getLogger(SendMail.class);

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final String recipient;
    private final String message;
    private final String subject;
    private final String successMessage;
    private final LoadingDialogBar loadingDialogBar;
    private final SystemEmailConfig systemEmailConfig;

    public SendMail(Context context, String recipient, String subject, String message, String successMessage) {
        this.context = context;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.successMessage = successMessage;
        systemEmailConfig = new SystemEmailConfig();
        this.loadingDialogBar = new LoadingDialogBar(context);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.loadingDialogBar.showDialog();
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.loadingDialogBar.hideDialog();
        Toast.makeText(this.context, this.successMessage, Toast.LENGTH_SHORT).show();

    }

    protected Void doInBackground(Void... params) {

        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");

        //Create a session with account credentials
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(systemEmailConfig.getEmail(), systemEmailConfig.getWatchword());
            }
        });

        //Prepare email message
        MimeMessage mimeMessage = prepareMessage(session);

        //Send mail
        assert mimeMessage != null;
        try {
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Messaging Error: ", e);
            return null;
        }
        return null;
    }

    private MimeMessage prepareMessage(Session session) {
        try {
            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress(systemEmailConfig.getEmail()));
            mm.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mm.setSubject(subject);
            mm.setText(message);
            return mm;
        } catch (MessagingException e) {
            logger.error("Messaging Error: ", e);
        }
        return null;
    }

}

package com.application.mypet.utils.email;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.application.mypet.utils.LoadingDialogBar;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final String email;
    private final LoadingDialogBar loadingDialogBar;
    private final String message;
    private final String subject;
    private final String successMessage;
    /* access modifiers changed from: private */
    public final SystemEmailConfig systemEmailConfig = new SystemEmailConfig();

    public SendMail(Context context2, String email2, String subject2, String message2, String successMessage2) {
        this.context = context2;
        this.email = email2;
        this.subject = subject2;
        this.message = message2;
        this.successMessage = successMessage2;
        this.loadingDialogBar = new LoadingDialogBar(context2);
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        super.onPreExecute();
        this.loadingDialogBar.showDialog();
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.loadingDialogBar.hideDialog();
        Toast.makeText(this.context, this.successMessage, 1).show();
    }

    /* access modifiers changed from: protected */
    public Void doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.checkserveridentity", true);
        try {
            MimeMessage mm = new MimeMessage(Session.getDefaultInstance(props, new Authenticator() {
                /* access modifiers changed from: protected */
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SendMail.this.systemEmailConfig.getEmail(), SendMail.this.systemEmailConfig.getWatchword());
                }
            }));
            mm.setFrom(new InternetAddress(this.systemEmailConfig.getEmail()));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(this.email));
            mm.setSubject(this.subject);
            mm.setText(this.message);
            Transport.send(mm);
            return null;
        } catch (MessagingException e) {
            Log.e("Messaging Error: ", e.getMessage());
            return null;
        }
    }
}

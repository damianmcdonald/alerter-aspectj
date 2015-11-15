package com.github.damianmcdonald.alerter.mail;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.github.damianmcdonald.alerter.domain.PointCutInfo;
import com.github.damianmcdonald.alerter.templating.TemplateType;
import com.github.damianmcdonald.alerter.templating.VelocityTemplater;
import com.github.damianmcdonald.alerter.utils.BeanUtils;
import com.github.damianmcdonald.alerter.utils.PropertiesFactory;

public class Mailer {

  private final static Logger logger = Logger.getLogger(Mailer.class.getName());
  private final VelocityTemplater velocityTemplater = new VelocityTemplater();
  private final Properties props = PropertiesFactory.getInstance().getProperties();
  private final boolean sendMail = Boolean.parseBoolean(props.getProperty("mail.send"));
  private final String from = props.getProperty("mail.from");
  private final String host = props.getProperty("mail.host");
  private final String port = props.getProperty("mail.port");
  private final String recipients = props.getProperty("mail.recipients");
  private final boolean requiresAuth = Boolean.parseBoolean(props.getProperty("mail.requires.auth"));
  private final boolean enableTLS = Boolean.parseBoolean(props.getProperty("mail.enable.tls"));
  private final String username = props.getProperty("mail.username");
  private final String password = props.getProperty("mail.password");
  private final boolean useHtmlTemplate = Boolean.parseBoolean(props.getProperty("mail.template.html"));

  public Mailer() {
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);
    if (requiresAuth) {
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", enableTLS);
    }
  }

  private Session createSession() {
    if (requiresAuth) {
      return Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });
    } else {
      return Session.getDefaultInstance(props);
    }
  }

  public void sendAlertMail(final PointCutInfo info) throws UnsupportedEncodingException, AddressException, IllegalArgumentException, IllegalAccessException {
    final Session session = createSession();
    final InternetAddress[] recipientAddresses = InternetAddress.parse(recipients);
    final String subject = MessageFormat.format((String) props.getProperty("mail.subject"), info.getAlertSignature());
    try {
      final MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from));
      message.addRecipients(Message.RecipientType.TO, recipientAddresses);
      message.setSubject(subject);
      if (useHtmlTemplate) {
        message.setContent(velocityTemplater.generateTemplate(info, TemplateType.HTML), "text/html; charset=utf-8");
      } else {
        message.setText(velocityTemplater.generateTemplate(info, TemplateType.PLAINTEXT));
      }
      logger.info("Sending alert email to: " + recipients);
      logger.trace("Email message configuration dump: ");
      logger.trace(BeanUtils.recursiveDescribe(message));
      if(sendMail) Transport.send(message);
    } catch (MessagingException mex) {
      mex.printStackTrace();
    }
  }
}

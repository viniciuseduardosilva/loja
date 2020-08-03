package br.com.vinicius.service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender sender;

	@Autowired
	private Configuration config;

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	public void novaSenha(String email, String token) {
		try {
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Template t = config.getTemplate("recupera-template.ftl");
			Map<String, Object> model = new HashMap<>();
			model.put("Name", email);
			model.put("confirmationUrl", "localhost:8080/api/usuario/nova-senha/" + token);
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
			helper.setTo(email);
			helper.setText(html, true);
			helper.setSubject("Ola");
			helper.setFrom("devhibrido.software@gmail.com");
			sender.send(message);
		} catch (Exception e) {
			logger.info(e.toString());
		}
	}

	public void sendEmail(String email, String token) {
		try {
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			// add attachment
//			helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
			Template t = config.getTemplate("email-template.ftl");
//			Map<String, Object> url = new HashMap<String, Object>();
			Map<String, Object> model = new HashMap<>();
			model.put("Name", email);
			model.put("confirmationUrl", "localhost:8080/api/usuario/confirmacao/" + token);
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
			helper.setTo(email);
			helper.setText(html, true);
			helper.setSubject("Ola");
			helper.setFrom("devhibrido.software@gmail.com");
			sender.send(message);
		} catch (Exception e) {
			logger.info(e.toString());
		}
	}
}

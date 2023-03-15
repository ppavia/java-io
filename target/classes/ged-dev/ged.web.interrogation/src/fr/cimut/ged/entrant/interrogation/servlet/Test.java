/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.interrogation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.mail.MailerHelper;

public class Test extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Test() {
		super();
	}

	/**
	 * test de health check - avec aussi potentiellement test de verification d'envoi de mail et test d'intégration de
	 * mail entrant
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try {

			// check envoi email multiccanal
			testSendMail(request.getParameter("mail"));

			// cette servlet est appelé par cron - pour vérifier que la gede est fonctionnelle et notamment le pooler de mail
			int status = 200;
			String message = "OK";

			if (StringUtils.isNotBlank(request.getParameter("checkMailEntrant"))) {
				long dateNow = new Date().getTime();
				// timeMaxAuthorized = 1 heure
				long timeMaxAuthorized = 1L * 60 * 60 * 1000;
				long dureeIntgerationDernierMail = dateNow - MailerHelper.dateDerniereIntegrationMail;
				if (dureeIntgerationDernierMail > timeMaxAuthorized) {
					logger.fatal("Aucune intégration de mail sur la dernière heure (" + timeMaxAuthorized
							+ " ms) - Pooler de mail KO ? derniere intégration " + dureeIntgerationDernierMail + " ms");
					status = 500;
					message = "KO : derniere intégration Mail il y a " + dureeIntgerationDernierMail + " ms";
				} else {
					message = "OK : derniere intégration Mail il y a " + dureeIntgerationDernierMail + " ms";
					logger.info(message);

				}
			}

			response.setStatus(status);
			response.setHeader("Content-Type", "text/html;charset=iso-8859-1");
			response.setHeader("Cache-Control", "no-cache");
			writer.write(message);

			writer.flush();
		} catch (Exception e) {
			logger.fatal("commentaire manquant", e);
		} finally {
			logger.debug("Servlet d'accroche OK");
			writer.close();
		}
	}

	/**
	 * test email
	 * 
	 * @param email
	 * @throws CimutMailException
	 */
	private void testSendMail(String email) throws CimutMailException {

		if (StringUtils.isNotBlank(email)) {
			List<String> dest = new ArrayList<String>();
			dest.add(email);
			MailerHelper.sendMail("9970", dest, "test email", "test email", " test de vie");
		}
	}

}
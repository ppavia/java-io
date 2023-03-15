package fr.cimut.ged.entrant.integration.ejb;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.service.CompteMailFileService;
import fr.cimut.ged.entrant.service.MailIntegrationService;
import fr.cimut.ged.entrant.utils.GlobalVariable;

/**
 * Session Bean implementation class Pooler
 */
@TransactionAttribute(value = TransactionAttributeType.NEVER)
@Singleton(mappedName = "PoolerMail")
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PoolerMail implements TimedObject {

	private static final Logger LOGGER = Logger.getLogger(PoolerMail.class);

	@EJB
	CompteMailFileService compteMailFileService;

	@EJB
	MailIntegrationService mailIntegrationService;

	@Resource
	TimerService timerService;

	// interval de pooling par default 5 minutes
	private static final long DEFAULT_INTERVAL = 300000L;

	// max mail default value
	private static final long DEFAULT_MAXMAIL = 10L;

	// timeout pour la method checkMail par default : 30 minutes
	private static final long DEFAULT_TIMEOUT = 1800000L;

	/**
	 * Default constructor.
	 */
	public PoolerMail() {
	}

	@PostConstruct
	public void creerTimer() {
		TimerConfig tc = new TimerConfig();
		tc.setPersistent(false);
		// start within one minute
		timerService.createSingleActionTimer(60000l, tc);
	}

	@Override
	public void ejbTimeout(Timer arg0) {

		// default timeout/ interval, don' t break all for this ...
		Long timeout;
		Long interval;
		try {
			timeout = GlobalVariable.getMailPoolerTimeout();
		} catch (CimutConfException e) {
			timeout = DEFAULT_TIMEOUT;
			LOGGER.fatal("Unable to get the timeout for the mail pooler, default is : " + timeout + " ms");

		}

		try {
			interval = GlobalVariable.getMailPoolerInterval();
		} catch (CimutConfException e) {
			interval = DEFAULT_INTERVAL;
			LOGGER.fatal("Unable to get the interval time for the mail pooler, default is : " + interval + " ms");

		}

		ExecutorService executor = Executors.newCachedThreadPool();

		Future<Void> future = executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				checkAllMailBoxes();
				return null;
			}
		});

		try {
			// kill the process at timeout.
			future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOGGER.warn("Pooler mail interrupted", e);
		} catch (ExecutionException e) {
			LOGGER.warn("Pooler mail interrupted", e);
		} catch (TimeoutException e) {
			LOGGER.warn("Pooler mail interrupted", e);
		} finally {
			if (!future.isDone() && !future.isCancelled() && !future.cancel(true)) {
				// la, cela devient compliqué ...
				LOGGER.warn("Unable to kill the mail pooler after the following timeout : " + timeout + " ms");
			}
			LOGGER.debug("relance le process check boite mail dans ; " + interval + " ms");
			timerService.createSingleActionTimer(interval, new TimerConfig());
		}
	}

	/**
	 * Vérifie toutes les boîtes mail, consulte et intègre tous les mails
	 * 
	 * @throws InterruptedException
	 */
	public void checkAllMailBoxes() throws InterruptedException {
		if (GlobalVariable.checkIfMasterForScheduledTask()) {
			LOGGER.debug("Traitement des emails");
			long maxEmail;
			try {
				maxEmail = GlobalVariable.getMailPoolerMaxEmail();
			} catch (CimutConfException e) {
				maxEmail = DEFAULT_MAXMAIL;
				LOGGER.fatal("Erreur lors de la recuperation du nombre max de mail a traiter par iteration, par defaut : " + maxEmail, e);
			}
			try {
				List<CompteMail> listCompte = compteMailFileService.getAllComptesMailFromFiles();

				// boucle sur les boites mails
				for (CompteMail compteMail : listCompte) {
					mailIntegrationService.checkMailBox(compteMail, maxEmail);
				}
			} catch (CimutConfException e) {
				LOGGER.fatal("Erreur lors de la recuperation des informations liees aux comptes email ", e);
			}
		}
	}

	@Schedule(hour = "8", dayOfWeek = "Mon-Fri", persistent = false)
	@Lock(LockType.WRITE)
	public void sendErrorsByEmail() {
		if (GlobalVariable.checkIfMasterForScheduledTask()) {
			try {
				List<CompteMail> listCompte = compteMailFileService.getAllComptesMailFromFiles();
				// boucle sur les boites mails
				for (CompteMail compteMail : listCompte) {
					mailIntegrationService.sendIntegrationErrorsByEmail(compteMail);
				}
			} catch (CimutConfException e) {
				LOGGER.fatal("Erreur lors de la recuperation des informations liees aux comptes email ", e);
			}
		}
	}
}
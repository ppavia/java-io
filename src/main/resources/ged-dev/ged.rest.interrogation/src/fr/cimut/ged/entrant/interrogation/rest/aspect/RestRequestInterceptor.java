package fr.cimut.ged.entrant.interrogation.rest.aspect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.cimut.ged.entrant.beans.UploadFileRequest;
import fr.cimut.ged.entrant.exceptions.AccessForbiddenException;
import fr.cimut.ged.entrant.exceptions.GedeError;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.EndpointAbstract;
import fr.cimut.ged.entrant.utils.SerialisationUtils;

/**
 * Interception des requetes envoyés au serveur. <br/>
 * Gestion des logs et mesure des temps de réponses <br/>
 * Vérification de la validité de session <br/>
 * Gestion de l'autorisation d'un utilisateur a effectuer une opération <br />
 * Gestion uniforme des exceptions métier
 * 
 * @author jlebourgocq
 *
 */
@Stateless
public class RestRequestInterceptor {

	private Logger logger = Logger.getLogger(this.getClass());

	@AroundInvoke
	public Object interceptCall(InvocationContext ctx) throws Exception {
		Object result = null;
		long timeBegin = new Date().getTime();
		final String signatureName = ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName();
		// par défaut pas d'exception
		final StringBuilder exceptionMessage = new StringBuilder();
		boolean isExecutionOk = true;

		try {
			// JLB check authorization
			Object[] paramsInput = ctx.getParameters();
			Object authKey = null;
			if (null != paramsInput && paramsInput.length > 0) {
				authKey = paramsInput[0];
			}
			if (!EndpointAbstract.AUTH_KEY.equals(authKey)) {
				throw new AccessForbiddenException();
			}

			//lancement du traitement nominal
			result = ctx.proceed();

		} catch (Exception e) {
			isExecutionOk = false;
			return dealWithExceptionAndThrowError(e, exceptionMessage, ctx);
		} finally {

			long timeEnd = new Date().getTime();
			long elapsedTime = timeEnd - timeBegin;
			final String resultText = (isExecutionOk ? "OK" : exceptionMessage.toString());
			String messageLog = "#INDICATEUR REST#" + signatureName + "#" + elapsedTime + "#" + resultText + "#";
			logger.info(messageLog);

		}
		return result;

	}

	/**
	 * gestion centralisée des exceptions
	 * 
	 * @param e
	 *            l'exception jetée
	 * @param exceptionMessage
	 *            a valoriser pour les indicateurs
	 * @param ctx
	 * @return
	 */
	private Response dealWithExceptionAndThrowError(Exception e, StringBuilder exceptionMessage, InvocationContext ctx) {

		String logError = "";
		String fonctionnalError = "";
		int httpCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
		GedeError errorObject = null;

		StringWriter errorsStackTrace = new StringWriter();

		if (e instanceof GedeException) {
			GedeException gedeException = (GedeException) e;
			fonctionnalError = gedeException.getMessage();
			exceptionMessage.append(gedeException.getClass().getCanonicalName());
			exceptionMessage.append(" - ");
			exceptionMessage.append(fonctionnalError);

			httpCode = gedeException.getHttpErrorCode();

			// TODO JLB : ajouter le full log erreur
			logError = getStackTrace(gedeException);
			errorObject = gedeException.toGedeError();

		} else {
			exceptionMessage.append("exception inconnue " + e.getMessage());
			errorsStackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(errorsStackTrace));
			logError = "exception inconnue : " + e.getMessage();
			logError = "\n " + errorsStackTrace.toString();
			fonctionnalError = logError;
			httpCode = 500;

			errorObject = new GedeError();
			errorObject.setCode("UNKNOWN");
			errorObject.setMessage(e.getMessage());
		}

		// on ajoute les parametres de la requete d'input au log d'erreur
		// en cas d'erreur on loggue la requête d'input
		// : log des informations de la requete input
		String requestParams = getStringParamsFromCtxt(ctx);
		logError += "request Input : " + requestParams + "\n";

		logger.error(logError);
		// Pas besoin de toutes la trace dans la réponse faite à starwebWS en cas d'erreur
		// errorObject.setTechnicalDetail(logError);
		return Response.status(httpCode).entity(errorObject).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

	}

	private String getStringParamsFromCtxt(InvocationContext ctx) {
		List<String> params = new ArrayList<String>();
		// on fait attention a pas logguer les infos sensibles logins mot de passe....
		if (null != ctx) {
			Object[] methodArguments = ctx.getParameters();
			if (ArrayUtils.isNotEmpty(methodArguments)) {
				for (Object obj : methodArguments) {
					if (null != obj) {
						String objSt = null;
						if (obj instanceof UploadFileRequest) {
							objSt = "-ici git le comptenu du zip-";
						} else {
							try {
								objSt = SerialisationUtils.writeValueAsString(obj);
							} catch (JsonProcessingException e) {
								logger.error("erreur serialisation : " + objSt, e);
								objSt = obj.toString();
							}
						}
						params.add(objSt);
					}
				}
			}
		}

		return "[" + StringUtils.join(params, "][") + "]";

	}

	/**
	 * retourne un message détaillé de l'erreur
	 * 
	 * @param gedeException
	 * 
	 * @return
	 */
	public String getStackTrace(GedeException gedeException) {
		StringWriter errorsStackTrace = new StringWriter();

		gedeException.printStackTrace(new PrintWriter(errorsStackTrace));
		String logError = "\n " + errorsStackTrace.toString();
		if (null != gedeException.getCause()) {
			errorsStackTrace = new StringWriter();
			logError += "\n exception initiale " + gedeException.getCause().getMessage();
			gedeException.getCause().printStackTrace(new PrintWriter(errorsStackTrace));
			logError += "\n " + errorsStackTrace.toString();
		}

		return logError;
	}

}
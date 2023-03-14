//package fr.cimut.ged.entrant.service.aspect;
//
//import java.util.Date;
//
//import javax.ejb.Stateless;
//import javax.interceptor.AroundInvoke;
//import javax.interceptor.InvocationContext;
//
//import org.apache.log4j.Logger;
//
///**
// * Interception des requetes aux différents service mis en place pour du debuggage
// * 
// * @author jlebourgocq
// *
// */
//@Stateless
//public class DebugServiceInterceptor {
//
//	private Logger logger = Logger.getLogger(this.getClass());
//
//	@AroundInvoke
//	public Object interceptCall(InvocationContext ctx) throws Throwable {
//		Object result = null;
//		long timeBegin = new Date().getTime();
//		final String signatureName = ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName();
//		// par défaut pas d'exception
//		boolean isExecutionOk = true;
//
//		try {
//
//			//lancement du traitement nominal
//			result = ctx.proceed();
//
//		} catch (Throwable t) {
//			isExecutionOk = false;
//		} finally {
//
//			long timeEnd = new Date().getTime();
//			long elapsedTime = timeEnd - timeBegin;
//			final String resultText = (isExecutionOk ? "OK" : "KO");
//			String messageLog = "#INDICATEUR SERVICE#" + "#" + signatureName + "#" + elapsedTime + "#" + resultText + "#";
//			logger.info(messageLog);
//
//		}
//		return result;
//
//	}
//
//}
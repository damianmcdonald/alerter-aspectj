package com.github.damianmcdonald.alerter.aspects;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

import com.github.damianmcdonald.alerter.domain.PointCutInfo;
import com.github.damianmcdonald.alerter.mail.Mailer;
import com.github.damianmcdonald.alerter.utils.BeanUtils;

@Aspect
public abstract class AbstractAlertAspect {
  
    private final static Logger logger = Logger.getLogger(Mailer.class.getName());
    
    @Pointcut
    abstract void alertMethod();

    @Around("alertMethod()")
    public Object around(final ProceedingJoinPoint jp) throws Throwable {
      logger.trace("Aspect invoked on " + jp.getSignature().toString());
      Object objToReturn = null;
      try {
        objToReturn = jp.proceed();
      } catch (Exception ex) {
        ex.printStackTrace();
        logger.error("Failure in: " + jp.getSignature().toString());
        final String[] paramNames = ((CodeSignature) jp.getStaticPart().getSignature()).getParameterNames();
        final PointCutInfo info = new PointCutInfo(
                                        jp.getSignature().getDeclaringType().toString(), 
                                        jp.getSignature().getName(), 
                                        jp.getSignature().toString(), 
                                        paramNames, 
                                        jp.getArgs()
                                        );
        logger.error("Attribute dump of failure: ");
        logger.error(BeanUtils.recursiveDescribe(info));
        logger.info("Sending alert email for failure in: " + jp.getSignature().toString());
        Mailer mailer = new Mailer();
        mailer.sendAlertMail(info);
      }
      return objToReturn;
    }
}

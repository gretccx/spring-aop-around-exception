package com.luv2code.aopdemo.aspect;

import java.util.List;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.luv2code.aopdemo.Account;

@Aspect
@Component
@Order(2)
public class MyDemoLoggingAspect {
	
	private Logger myLogger = Logger.getLogger(getClass().getName());
	
	@Around("execution(* com.luv2code.aopdemo.service.*.getFortune(..))")
	public Object aroundGetFortune(
			ProceedingJoinPoint theProceedingJoinPoint) throws Throwable {
		
		// print out which method we are advising on
		String method = theProceedingJoinPoint.getSignature().toShortString();
		myLogger.info("\n====>>>> Executing @Around on method: " + method);
		
		// get begin time stamp (in milliseconds)
		long start = System.currentTimeMillis();
		
		// execute the method
		Object result = null;
		
		try {
			result = theProceedingJoinPoint.proceed();
		} catch (Exception e) {
			
			// log the exception
			myLogger.warning(e.getMessage());
			
			// rethrow the exception
			throw e;
		}
		
		// get end time stamp
		long end = System.currentTimeMillis();
		
		// compute the duration and display
		long duration = end - start;
		myLogger.info("\n====>>>> Duration: " + duration / 1000.0 + " seconds");
		
		// return the result of the method call to the calling program
		return result;
	}
	
	@After("execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))")
	public void afterFinallyFindAccountsAdvice(JoinPoint theJoinPoint) {
		
		// print out which method we are advising on
		String method = theJoinPoint.getSignature().toShortString();
		myLogger.info("\n====>>>> Executing @After (finally) on method: " + method);	
	}
	
	@AfterThrowing(
			pointcut="execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
			throwing="theExc")
	public void afterThrowingFindAccountsAdvice(
			JoinPoint theJoinPoint, Throwable theExc) {
		
		// print out which method we are advising on
		String method = theJoinPoint.getSignature().toShortString();
		myLogger.info("\n====>>>> Executing @AfterThrowing on method: " + method);
		
		// log the exception
		myLogger.info("exception is: " + theExc);
	}

	// add a new advice for @AfterReturning on the findAccounts method
	@AfterReturning(
			pointcut="execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
			returning="result")
	public void afterReturningFindAccountsAdvice(
			JoinPoint theJoinPoint, List<Account> result) {
		
		// print out which method we are advising on
		String method = theJoinPoint.getSignature().toShortString();
		myLogger.info("\n====>>>> Executing @AfterReturning on method: " + method);
		
		// print out the results of the method call
		myLogger.info("result is: " + result);
		
		// post-process the data .. modify them
		// convert the account names to upper case and print
		convertAccountNamesToUpperCase(result);
		myLogger.info("result is: " + result);
	}
	
	private void convertAccountNamesToUpperCase(List<Account> result) {
		
		// loop thru accounts
		for (Account tempAccount : result) {
			
			// get upper case version of name
			String upperName = tempAccount.getName().toUpperCase();
			
			// update the name on the account obj
			tempAccount.setName(upperName);
		}		
	}

	@Before("com.luv2code.aopdemo.aspect.LuvAopExpressions.forDaoPackageNoGetterSetter()")
	public void beforeAddAccountAdvice(JoinPoint theJoinPoint) {
		myLogger.info("\n====>>>> Executing @Before advice on addAccount()");
		
		// display the method signature
		MethodSignature methodSig = (MethodSignature) theJoinPoint.getSignature();
		myLogger.info("Method: " + methodSig);
		
		// display method arguments
		// get args
		Object[] args = theJoinPoint.getArgs();
		
		// loop thru args
		for (Object tempArg : args) {
			myLogger.info(tempArg.toString());
			
			if (tempArg instanceof Account) {
				
				// downcast and print Account specific data
				Account theAccount = (Account) tempArg;
				myLogger.info("account name: " + theAccount.getName());
				myLogger.info("account level: " + theAccount.getLevel());
			}
		}
	}

}

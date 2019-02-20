package com.luv2code.aopdemo.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyDemoLoggingAspect {

	// this is where we add all of our related advices for logging
	
	// let's start with @Before advice
	@Before("execution(* add*())")
	public void beforeAddAccountAdvice() {
		
		// add our custom code
		System.out.println("\n====>>>> Executing @Before advice on addAccount()");
	}
}

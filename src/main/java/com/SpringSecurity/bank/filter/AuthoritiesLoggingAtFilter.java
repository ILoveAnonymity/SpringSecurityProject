package com.SpringSecurity.bank.filter;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class AuthoritiesLoggingAtFilter implements Filter {

	public final Logger logger = Logger.getLogger( AuthoritiesLoggingAtFilter.class.toString() );

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logger.info( "auth validation is in progress" );
		chain.doFilter( request, response );
	}

}

package com.SpringSecurity.bank.filter;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.SpringSecurity.bank.model.Authoritiy;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class AuthoritiesLoggingAfterFilter implements Filter {

	public final Logger logger = Logger.getLogger( AuthoritiesLoggingAfterFilter.class.getName() );

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth != null ) {
			logger.info( "User " + auth.getName() + " is successfully authenticated and " + "has the authorities: "
					+ auth.getAuthorities().toString() );
		}
		
		chain.doFilter( request, response );

	}

}

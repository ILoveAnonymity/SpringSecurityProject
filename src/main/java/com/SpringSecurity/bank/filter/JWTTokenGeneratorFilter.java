package com.SpringSecurity.bank.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.SpringSecurity.bank.constants.SecurityConstants;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			SecretKey key = Keys.hmacShaKeyFor( SecurityConstants.JWT_KEY.getBytes( StandardCharsets.UTF_8 ) );
			String jwt = Jwts.builder().setIssuer( "EasyBank" ).setSubject( "Jwt Token" )
					.claim( "username", authentication.getName() )
					.claim( "authorities", receiveAuthoritiesAsString( authentication.getAuthorities() ) )
					.setIssuedAt( new Date() )
					.setExpiration( new Date( new Date().getTime() + 3000000) )
					.signWith( key ).compact();
			response.setHeader( SecurityConstants.JWT_HEADER, jwt );
		}
		filterChain.doFilter( request, response );
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {		
		return !request.getServletPath().equals( "/user" );
	}

	private String receiveAuthoritiesAsString(Collection<? extends GrantedAuthority> authorities) {
		List<String> authority = new ArrayList<>();
		for ( GrantedAuthority grantedAuthority : authorities ) {
			authority.add( grantedAuthority.getAuthority());
		}
		return String.join( ",", authority );
	}

}

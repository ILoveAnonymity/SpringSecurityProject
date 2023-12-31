package com.SpringSecurity.bank.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.SpringSecurity.bank.constants.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String jwt = request.getHeader( SecurityConstants.JWT_HEADER );

		if ( jwt != null ) {
			try {
				SecretKey key = Keys.hmacShaKeyFor(
						SecurityConstants.JWT_KEY.getBytes( StandardCharsets.UTF_8 ) );
				Claims claim = Jwts.parserBuilder()
						.setSigningKey( key )
						.build()
						.parseClaimsJws( jwt )
						.getBody();
				String username = String.valueOf(claim.get("username"));
                String authorities = (String) claim.get("authorities");	
				Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
						AuthorityUtils.commaSeparatedStringToAuthorityList( authorities ));
				SecurityContextHolder.getContext().setAuthentication( auth );				
			} catch (Exception e) {
				throw new BadCredentialsException( "Invalid Credentials" );
			}
		}
		filterChain.doFilter( request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return request.getServletPath().equals( "/user" );
	}
	
}

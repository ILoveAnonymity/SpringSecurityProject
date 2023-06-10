package com.SpringSecurity.bank.Config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.SpringSecurity.bank.filter.AuthoritiesLoggingAfterFilter;
import com.SpringSecurity.bank.filter.AuthoritiesLoggingAtFilter;
import com.SpringSecurity.bank.filter.CsrfCookieFilter;
import com.SpringSecurity.bank.filter.JWTTokenGeneratorFilter;
import com.SpringSecurity.bank.filter.JWTTokenValidatorFilter;
import com.SpringSecurity.bank.filter.RequestValidationBeforeFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class ProjectSecurityConfig {

	@Bean
	SecurityFilterChain defaultDecurityFilterChain(HttpSecurity http) throws Exception {
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		requestHandler.setCsrfRequestAttributeName( "_csrf" );

		http.sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
//		.securityContext().requireExplicitSave( false ).and()
//				.sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.ALWAYS ) )
				.cors().configurationSource( new CorsConfigurationSource() {

					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration config = new CorsConfiguration();
						config.setAllowedOrigins( Collections.singletonList( "http://localhost:4200" ) );
						config.setAllowedMethods( Collections.singletonList( "*" ) );
						config.setAllowCredentials( true );
						config.setAllowedHeaders( Collections.singletonList( "*" ) );
						config.setExposedHeaders( Arrays.asList("Authorization") );
						config.setMaxAge( 3600L );
						return config;
					}
				} ).and()
				.csrf( (csrf) -> csrf.csrfTokenRequestHandler( requestHandler )
						.ignoringRequestMatchers( "/contact", "/register" )
						.csrfTokenRepository( CookieCsrfTokenRepository.withHttpOnlyFalse() ) )
				.addFilterAfter( new CsrfCookieFilter(), BasicAuthenticationFilter.class )
				.addFilterBefore( new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class )
				.addFilterAt( new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class )
				.addFilterAfter( new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class )
				.addFilterAfter( new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class )
				.addFilterBefore( new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
				.authorizeHttpRequests()
					.requestMatchers( "/myAccount" ).hasRole( "USER" )
					.requestMatchers( "/dashboard" ).hasRole( "USER" )
					.requestMatchers( "/myBalance" ).hasAnyRole( "USER", "ADMIN" )
					.requestMatchers( "/myLoans" ).hasRole( "USER" )
					.requestMatchers( "/myCards" ).hasRole( "MANAGER" )
					.requestMatchers( "/dashboard" ).hasRole( "USER" )
					.requestMatchers( "/user" ).hasRole( "USER" )
					.requestMatchers( "/notices", "/contact", "/register" ).permitAll()
				.and().formLogin().permitAll().and().httpBasic();
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

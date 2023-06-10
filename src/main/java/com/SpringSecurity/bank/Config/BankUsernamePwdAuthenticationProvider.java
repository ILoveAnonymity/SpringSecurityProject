package com.SpringSecurity.bank.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.SpringSecurity.bank.Repository.CustomerRepository;
import com.SpringSecurity.bank.model.Authoritiy;
import com.SpringSecurity.bank.model.Customer;

@Component
public class BankUsernamePwdAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	CustomerRepository customerRepositry;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		List<Customer> customerList = customerRepositry.findByEmail( name );
		Customer customer = null;

		if ( customerList.size() > 0 ) {
			if ( passwordEncoder.matches( pwd, customerList.get( 0 ).getPwd() ) ) {
				List<GrantedAuthority> authorities = getGrantedAuthorites(customerList.get( 0 ).getAuthorities());				
				return new UsernamePasswordAuthenticationToken( name, pwd, authorities );
			} else {
				throw new BadCredentialsException( "Invalid Password" );
			}

		} else {
			throw new BadCredentialsException( "No user registered with this details!" );
		}
	}

	private List<GrantedAuthority> getGrantedAuthorites(Set<Authoritiy> authorities){
		List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
		for ( Authoritiy authority: authorities) {
			grantedAuthorityList.add( new SimpleGrantedAuthority( authority.getName() ) );
			System.out.println(  authority.getName() );
		}
		return grantedAuthorityList;
	}
	
	@Override
	public boolean supports(Class<?> authentication) {  
		return ( UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication ) );
	}

}

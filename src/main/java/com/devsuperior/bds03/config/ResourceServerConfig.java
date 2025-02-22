package com.devsuperior.bds03.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer // configura a classexom o necessário para ser o ResourceServer do OAuth2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private Environment env;
	
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };
	
	private static final String[] OPERATOR_GET = { "/departments/**", "/employees/**" };
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	/* Com o método abaixo o ResourseServer vai ser capaz de decodificar o token e analisá-lo  */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// Libera acesso no H2
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		// determina quem pode acessar o que, através das requisições
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_GET).hasAnyRole("OPERATOR", "ADMIN")
		.anyRequest().hasAnyRole("ADMIN");
	}

}

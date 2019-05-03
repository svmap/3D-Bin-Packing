package packing.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter 
{
    @Override
    protected void configure(HttpSecurity http) throws Exception 
    {
        http.csrf().disable().authorizeRequests()
        		/*
        		 * Skip The Patter Matching From Authorization
        		 */
                .antMatchers("/",
                		"/*.html",
                		"/favicon.ico",
                		"/css/**",
                		"/js/**",
                		"/assets/**",
                		"/images/*",
                		"/downloadFile/**").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                //.antMatchers(HttpMethod.POST, "/getJson").permitAll()
                .anyRequest().authenticated()
                .and()
                /*
                 * We filter the api/login requests
                 */
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                /*
                 * And filter other requests to check the presence of JWT in header
                 */
                .addFilterBefore(new JWTAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception 
    {
    	/*
    	 * Create a default account
    	 */
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}password")
                .roles("ADMIN");
    }
}
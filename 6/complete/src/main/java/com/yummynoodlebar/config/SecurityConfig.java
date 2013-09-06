package com.yummynoodlebar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("letsnosh").password("noshing").roles("USER");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //{!begin configure}
    http.authorizeUrls()
        .antMatchers("/order/**").hasRole("USER")
        .antMatchers("/checkout").hasRole("USER")
        .anyRequest().anonymous()
        .and()
        //This will generate a login form if none is supplied.
        .formLogin();
    //{!end configure}
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
       return super.authenticationManagerBean();
  }
}

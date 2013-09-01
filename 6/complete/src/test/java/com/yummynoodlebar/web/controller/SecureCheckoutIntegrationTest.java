package com.yummynoodlebar.web.controller;

import com.yummynoodlebar.config.CoreConfig;
import com.yummynoodlebar.config.PersistenceConfig;
import com.yummynoodlebar.config.SecurityConfig;
import com.yummynoodlebar.config.WebConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
    SecurityConfig.class, CoreConfig.class,
    PersistenceConfig.class, WebConfig.class})
public class SecureCheckoutIntegrationTest {

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  @Autowired
  WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
        .addFilter(springSecurityFilterChain).build();
  }

  @Test
  public void thatForcedToLoginOnCheckout() throws Exception {
    this.mockMvc.perform(
        post("/checkout"))
        .andExpect(redirectedUrl("http://localhost/login"));
  }

  @Test
  public void thatForcedToLoginOnOrder() throws Exception {
    this.mockMvc.perform(
        post("/order/12345"))
        .andExpect(redirectedUrl("http://localhost/login"));
  }

  @Test
  public void authenticatingUser() throws Exception {
    mockMvc.perform(post("/login").param("username", "letsnosh").param("password", "noshing"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(redirectedUrl("/"));

  }



}

package com.greenfox.springsecurityjwt.controllers;

import com.greenfox.springsecurityjwt.dtos.AuthenticationRequest;
import com.greenfox.springsecurityjwt.dtos.AuthenticationResponse;
import com.greenfox.springsecurityjwt.services.MyUserDetailsService;
import com.greenfox.springsecurityjwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloResource {
  private AuthenticationManager authenticationManager;
  private MyUserDetailsService userDetailsService;
  private JwtUtil jwtUtil;

  @Autowired
  public HelloResource(
      AuthenticationManager authenticationManager,
      MyUserDetailsService userDetailsService, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping("/hello")
  public String hello() {
    return "Hello World";
  }

  @PostMapping("/authenticate")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
      throws Exception {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authenticationRequest
              .getUsername(), authenticationRequest.getPassword()));
    } catch (BadCredentialsException e) {
      throw new Exception("Incorrect username or password", e);
    }
    final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
    String jwt = jwtUtil.generateToken(userDetails);
    return ResponseEntity.ok(new AuthenticationResponse(jwt));
  }
}

package com.examly.springapp.security;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.examly.springapp.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtFilter extends OncePerRequestFilter {
 private final JwtUtil jwtUtil;
 private final UserRepo userRepo;
 @Autowired public JwtFilter(JwtUtil jwtUtil, UserRepo userRepo){this.jwtUtil=jwtUtil;this.userRepo=userRepo;}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)
     throws ServletException,IOException {
   String header=request.getHeader("Authorization");
   if(header!=null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication()==null){
     try {
       String token=header.substring(7);
       String email=jwtUtil.extractUsername(token);
       var user=userRepo.findByEmail(email);
       if(user.isPresent() && jwtUtil.validateToken(token,email)){
         String role=user.get().getUserRole();
         var auth=new UsernamePasswordAuthenticationToken(email,null,List.of(new SimpleGrantedAuthority("ROLE_"+role.toUpperCase(java.util.Locale.ROOT))));
         SecurityContextHolder.getContext().setAuthentication(auth);
       } else {
         response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid authentication token");return;
       }
     } catch(io.jsonwebtoken.JwtException | IllegalArgumentException e){
       SecurityContextHolder.clearContext();response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid authentication token");return;
     }
   }
   chain.doFilter(request,response);
 }
}

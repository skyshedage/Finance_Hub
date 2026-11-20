package com.examly.springapp;
import com.examly.springapp.security.*;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.model.User;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class JwtFilterSecurityTest {
 @AfterEach void clear(){SecurityContextHolder.clearContext();}
 @Test void missingKeyRejected(){assertThrows(IllegalArgumentException.class,()->new JwtUtil(""));}
 @Test void malformedTokenRejected() throws Exception {
   var req=new MockHttpServletRequest();req.addHeader("Authorization","Bearer malformed");var res=new MockHttpServletResponse();
   new JwtFilter(new JwtUtil("test-only-placeholder-at-least-32-bytes"),mock(UserRepo.class)).doFilter(req,res,new MockFilterChain());
   assertEquals(401,res.getStatus());assertNull(SecurityContextHolder.getContext().getAuthentication());
 }
 @Test void usesCurrentDatabaseRoleNotStaleTokenRole() throws Exception {
   var jwt=new JwtUtil("test-only-placeholder-at-least-32-bytes");var repo=mock(UserRepo.class);var user=new User();
   user.setEmail("test@example.invalid");user.setUserRole("ADMIN");String token=jwt.generateToken(user);user.setUserRole("USER");
   when(repo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
   var req=new MockHttpServletRequest();req.addHeader("Authorization","Bearer "+token);
   new JwtFilter(jwt,repo).doFilter(req,new MockHttpServletResponse(),new MockFilterChain());
   assertEquals("ROLE_USER",SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
 }
 @Test void serializedUserDoesNotExposePassword() throws Exception {
   var user=new User();user.setPassword("test-hash");
   assertFalse(new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules().writeValueAsString(user).contains("test-hash"));
 }
}

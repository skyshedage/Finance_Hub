package com.examly.springapp.security;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import com.examly.springapp.repository.*;
@Component("ownership")
public class Ownership {
 private final UserRepo users;private final FeedbackRepo feedbacks;private final EnquiryRepo enquiries;
 public Ownership(UserRepo users, FeedbackRepo feedbacks, EnquiryRepo enquiries){this.users=users;this.feedbacks=feedbacks;this.enquiries=enquiries;}
 public boolean user(Long id, Authentication auth){
   return id!=null && auth!=null && users.findByEmail(auth.getName()).map(u->id.equals(u.getUserId())).orElse(false);
 }
 public boolean feedback(Long id, Authentication auth){
   return id!=null && feedbacks.findById(id).map(f->f.getUser()!=null && user(f.getUser().getUserId(),auth)).orElse(false);
 }
 public boolean enquiry(Long id, Authentication auth){
   return id!=null && enquiries.findById(id).map(e->e.getUser()!=null && user(e.getUser().getUserId(),auth)).orElse(false);
 }
}

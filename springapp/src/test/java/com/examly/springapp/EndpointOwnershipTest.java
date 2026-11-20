package com.examly.springapp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class EndpointOwnershipTest {
 @Autowired MockMvc mvc;
 @Autowired com.examly.springapp.repository.UserRepo users;
 @WithMockUser(username="unknown@example.invalid",roles="USER") @Test void cannotReadAnotherUsersApplications() throws Exception {
   mvc.perform(get("/api/planapplications/applications/user/99")).andExpect(status().isForbidden());
 }
 @WithMockUser(roles="USER") @Test void normalUserCannotReadAllApplications() throws Exception {
   mvc.perform(get("/api/planapplications")).andExpect(status().isForbidden());
 }
 @WithMockUser(roles="USER") @Test void cannotPostFeedbackAsAnotherUser() throws Exception {
   mvc.perform(post("/api/feedback/99").contentType("application/json").content("{}")).andExpect(status().isForbidden());
 }
 @WithMockUser(roles="USER") @Test void cannotDeleteUnownedFeedback() throws Exception {
   mvc.perform(delete("/api/feedback/99")).andExpect(status().isForbidden());
 }
 @WithMockUser(roles="USER") @Test void cannotReadAnotherUsersEnquiries() throws Exception {
   mvc.perform(get("/api/enquiries/user/99")).andExpect(status().isForbidden());
 }
 @WithMockUser(roles="USER") @Test void cannotUpdateUnownedEnquiry() throws Exception {
   mvc.perform(put("/api/enquiries/99").contentType("application/json").content("{}")).andExpect(status().isForbidden());
 }
 @WithMockUser(roles="USER") @Test void cannotApplyForAnotherUser() throws Exception {
   mvc.perform(post("/api/planapplications").contentType("application/json").content("{\"userId\":99,\"savingsPlanId\":1}")).andExpect(status().isForbidden());
 }
 @Test void anonymousCannotReadProtectedPlans() throws Exception {mvc.perform(get("/api/savingsplans")).andExpect(status().isForbidden());}
 @Test void anonymousCannotReadFeedback() throws Exception {mvc.perform(get("/api/feedback")).andExpect(status().isForbidden());}
 @WithMockUser(username="owner@example.invalid",roles="USER") @Test void userCanReadOwnFeedback() throws Exception {
   var u=new com.examly.springapp.model.User();u.setEmail("owner@example.invalid");u.setUsername("owner");u.setMobileNumber("test-owner");u.setUserRole("USER");u=users.save(u);
   mvc.perform(get("/api/feedback/"+u.getUserId())).andExpect(status().isOk());
 }
 @WithMockUser(roles="ADMIN") @Test void adminCanReadAllApplications() throws Exception {mvc.perform(get("/api/planapplications")).andExpect(status().isOk());}

 @WithMockUser(username="empty-owner@example.invalid",roles="USER") @Test void ownerWithNoEnquiriesGetsEmptyList() throws Exception {
   var u=new com.examly.springapp.model.User();u.setEmail("empty-owner@example.invalid");u.setUsername("emptyowner");u.setMobileNumber("test-empty-owner");u.setUserRole("USER");u=users.save(u);
   mvc.perform(get("/api/enquiries/user/"+u.getUserId())).andExpect(status().isOk()).andExpect(jsonPath("$.data").isEmpty());
 }
}

package com.examly.springapp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class WorkflowIntegrationTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json;
 @Autowired com.examly.springapp.repository.UserRepo users;
 String register(String who) throws Exception {
  mvc.perform(post("/api/register").contentType("application/json").content("{\"username\":\""+who+"\",\"email\":\""+who+"@example.invalid\",\"mobileNumber\":\""+who+"\",\"password\":\"LocalTest!123\",\"userRole\":\"ADMIN\"}")).andExpect(status().isCreated());
  return login(who);
 }
 String login(String who) throws Exception {
  String body=mvc.perform(post("/api/login").contentType("application/json").content("{\"email\":\""+who+"@example.invalid\",\"password\":\"LocalTest!123\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  return "Bearer "+json.readTree(body).get("data").asText().split(",")[0];
 }
 long id(String body,String key)throws Exception{return json.readTree(body).path("data").path(key).asLong();}
 @Test void userAdminApplicationEnquiryAndFeedbackJourney() throws Exception {
  String user=register("journeyuser");String other=register("journeyother");register("journeyadmin");
  var a=users.findByEmail("journeyadmin@example.invalid").orElseThrow();a.setUserRole("ADMIN");users.save(a);String admin=login("journeyadmin");
  long uid=users.findByEmail("journeyuser@example.invalid").orElseThrow().getUserId();
  String plan=mvc.perform(post("/api/savingsplans").header("Authorization",admin).contentType("application/json").content("{\"name\":\"Test savings\",\"goalAmount\":1000,\"timeFrame\":12,\"riskLevel\":\"LOW\",\"status\":\"ACTIVE\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  long pid=id(plan,"savingsPlanId");
  mvc.perform(get("/api/savingsplans").header("Authorization",user)).andExpect(status().isOk());
  String app=mvc.perform(post("/api/planapplications").header("Authorization",user).contentType("application/json").content("{\"userId\":"+uid+",\"savingsPlanId\":"+pid+",\"appliedAmount\":200,\"status\":\"APPROVED\",\"remarks\":\"forged\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("PENDING")).andReturn().getResponse().getContentAsString();
  long aid=id(app,"planApplicationId");
  mvc.perform(get("/api/planapplications/applications/user/"+uid).header("Authorization",other)).andExpect(status().isForbidden());
  mvc.perform(put("/api/planapplications/approve/"+aid).header("Authorization",user)).andExpect(status().isForbidden());
  mvc.perform(put("/api/planapplications/approve/"+aid).header("Authorization",admin)).andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("APPROVED"));
  mvc.perform(get("/api/planapplications/applications/user/"+uid).header("Authorization",user)).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].status").value("APPROVED"));
  String enq=mvc.perform(post("/api/enquiries/"+uid).header("Authorization",user).contentType("application/json").content("{\"message\":\"A local test enquiry\",\"replyFromManager\":\"forged\",\"status\":\"RESOLVED\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING")).andExpect(jsonPath("$.replyFromManager").isEmpty()).andReturn().getResponse().getContentAsString();
  long eid=json.readTree(enq).get("enquiryId").asLong();
  mvc.perform(put("/api/enquiries/"+eid+"/reply").param("reply","Local answer").param("status","RESOLVED").header("Authorization",admin)).andExpect(status().isOk());
  mvc.perform(get("/api/enquiries/user/"+uid).header("Authorization",user)).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].status").value("RESOLVED"));
  String feedback=mvc.perform(post("/api/feedback/"+uid).header("Authorization",user).contentType("application/json").content("{\"feedbackText\":\"Local feedback\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.user.password").doesNotExist()).andReturn().getResponse().getContentAsString();
  long fid=json.readTree(feedback).get("feedbackId").asLong();
  mvc.perform(get("/api/feedback").header("Authorization",admin)).andExpect(status().isOk());
  mvc.perform(delete("/api/feedback/"+fid).header("Authorization",other)).andExpect(status().isForbidden());
  mvc.perform(delete("/api/feedback/"+fid).header("Authorization",user)).andExpect(status().isOk());
 }
 @Test void rejectsInvalidPlanAndAmount() throws Exception {
  register("badadmin");var a=users.findByEmail("badadmin@example.invalid").orElseThrow();a.setUserRole("ADMIN");users.save(a);
  mvc.perform(post("/api/savingsplans").header("Authorization",login("badadmin")).contentType("application/json").content("{\"name\":\"Invalid\",\"goalAmount\":-1,\"timeFrame\":0,\"status\":\"ACTIVE\"}")).andExpect(status().isBadRequest());
 }
 @Autowired com.examly.springapp.repository.SavingsPlanRepo plans;
 @Test void proofImagesAreValidatedPersistedAndOwnerRestricted() throws Exception {
  String owner=register("proofowner"),other=register("proofother");long uid=users.findByEmail("proofowner@example.invalid").orElseThrow().getUserId();
  var plan=new com.examly.springapp.model.SavingsPlan();plan.setName("Proof test");plan.setGoalAmount(1000);plan.setTimeFrame(12);plan.setStatus("ACTIVE");plan=plans.save(plan);
  var image=new java.awt.image.BufferedImage(32,32,java.awt.image.BufferedImage.TYPE_INT_RGB);var random=new java.util.Random(42);
  for(int x=0;x<32;x++)for(int y=0;y<32;y++)image.setRGB(x,y,random.nextInt());
  var bytes=new java.io.ByteArrayOutputStream();javax.imageio.ImageIO.write(image,"png",bytes);org.junit.jupiter.api.Assertions.assertTrue(bytes.size()>255);
  var request=new java.util.HashMap<String,Object>();request.put("userId",uid);request.put("savingsPlanId",plan.getSavingsPlanId());request.put("appliedAmount",200);request.put("image","proof.png");request.put("proof",java.util.Base64.getEncoder().encodeToString(bytes.toByteArray()));
  String body=mvc.perform(post("/api/planapplications").header("Authorization",owner).contentType("application/json").content(json.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.data.proofData").doesNotExist()).andReturn().getResponse().getContentAsString();long aid=id(body,"planApplicationId");
  mvc.perform(get("/api/planapplications/"+aid+"/proof").header("Authorization",owner)).andExpect(status().isOk()).andExpect(content().contentType("image/png")).andExpect(content().bytes(bytes.toByteArray()));
  mvc.perform(get("/api/planapplications/"+aid+"/proof").header("Authorization",other)).andExpect(status().isForbidden());
  request.put("proof","not an image");mvc.perform(post("/api/planapplications").header("Authorization",owner).contentType("application/json").content(json.writeValueAsString(request))).andExpect(status().isBadRequest());
 }
}

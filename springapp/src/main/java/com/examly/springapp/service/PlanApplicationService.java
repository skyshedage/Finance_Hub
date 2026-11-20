package com.examly.springapp.service;
import java.util.List;
import com.examly.springapp.model.PlanApplication;
import com.examly.springapp.model.PlanApplicationRequest;
/** Contract for the plan-application operations used by current controllers. */
public interface PlanApplicationService {
 PlanApplication applyForPlan(PlanApplicationRequest request);
 List<PlanApplication> getUserApplications(Long userId);
 List<PlanApplication> getAllApplications();
 PlanApplication approveApplication(Long id);
 PlanApplication rejectApplication(Long id, String remarks);
 List<PlanApplication> getUserApplication(Long userId);
 Long countByStatus(String status);
}

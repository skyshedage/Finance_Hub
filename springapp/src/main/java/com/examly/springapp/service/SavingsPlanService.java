package com.examly.springapp.service;
import java.util.List;
import com.examly.springapp.model.SavingsPlan;
/** Contract for the savings-plan operations used by the current controller. */
public interface SavingsPlanService {
 SavingsPlan addSavingsPlan(SavingsPlan savingsPlan);
 List<SavingsPlan> getAllPlans();
 SavingsPlan updateSavingsPlan(Long id, SavingsPlan updatedPlan);
 void deleteSavingsPlan(Long id);
 SavingsPlan getSavingsPlanById(Long id);
}

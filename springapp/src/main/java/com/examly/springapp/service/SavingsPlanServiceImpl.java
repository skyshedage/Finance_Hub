package com.examly.springapp.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.examly.springapp.model.SavingsPlan;
import com.examly.springapp.repository.SavingsPlanRepo;

@Service
public class SavingsPlanServiceImpl implements SavingsPlanService {

    @Autowired
    private SavingsPlanRepo savingsPlanRepository;

    public SavingsPlan addSavingsPlan(SavingsPlan savingsPlan) {
        validate(savingsPlan);savingsPlan.setSavingsPlanId(null);
        return savingsPlanRepository.save(savingsPlan);
    }

    public List<SavingsPlan> getAllPlans() {
        return savingsPlanRepository.findAll();
    }

    private void validate(SavingsPlan p){
        if(p.getStatus()!=null)p.setStatus(p.getStatus().toUpperCase(java.util.Locale.ROOT));
        if(p.getName()==null || p.getName().isBlank() || !Double.isFinite(p.getGoalAmount()) || p.getGoalAmount()<=0 || p.getTimeFrame()<=0)
            throw new IllegalArgumentException("Name, positive finite goal and positive timeframe are required");
        if(!"ACTIVE".equals(p.getStatus()) && !"INACTIVE".equals(p.getStatus())) throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE");
    }
    public SavingsPlan updateSavingsPlan(Long id, SavingsPlan updatedPlan) {
        validate(updatedPlan);
        SavingsPlan plan = savingsPlanRepository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Plan not found"));
        plan.setName(updatedPlan.getName());
        plan.setGoalAmount(updatedPlan.getGoalAmount());
        plan.setTimeFrame(updatedPlan.getTimeFrame());
        plan.setRiskLevel(updatedPlan.getRiskLevel());
        plan.setDescription(updatedPlan.getDescription());
        plan.setStatus(updatedPlan.getStatus());
        return savingsPlanRepository.save(plan);
    }

    public void deleteSavingsPlan(Long id) {
        savingsPlanRepository.deleteById(id);
    }

    public SavingsPlan getSavingsPlanById(Long savingsPlanId) {
        return savingsPlanRepository.findById(savingsPlanId).orElse(null);
    }
}

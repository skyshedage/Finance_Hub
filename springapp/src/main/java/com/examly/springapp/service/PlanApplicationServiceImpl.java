package com.examly.springapp.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.examly.springapp.model.PlanApplication;
import com.examly.springapp.model.PlanApplicationRequest;
import com.examly.springapp.model.SavingsPlan;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.PlanApplicationRepo;
import com.examly.springapp.repository.SavingsPlanRepo;
import com.examly.springapp.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PlanApplicationServiceImpl implements PlanApplicationService {

    @Autowired
    private PlanApplicationRepo planApplicationRepository;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private SavingsPlanRepo savingsPlanRepository;

    @Transactional
    public PlanApplication applyForPlan(PlanApplicationRequest request) {
        if(request.getUserId()==null || request.getSavingsPlanId()==null || !Double.isFinite(request.getAppliedAmount()) || request.getAppliedAmount()<=0)
            throw new IllegalArgumentException("A user, savings plan and positive finite amount are required");
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        SavingsPlan savingsPlan = savingsPlanRepository.findById(request.getSavingsPlanId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Savings plan not found"));

        if(!"ACTIVE".equalsIgnoreCase(savingsPlan.getStatus())) throw new IllegalArgumentException("Savings plan is not active");
        PlanApplication planApplication = new PlanApplication();
        planApplication.setAppliedAmount(request.getAppliedAmount());
        planApplication.setStatus("PENDING");
        planApplication.setDate(LocalDate.now());
        planApplication.setRemarks(null);
        planApplication.setImage(request.getImage()==null?null:request.getImage().replaceAll("[^A-Za-z0-9._-]", "_"));
        planApplication.setProofData(decodeProof(request.getProof()));
        planApplication.setUser(user);
        planApplication.setSavingsPlan(savingsPlan);

        return planApplicationRepository.save(planApplication);
    }

    private byte[] decodeProof(String value) {
        if(value==null || value.isBlank()) return null;
        if(value.length()>2_800_000) throw new IllegalArgumentException("Proof image must be at most 2 MB");
        try {
            byte[] bytes=java.util.Base64.getDecoder().decode(value);
            if(bytes.length>2_097_152) throw new IllegalArgumentException("Proof image must be at most 2 MB");
            try(var input=javax.imageio.ImageIO.createImageInputStream(new java.io.ByteArrayInputStream(bytes))) {
                var readers=javax.imageio.ImageIO.getImageReaders(input);
                if(!readers.hasNext()) throw new IllegalArgumentException("Proof must be a PNG or JPEG image");
                var reader=readers.next();
                try {
                    String format=reader.getFormatName();
                    if(!format.equalsIgnoreCase("PNG") && !format.equalsIgnoreCase("JPEG")) throw new IllegalArgumentException("Proof must be PNG or JPEG");
                    reader.setInput(input);
                    if(reader.getWidth(0)>3000 || reader.getHeight(0)>3000) throw new IllegalArgumentException("Proof dimensions must not exceed 3000 pixels");
                    var image=reader.read(0);var out=new java.io.ByteArrayOutputStream();
                    javax.imageio.ImageIO.write(image,"png",out);return out.toByteArray();
                } finally {reader.dispose();}
            }
        } catch(java.io.IOException | IllegalArgumentException e) {throw new IllegalArgumentException("Invalid proof image: use a PNG or JPEG up to 2 MB and 3000 pixels");}
    }

    public List<PlanApplication> getUserApplications(Long userId) {
        return planApplicationRepository.findByUser_UserId(userId);
    }

    public List<PlanApplication> getAllApplications() {
        return planApplicationRepository.findAll();
    }

    public PlanApplication approveApplication(Long id) {
        PlanApplication application = planApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
        application.setStatus("APPROVED");
        return planApplicationRepository.save(application);
    }

    public PlanApplication rejectApplication(Long id, String remarks) {
        PlanApplication application = planApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
        application.setStatus("REJECTED");
        application.setRemarks(remarks);
        return planApplicationRepository.save(application);
    }

    public List<PlanApplication> getUserApplication(Long userId) {
        return planApplicationRepository.findByUser_UserId(userId);
    }

    public Long countByStatus(String string) {
        return planApplicationRepository.cByStatus(string);
    }

}

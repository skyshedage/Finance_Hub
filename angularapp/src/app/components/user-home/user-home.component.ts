import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { PlanapplicationformService } from 'src/app/services/planapplicationform.service';
 
import { EnquiryService } from 'src/app/services/enquiry.service';
import { SavingsPlanService } from 'src/app/services/savingsplan.service';
 
@Component({
  selector: 'app-user-home',
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent implements OnInit {
  stats = { userActivePlans: 0, totalSavings: 0, applicationCount: 0, totalEnquiries: 0 };
  userName: string = '';
 
  constructor(
    private router: Router,
    private authService: AuthService,
    private planApplicationService: PlanapplicationformService,
    private savingsPlanService: SavingsPlanService,
    private enquiryService: EnquiryService
  ) {}
 
  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.loadUserStats();
  }
 
  loadUserStats(): void {
    const userId = this.authService.getUserId();
 
    this.planApplicationService.getAllPlanApplicationsForUser().subscribe((response:any) => {
      const plans = response?.data || [];
      this.stats.applicationCount = plans.length;
      this.stats.totalSavings = plans.reduce((sum, plan) => plan.status === "APPROVED" ? sum + plan.appliedAmount : sum, 0);
      this.stats.userActivePlans = plans.filter(plan => plan.status === "APPROVED").length;
    });
    this.enquiryService.getUserEnquiries(Number(userId)).subscribe((response:any) => {
      this.stats.totalEnquiries = response?.data?.length;
    });
  }
 
  navigateToPlans() { this.router.navigate(['/user/view/savings/plan']); }
  navigateToApplications() { this.router.navigate(['/user/applied/plans']); }
  navigateToEnquiries() { this.router.navigate(['/user/add/enquiry']); }
  navigateToFeedback() { this.router.navigate(['/user/add/feedback']); }
}
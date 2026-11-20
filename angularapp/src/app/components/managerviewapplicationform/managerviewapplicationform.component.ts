import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { PlanApplication } from 'src/app/models/planapplication.model';
import { PlanapplicationformService } from 'src/app/services/planapplicationform.service';

@Component({
  selector: 'app-managerviewapplicationform',
  templateUrl: './managerviewapplicationform.component.html',
  styleUrls: ['./managerviewapplicationform.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ManagerviewapplicationformComponent implements OnInit {

  planApplications: PlanApplication[] = [];
  showModal: boolean = false;
  imageSrc: SafeUrl | string = "";
  private proofObjectUrl: string = "";
  showRejectModal: boolean = false;  // Flag to show rejection modal
  rejectionReason: string = "";      // To capture rejection reason
  selectedApplicationId: number = 0; // To capture the application being rejected

  constructor(private service: PlanapplicationformService, private sanitizer: DomSanitizer) {}

  ngOnInit(): void {
    this.viewAllApplication();
  }

  viewAllApplication(): void {
    this.service.getAllPlanApplications().subscribe({
      next: (response:any) => {

        this.planApplications = response?.data || [];
      },
      error: (err) => console.error("Error fetching applications", err),
    });
  }

  acceptApplication(planApplicationId: number): void {
    this.service.approvePlanApplication(planApplicationId).subscribe({
      next: (response) => {

        this.viewAllApplication();
      },
      error: (err) => console.error("Error approving application", err),
    });
  }

  openRejectModal(planApplicationId: number): void {
    this.selectedApplicationId = planApplicationId;
    this.showRejectModal = true;
  }

  confirmRejection(): void {
    if (this.rejectionReason.trim()) {
      this.service.rejectPlanApplication(this.selectedApplicationId, this.rejectionReason).subscribe({
        next: (response) => {

          this.showRejectModal = false;
          this.rejectionReason = ""; // Clear the input
          this.viewAllApplication(); // Refresh the applications
        },
        error: (err) => {
          console.error("Error rejecting application", err);
          alert("Error rejecting the application.");
        }
      });
    } else {
      alert("Please provide a rejection reason.");
    }
  }

  toggleOpen(applicationId: number): void {
    this.service.getProof(applicationId).subscribe({next: blob => {
      this.toggleClose();
      // Only trust the URL created here for the backend-decoded PNG, never a user-supplied URL.
      this.proofObjectUrl=URL.createObjectURL(blob);
      this.imageSrc=this.sanitizer.bypassSecurityTrustUrl(this.proofObjectUrl);this.showModal=true;
    },error: () => alert('No proof image is available for this application.')});
  }

  toggleClose(): void {
    this.showModal = false;
    if(this.proofObjectUrl) URL.revokeObjectURL(this.proofObjectUrl);
    this.proofObjectUrl="";
    this.imageSrc = "";
  }

  toggleRejectModal(): void {
    this.showRejectModal = false;
    this.rejectionReason = "";
  }

  

}

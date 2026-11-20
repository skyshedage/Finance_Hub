import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PlanApplication } from 'src/app/models/planapplication.model';
import { AuthService } from 'src/app/services/auth.service';
import { PlanapplicationformService } from 'src/app/services/planapplicationform.service';
import { SavingsPlanService } from 'src/app/services/savingsplan.service';

@Component({
  selector: 'app-userplanapplicationform',
  templateUrl: './userplanapplicationform.component.html',
  styleUrls: ['./userplanapplicationform.component.css']
})
export class UserplanapplicationformComponent implements OnInit {
  planForm: FormGroup;
  showModal: boolean = false;
  planId: number = 0;

  constructor(private fb: FormBuilder, private planApplicationService: PlanapplicationformService, private router: Router, private actRoute: ActivatedRoute,private auth:AuthService) {
    this.planForm = this.fb.group({
      appliedAmount: ['', [Validators.required, Validators.min(100)]],
      date: [''],
      remarks: [''],
      image: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.actRoute.queryParams.subscribe(params => {

      this.planId = +params['planId'];
    })
  }

  proofData: string = '';
  errorMessage: string = '';
  fileName: string = ''; // To store file name

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0]; // Get the first selected file
      if (!['image/png','image/jpeg'].includes(file.type) || file.size > 2 * 1024 * 1024) {
        this.errorMessage = 'Use a PNG or JPEG image up to 2 MB.';
        this.planForm.patchValue({image: ''});this.proofData='';return;
      }
      this.fileName = file.name;
      const reader = new FileReader();
      reader.onload = () => {
        this.proofData = String(reader.result).split(',')[1];
        this.planForm.patchValue({image: this.fileName});this.errorMessage='';
      };
      reader.onerror = () => {this.errorMessage='The image could not be read. Please select it again.';};
      reader.readAsDataURL(file);
    }
  }

  applyForPlan() {

    if(this.planForm.invalid || !this.proofData){this.planForm.markAllAsTouched();return;}
    this.errorMessage='';
    const planId = +this.planId;
    const appliedAmount = this.planForm.value.appliedAmount;

    const application = {
      userId:+this.auth.getUserId(),
      savingsPlanId:planId,
      appliedAmount: appliedAmount,
      date: this.planForm.value.date,
      image: this.fileName,
      proof: this.proofData,
      remarks: this.planForm.value.remarks,
      status: 'PENDING'
    };

    this.planApplicationService.applyForPlan(application).subscribe(
      (response:any) => {

        window.alert("applied successfully");
        this.planForm.reset();
        this.fileName="";
        this.router.navigate(['/user/applied/plans']);
      },
      (error) => {
        this.errorMessage=error?.error?.message || 'Application could not be submitted. Please try again.';
      }
    );
  }

}

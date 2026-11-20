import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EnquiryService } from 'src/app/services/enquiry.service';

@Component({
  selector: 'app-managereditenquiry',
  templateUrl: './managereditenquiry.component.html',
  styleUrls: ['./managereditenquiry.component.css']
})
export class ManagereditenquiryComponent implements OnInit {
  enquiry: any = {
    enquiryId: 0,
    message: '',
    replyFromManager: '',
    status: '',
    userId: 0,
    username: ""
  };

  enquiryId: number; // Store enquiry ID from URL

  constructor(
    private enquiryService: EnquiryService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Get enquiry ID from URL
    this.enquiryId = +this.route.snapshot.params['enqId'];

    if (this.enquiryId) {
      // Fetch enquiry details from API
      this.enquiryService.getEnquiryById(this.enquiryId).subscribe((data: any) => {

        this.enquiry = data.data;
      });
    }
  }

  updateEnquiry(): void {
    if (!this.enquiry.replyFromManager.trim()) {
      alert('Reply cannot be empty!');
      return;
    }

    this.enquiryService.editEnquiry(this.enquiryId, this.enquiry.replyFromManager, this.enquiry.status)
      .subscribe(
        (res) => {
          alert('Enquiry updated successfully');

          this.router.navigate(['/manager/view/enquiries']);
        },
        (error) => {
          console.error("Error updating enquiry:", error);
          alert('Failed to update enquiry.');
        }
      );
  }
}

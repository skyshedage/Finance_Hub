import { Component, OnInit } from '@angular/core';
import { AdminDashService } from 'src/app/services/admin-dash.service';
import { EnquiryService } from 'src/app/services/enquiry.service';

@Component({
  selector: 'app-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.css']
})
export class TablesComponent implements OnInit {
  recentApplications: any[] = [];
  recentEnquiries: any[] = [];
  recentFeedbacks: any[] = [];

  constructor(private dashboardService: AdminDashService,private enqService:EnquiryService) {}

  ngOnInit(): void {
    this.dashboardService.getRecentApplications().subscribe(response => this.recentApplications = response.data);

    this.dashboardService.getRecentEnquiries().subscribe(response => this.recentEnquiries = response.data);

    this.dashboardService.getRecentFeedbacks().subscribe(response => this.recentFeedbacks = response.data);

    this.enqService.getAllEnquiry().subscribe(response => this.recentEnquiries=response);

  }
}

import { Component, OnInit, ElementRef, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-adminnav',
  templateUrl: './adminnav.component.html',
  styleUrls: ['./adminnav.component.css']
})
export class AdminnavComponent implements OnInit {
  
  menuOpen: boolean = false;
  username : string;
  constructor(private router: Router, private authService: AuthService, private element: ElementRef) {}

  ngOnInit(): void {
    this.username=this.authService.getUserName();
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
    setTimeout(() => this.element.nativeElement.querySelector(this.menuOpen ? '.menu-close' : '.navbar-toggler')?.focus());
  }

  @HostListener('document:keydown', ['$event'])
  onKey(event: KeyboardEvent): void {
    if (!this.menuOpen) return;
    if (event.key === 'Escape') { event.preventDefault(); this.toggleMenu(); }
    if (event.key === 'Tab') {
      const items = this.element.nativeElement.querySelectorAll('.overlay button, .overlay a[href], .overlay [tabindex="0"]');
      const first = items[0], last = items[items.length - 1];
      if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
      else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]);
  }
}
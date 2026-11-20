import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private apiURL = '/api'; // Change as per backend API

  constructor(private http: HttpClient) {}

  login(credentials: any) {
    return this.http.post(`${this.apiURL}/login`, credentials);
  }
  register(creadentials :any) {
    
    return this.http.post(`${this.apiURL}/register`,creadentials);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');

    // this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getUserName(){
    return localStorage.getItem('username');

  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getUserId(): string | null {
    return localStorage.getItem('userId');
  }
}

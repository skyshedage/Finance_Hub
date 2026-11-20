import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PlanApplication } from '../models/planapplication.model';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PlanapplicationformService {

  constructor(private http:HttpClient,private auth:AuthService) { }
  apiUrl:string='/api/planapplications'

  public getProof(id:number):Observable<Blob>{return this.http.get(this.apiUrl+'/'+id+'/proof',{responseType:'blob'});}

  public addPlanApplication(data:PlanApplication):Observable<PlanApplication>{
    return this.http.post<PlanApplication>(this.apiUrl,data);
  }
  ///approve/{id}

  public deletePlanApplication(planId: number): Observable<void> {
    return this.http.delete<void>(this.apiUrl + '/' + planId);
  }

  public getAppliedPlans(userId: number): Observable<PlanApplication[]> {
    return this.http.get<PlanApplication[]>(this.apiUrl + '/applications/user/' + userId);
  }

  //working
  public getAllPlanApplicationsForUser(): Observable<PlanApplication[]> {
    return this.http.get<PlanApplication[]>(this.apiUrl + '/applications/user/'+`${this.auth.getUserId()}`);
  }

  public getAllPlanApplications(): Observable<PlanApplication[]> {
    return this.http.get<PlanApplication[]>(this.apiUrl);
  }

  public updatePlanApplication(planId: number, updateData: PlanApplication): Observable<PlanApplication> {
    return this.http.put<PlanApplication>(this.apiUrl + '/' + planId, updateData);
  }
  
  public applyForPlan(application: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, application);
  }

  public approvePlanApplication(planApplicationId:number):Observable<any>{
    return this.http.put<any>(this.apiUrl+`/approve/${planApplicationId}`,null);
  }
  
  public rejectPlanApplication(planApplicationId:number,remarks:string):Observable<any>{
    return this.http.put<any>(this.apiUrl+`/reject/${planApplicationId}`,remarks);
  }
}
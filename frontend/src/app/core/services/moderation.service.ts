import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SuspiciousUser {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  reason: string;
  suspendedUntil: Date;
  flaggedAt: Date;
  suspensionType: 'POSTING' | 'LOGIN';
  reportCount: number;
  blockCount: number;
}

export interface DetectionResponse {
  suspiciousUsers: SuspiciousUser[];
  totalCount: number;
  detectedAt: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ModerationService {
  private apiUrl = `${environment.apiBaseUrl}/moderation`;

  constructor(private http: HttpClient) { }

  detectSuspiciousUsers(): Observable<DetectionResponse> {
    return this.http.post<DetectionResponse>(`${this.apiUrl}/detect-suspicious`, {});
  }

  detectSuspiciousUser(userId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/detect-user/${userId}`, {});
  }

  applySuspension(userId: number, suspensionType: string, suspendedUntil: Date): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/apply-suspension/${userId}`, {
      suspensionType,
      suspendedUntil: suspendedUntil.getTime().toString()
    });
  }
}

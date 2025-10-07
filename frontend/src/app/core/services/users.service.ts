import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, map } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UsersService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}`;

  search(q: string): Observable<User[]> {
    return this.http.get<any>(`${this.base}/search`, { params: { q } })
      .pipe(map(response => response.users || []));
  }

  addFriend(userId: number) {
    return this.http.post(`${this.base}/users/${userId}/friend`, {});
  }

  block(userId: number) {
    return this.http.post(`${this.base}/users/${userId}/block`, {});
  }
}

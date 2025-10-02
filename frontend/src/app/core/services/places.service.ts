import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, map } from 'rxjs';
import { Place } from '../models/place.model';

export interface CreatePlaceRequest {
  name: string;
  country: string;
  city: string;
  description?: string;
  hashtags?: string[];
}

@Injectable({ providedIn: 'root' })
export class PlacesService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/places`;

  list(): Observable<Place[]> {
    return this.http.get<{ places: Place[] }>(this.base).pipe(
      map(response => response.places ?? [])
    );
  }

  create(dto: CreatePlaceRequest): Observable<Place> {
    return this.http.post<{ place: Place }>(`${this.base}/add`, dto).pipe(
      map(response => response.place)
    );
  }

  rate(id: number, score: number, description: string) {
    return this.http.post(`${this.base}/${id}/rate`, { score, description });
  }

  get(id: number): Observable<Place> {
    return this.http.get<{ place: Place }>(`${this.base}/${id}`).pipe(
      map(response => response.place)
    );
  }
}

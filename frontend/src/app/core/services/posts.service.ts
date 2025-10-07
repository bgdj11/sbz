import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, map } from 'rxjs';
import { Post } from '../models/post.model';
import { Place } from '../models/place.model';

interface DashboardResp { currentUser: any; posts: Post[]; places: Place[]; }
interface ProfileResp { currentUser: any; posts: Post[]; }

export interface CreatePostRequest {
  content: string;
  hashtags?: string[];
}

export interface RecommendedPost {
  post: Post;
  score: number;
  reasons: string[];
}

@Injectable({ providedIn: 'root' })
export class PostsService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}`;

  friends(): Observable<Post[]> {
    return this.http.get<DashboardResp>(`${this.base}/dashboard`).pipe(map(r => r.posts ?? []));
  }

  my(): Observable<Post[]> {
    return this.http.get<ProfileResp>(`${this.base}/profile`).pipe(map(r => r.posts ?? []));
  }

  getFriendsFeed(userId: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.base}/feed/friends`, {
      params: { userId }
    });
  }

  getRecommendedFeed(userId: string): Observable<RecommendedPost[]> {
    return this.http.get<RecommendedPost[]>(`${this.base}/feed/recommend`, {
      params: { userId }
    });
  }

  create(request: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>(`${this.base}/posts`, request);
  }

  like(id: number): Observable<{ liked: boolean; count: number }> {
    return this.http.post<{ liked: boolean; count: number }>(`${this.base}/posts/${id}/like`, {});
  }

  report(id: number): Observable<any> {
    return this.http.post(`${this.base}/posts/${id}/report`, {});
  }
}

import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PostsService, CreatePostRequest, RecommendedPost } from '../../../core/services/posts.service';
import { Post } from '../../../core/models/post.model';
import { HashtagInputComponent } from '../../../shared/hashtag-input/hashtag-input.component';
import { AuthService } from '../../../core/services/auth.service';

type FeedMode = 'friends' | 'recommended';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, HashtagInputComponent],
  templateUrl: './friends-feed.component.html',
  styles: [`
    .create-post-card {
      margin: 20px 0;
      padding: 20px;
      background: #f8f9fa;
      border: 1px solid #dee2e6;
      border-radius: 8px;
    }

    .create-post-card h3 {
      margin: 0 0 16px 0;
      color: #495057;
    }

    .form-group {
      margin-bottom: 16px;
    }

    .form-group label {
      display: block;
      margin-bottom: 6px;
      font-weight: 500;
      color: #495057;
    }

    .form-control {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #ced4da;
      border-radius: 4px;
      font-size: 14px;
      box-sizing: border-box;
    }

    .form-control:focus {
      outline: none;
      border-color: #007acc;
      box-shadow: 0 0 0 2px rgba(0, 122, 204, 0.2);
    }

    .form-actions {
      text-align: right;
    }

    .btn {
      padding: 8px 16px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      text-decoration: none;
      display: inline-block;
    }

    .btn-primary {
      background: #007acc;
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      background: #005a9a;
    }

    .btn-primary:disabled {
      background: #6c757d;
      cursor: not-allowed;
    }

    .btn-sm {
      padding: 4px 8px;
      font-size: 12px;
    }

    .btn-warning {
      background: #ffc107;
      color: #212529;
    }

    .posts-container {
      margin-top: 20px;
    }

    .post-card {
      margin: 16px 0;
      padding: 16px;
      border: 1px solid #dee2e6;
      border-radius: 8px;
      background: white;
    }

    .post-author {
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid #eee;
    }

    .post-content {
      margin: 12px 0;
    }

    .post-content p {
      margin: 0 0 12px 0;
      line-height: 1.5;
    }

    .post-meta {
      margin: 12px 0;
      padding: 8px 0;
      border-top: 1px solid #eee;
    }

    .post-location {
      color: #6c757d;
      font-size: 13px;
      margin-bottom: 8px;
    }

    .post-hashtags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }

    .hashtag {
      background: #e3f2fd;
      color: #1976d2;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }

    .post-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 12px;
      padding-top: 8px;
      border-top: 1px solid #eee;
    }

    .post-date {
      color: #6c757d;
      font-size: 13px;
    }

    .post-actions {
      display: flex;
      gap: 8px;
      align-items: center;
    }

    .likes-count {
      font-size: 14px;
      color: #495057;
    }

    .btn.liked {
      background: #28a745;
      color: white;
    }

    .no-posts {
      text-align: center;
      padding: 40px 20px;
      color: #6c757d;
    }

    .feed-mode-switcher {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
      border-bottom: 2px solid #dee2e6;
      padding-bottom: 0;
    }

    .mode-btn {
      padding: 12px 24px;
      border: none;
      background: transparent;
      color: #6c757d;
      cursor: pointer;
      font-size: 16px;
      font-weight: 500;
      border-bottom: 3px solid transparent;
      transition: all 0.3s;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .mode-btn:hover {
      color: #007acc;
      background: #f8f9fa;
    }

    .mode-btn.active {
      color: #007acc;
      border-bottom-color: #007acc;
    }

    .mode-btn i {
      font-size: 18px;
    }

    .recommended-badge {
      display: inline-block;
      margin-left: 8px;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 11px;
      font-weight: 600;
      text-transform: uppercase;
    }

    .badge-high {
      background: #d4edda;
      color: #155724;
    }

    .badge-medium {
      background: #d1ecf1;
      color: #0c5460;
    }

    .badge-low {
      background: #fff3cd;
      color: #856404;
    }

    .post-score {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      padding: 8px 12px;
      background: #f8f9fa;
      border-radius: 6px;
      border-left: 4px solid;
    }

    .post-score.high-score {
      border-left-color: #28a745;
      background: #d4edda;
    }

    .post-score.medium-score {
      border-left-color: #17a2b8;
      background: #d1ecf1;
    }

    .post-score.low-score {
      border-left-color: #ffc107;
      background: #fff3cd;
    }

    .score-value {
      font-size: 18px;
      font-weight: bold;
    }

    .score-label {
      font-size: 13px;
      color: #6c757d;
    }

    .post-reasons {
      margin: 12px 0;
      padding: 10px;
      background: #e7f3ff;
      border-radius: 6px;
      border-left: 3px solid #007acc;
    }

    .post-reasons h4 {
      margin: 0 0 8px 0;
      font-size: 13px;
      color: #495057;
      font-weight: 600;
    }

    .reasons-list {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .reasons-list li {
      padding: 4px 0;
      font-size: 13px;
      color: #495057;
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .reasons-list li:before {
      content: "✓";
      color: #007acc;
      font-weight: bold;
    }
  `]
})
export class FriendsFeedComponent implements OnInit {
  private postsSvc = inject(PostsService);
  private authSvc = inject(AuthService);

  // Feed mode
  feedMode = signal<FeedMode>('friends');
  
  // Data
  posts = signal<Post[]>([]);
  friendsFeedData = signal<Post[]>([]);
  recommendedFeedData = signal<RecommendedPost[]>([]);

  newContent = '';
  newHashtags: string[] = [];

  ngOnInit(): void {
    this.load();
  }

  switchMode(mode: FeedMode) {
    this.feedMode.set(mode);
    this.load();
  }

  load() {
    const userId = this.authSvc.currentUserValue?.id;
    if (!userId) {
      console.error('No user logged in');
      return;
    }

    if (this.feedMode() === 'friends') {
      this.loadFriendsFeed(userId.toString());
    } else {
      this.loadRecommendedFeed(userId.toString());
    }
  }

  private loadFriendsFeed(userId: string) {
    console.log('Loading friends feed for user:', userId);
    this.postsSvc.getFriendsFeed(userId).subscribe({
      next: (data) => {
        console.log('Friends feed data:', data);
        this.friendsFeedData.set(data);
      },
      error: (error) => {
        console.error('Error loading friends feed:', error);
      }
    });
  }

  private loadRecommendedFeed(userId: string) {
    console.log('Loading recommended feed for user:', userId);
    this.postsSvc.getRecommendedFeed(userId).subscribe({
      next: (data) => {
        console.log('Recommended feed data:', data);
        this.recommendedFeedData.set(data);
      },
      error: (error) => {
        console.error('Error loading recommended feed:', error);
      }
    });
  }

  create() {
    const content = this.newContent?.trim();
    if (!content) return;

    const request: CreatePostRequest = {
      content,
      hashtags: this.newHashtags.length > 0 ? this.newHashtags : undefined
    };

    this.postsSvc.create(request).subscribe(_ => {
      this.newContent = '';
      this.newHashtags = [];
      this.load();
    });
  }

  like(postId: number) {
    this.postsSvc.like(postId).subscribe({
      next: (res) => {
        console.log('Liked post:', res);
        this.load(); // Reload to update like status
      },
      error: (error) => {
        console.error('Error liking post:', error);
      }
    });
  }

  report(postId: number) {
    if (!confirm('Da li ste sigurni da želite da prijavite ovu objavu?')) {
      return;
    }

    this.postsSvc.report(postId).subscribe({
      next: () => {
        alert('Objava je prijavljena');
        this.load();
      },
      error: () => {
        alert('Greška pri prijavljivanju objave');
      }
    });
  }

  getScoreClass(score: number): string {
    if (score >= 70) return 'high-score';
    if (score >= 40) return 'medium-score';
    return 'low-score';
  }

  getScoreBadgeClass(score: number): string {
    if (score >= 70) return 'badge-high';
    if (score >= 40) return 'badge-medium';
    return 'badge-low';
  }

  getScoreLabel(score: number): string {
    if (score >= 70) return 'Visoko preporučeno';
    if (score >= 40) return 'Preporučeno';
    return 'Možda vas zanima';
  }

  trackByPostId(index: number, item: Post): number {
    return item.id;
  }

  trackByRecommendedId(index: number, item: RecommendedPost): number {
    return item.post.id;
  }

  formatDate(dateValue: any): string {
    if (!dateValue) return '';

    try {
      let date: Date;

      if (typeof dateValue === 'string') {
        date = new Date(dateValue);
      } else if (dateValue instanceof Date) {
        date = dateValue;
      } else if (typeof dateValue === 'object' && dateValue !== null) {
        if (dateValue.year && dateValue.monthValue && dateValue.dayOfMonth) {
          date = new Date(
            dateValue.year,
            dateValue.monthValue - 1,
            dateValue.dayOfMonth,
            dateValue.hour || 0,
            dateValue.minute || 0,
            dateValue.second || 0,
            (dateValue.nano || 0) / 1000000
          );
        } else {
          console.warn('Unknown date object structure:', dateValue);
          return 'Nepoznat format datuma';
        }
      } else {
        return String(dateValue);
      }

      if (isNaN(date.getTime())) {
        console.warn('Invalid date parsed:', dateValue);
        return 'Nevaljan datum';
      }

      return date.toLocaleString('sr-RS', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      console.error('Date formatting error:', error, dateValue);
      return 'Greška u datumu';
    }
  }
}

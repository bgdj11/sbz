import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PostsService, CreatePostRequest } from '../../../core/services/posts.service';
import { Post } from '../../../core/models/post.model';
import { HashtagInputComponent } from '../../../shared/hashtag-input/hashtag-input.component';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, HashtagInputComponent],
  templateUrl: './my-posts.component.html',
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
  `]
})
export class MyPostsComponent implements OnInit {
  private postsSvc = inject(PostsService);

  posts = signal<Post[]>([]);

  newContent = '';
  newHashtags: string[] = [];

  ngOnInit(): void {
    this.load();
  }

  load() {
    console.log('Loading my posts...');
    this.postsSvc.my().subscribe({
      next: (p) => {
        console.log('Raw posts from API:', p);

        // Ensure each post has the required properties
        const postsWithDefaults = p.map(post => ({
          ...post,
          likesCount: post.likesCount ?? post.likedByUsers?.length ?? 0,
          liked: post.liked ?? false,
          liking: false
        }));

        console.log('Processed posts:', postsWithDefaults);
        this.posts.set(postsWithDefaults);
      },
      error: (error) => {
        console.error('Error loading my posts:', error);
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

  like(p: Post) {
    // Prevent multiple simultaneous like requests
    const anyP = p as any;
    if (anyP.liking) return;

    anyP.liking = true;

    this.postsSvc.like(p.id).subscribe({
      next: (res) => {
        anyP.likesCount = res.count;
        anyP.liked = res.liked;
        anyP.liking = false;
      },
      error: () => {
        anyP.liking = false;
      }
    });
  }

  report(p: Post) {
    this.postsSvc.report(p.id).subscribe({
      next: () => {
        alert('Objava je prijavljena');
      },
      error: () => {
        alert('Greška pri prijavljivanju objave');
      }
    });
  }

  isLiked(p: Post): boolean {
    return (p as any)?.liked ?? false;
  }

  likesCount(p: Post): number {
    const anyP = p as any;
    if (typeof anyP?.likesCount === 'number') return anyP.likesCount;
    const arrLen = p.likedByUsers?.length;
    return typeof arrLen === 'number' ? arrLen : 0;
  }

  trackByPostId(index: number, post: Post): number {
    return post.id;
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
          // Create date from Java LocalDateTime structure
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

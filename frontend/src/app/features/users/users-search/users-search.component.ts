import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsersService } from '../../../core/services/users.service';
import { User } from '../../../core/models/user.model';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users-search.component.html',
  styles: [`
    .search-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
    }

    .search-container h2 {
      margin-bottom: 30px;
      text-align: center;
      color: #333;
      font-size: 24px;
    }

    .search-section {
      margin-bottom: 30px;
    }

    .search-form {
      margin-bottom: 30px;
    }

    .search-input-group {
      display: flex;
      gap: 12px;
      align-items: center;
    }

    .search-input {
      flex: 1;
      padding: 12px 16px;
      border: 2px solid #e1e5e9;
      border-radius: 8px;
      font-size: 16px;
      box-sizing: border-box;
    }

    .search-input:focus {
      outline: none;
      border-color: #007acc;
      box-shadow: 0 0 0 3px rgba(0, 122, 204, 0.1);
    }

    .search-btn {
      padding: 12px 20px;
      white-space: nowrap;
    }

    .btn {
      padding: 8px 16px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.2s ease;
    }

    .btn-primary {
      background: #007acc;
      color: white;
    }

    .btn-primary:hover {
      background: #005a9a;
    }

    .btn-success {
      background: #28a745;
      color: white;
    }

    .btn-success:hover {
      background: #1e7e34;
    }

    .btn-warning {
      background: #ffc107;
      color: #212529;
    }

    .btn-warning:hover {
      background: #e0a800;
    }

    .no-results {
      text-align: center;
      padding: 40px 20px;
      color: #666;
      background: #f8f9fa;
      border-radius: 8px;
      border: 1px dashed #dee2e6;
    }

    .results-section h3 {
      margin-bottom: 20px;
      color: #495057;
      font-size: 18px;
    }

    .users-grid {
      display: grid;
      gap: 16px;
    }

    .user-card {
      background: white;
      border: 1px solid #dee2e6;
      border-radius: 8px;
      padding: 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      transition: box-shadow 0.2s ease;
    }

    .user-card:hover {
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 16px;
      flex: 1;
    }

    .user-avatar {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 16px;
      text-transform: uppercase;
    }

    .user-details h4 {
      margin: 0 0 4px 0;
      color: #333;
      font-size: 16px;
      font-weight: 600;
    }

    .user-email {
      margin: 0 0 4px 0;
      color: #666;
      font-size: 14px;
    }

    .user-city {
      margin: 0;
      color: #888;
      font-size: 13px;
    }

    .user-actions {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
    }

    @media (max-width: 768px) {
      .search-input-group {
        flex-direction: column;
      }

      .search-input {
        width: 100%;
      }

      .search-btn {
        width: 100%;
      }

      .user-card {
        flex-direction: column;
        gap: 16px;
        text-align: center;
      }

      .user-info {
        flex-direction: column;
        text-align: center;
      }

      .user-actions {
        width: 100%;
        justify-content: center;
      }
    }
  `]
})
export class UsersSearchComponent {
  private usersSvc = inject(UsersService);
  q = '';
  results = signal<User[]>([]);
  hasSearched = false;

  doSearch() {
    const term = this.q.trim();
    if (!term) { 
      this.results.set([]);
      this.hasSearched = false;
      return; 
    }
    
    this.hasSearched = true;
    this.usersSvc.search(term).subscribe({
      next: (res) => this.results.set(res ?? []),
      error: (error) => {
        console.error('Search error:', error);
        this.results.set([]);
      }
    });
  }

  addFriend(u: User) {
    this.usersSvc.addFriend(u.id).subscribe({
      next: () => {
        alert(`Zahtev za prijateljstvo poslat korisniku ${u.firstName} ${u.lastName}`);
      },
      error: (error) => {
        console.error('Add friend error:', error);
        alert('Greška pri slanju zahteva za prijateljstvo');
      }
    });
  }

  block(u: User) {
    this.usersSvc.block(u.id).subscribe({
      next: () => {
        alert(`Korisnik ${u.firstName} ${u.lastName} je blokiran`);
        // Remove user from results
        const currentResults = this.results();
        this.results.set(currentResults.filter(user => user.id !== u.id));
      },
      error: (error) => {
        console.error('Block user error:', error);
        alert('Greška pri blokiranju korisnika');
      }
    });
  }
}

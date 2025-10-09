import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModerationService, SuspiciousUser } from '../../../core/services/moderation.service';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-suspicious-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './suspicious-users.component.html',
  styleUrls: ['./suspicious-users.component.scss']
})
export class SuspiciousUsersComponent implements OnInit {
  suspiciousUsers: SuspiciousUser[] = [];
  isLoading = false;
  error: string | null = null;
  detectedAt: Date | null = null;
  isAdmin = false;

  constructor(
    private moderationService: ModerationService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.isAdmin = user.admin ?? false;
      if (!this.isAdmin) {
        this.router.navigate(['/']);
      }
    } else {
      this.router.navigate(['/auth/login']);
    }
  }

  detectSuspiciousUsers(): void {
    this.isLoading = true;
    this.error = null;
    this.suspiciousUsers = []; // Reset liste
    
    console.log('🔍 Pozivam detekciju sumljivih korisnika...');

    this.moderationService.detectSuspiciousUsers().subscribe({
      next: (response) => {
        console.log('✅ Response primljen:', response);
        
        // Eksplicitno postavljanje vrednosti
        this.suspiciousUsers = response.suspiciousUsers || [];
        this.detectedAt = new Date(response.detectedAt);
        this.isLoading = false;
        
        console.log(`✅ Učitano ${this.suspiciousUsers.length} sumljivih korisnika`);
        console.log('isLoading postavljen na:', this.isLoading);
        
        // Forsiraj Change Detection
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error detecting suspicious users:', err);
        this.error = err.error?.error || 'Greška pri detekciji sumljivih korisnika';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getSuspensionTypeBadgeClass(type: string): string {
    return type === 'LOGIN' ? 'badge-danger' : 'badge-warning';
  }

  getSuspensionTypeText(type: string): string {
    return type === 'LOGIN' ? 'Zabrana logovanja' : 'Zabrana postovanja';
  }

  getSeverityClass(reportCount: number, blockCount: number): string {
    const total = reportCount + blockCount;
    if (total > 15) return 'severity-critical';
    if (total > 10) return 'severity-high';
    if (total > 5) return 'severity-medium';
    return 'severity-low';
  }

  formatDate(date: Date | number): string {
    try {
      const dateObj = typeof date === 'number' ? new Date(date) : date;
      return dateObj.toLocaleString('sr-RS', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (e) {
      console.error('Error formatting date:', date, e);
      return 'Nepoznato';
    }
  }

  getTimeRemaining(suspendedUntil: Date | number): string {
    try {
      const now = new Date();
      const end = typeof suspendedUntil === 'number' ? new Date(suspendedUntil) : suspendedUntil;
      const diff = end.getTime() - now.getTime();

      if (diff <= 0) {
        return 'Suspenzija istekla';
      }

      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

      if (hours > 48) {
        const days = Math.floor(hours / 24);
        return `${days} dana`;
      }

      return `${hours}h ${minutes}min`;
    } catch (e) {
      console.error('Error calculating time remaining:', suspendedUntil, e);
      return 'Nepoznato';
    }
  }

  applySuspension(user: SuspiciousUser): void {
    if (confirm(`Da li ste sigurni da želite da primjenite kaznu za korisnika ${user.firstName} ${user.lastName}?`)) {
      this.moderationService.applySuspension(
        user.userId,
        user.suspensionType,
        new Date(user.suspendedUntil)
      ).subscribe({
        next: () => {
          alert('Kazna uspješno primjenjena!');
        },
        error: (err) => {
          console.error('Error applying suspension:', err);
          alert('Greška pri primjeni kazne: ' + (err.error?.error || 'Nepoznata greška'));
        }
      });
    }
  }
}

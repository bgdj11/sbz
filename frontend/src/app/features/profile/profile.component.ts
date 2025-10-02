import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'app-profile',
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  private auth = inject(AuthService);
  user = signal<User | null>(null);

  ngOnInit(): void {
    this.user.set(this.auth.currentUserValue);
  }
}

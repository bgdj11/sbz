import { Component, inject, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  auth = inject(AuthService);
  router = inject(Router);

  ngOnInit() {
    // Don't initialize auth automatically since /me endpoint doesn't exist
    // Authentication will be managed through login/logout flows
  }

  onLogout() {
    this.auth.logout().subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: () => {
        // Even if server logout fails, redirect to login
        this.router.navigateByUrl('/login');
      }
    });
  }
}

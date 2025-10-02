import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styles: [`
    .register-container {
      min-height: calc(100vh - 100px);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .register-card {
      background: white;
      border-radius: 12px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
      padding: 40px;
      width: 100%;
      max-width: 500px;
    }

    .register-card h2 {
      margin: 0 0 30px 0;
      text-align: center;
      color: #333;
      font-size: 28px;
      font-weight: 600;
    }

    .register-form {
      width: 100%;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
      margin-bottom: 20px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .form-group label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
      color: #555;
      font-size: 14px;
    }

    .form-control {
      width: 100%;
      padding: 14px 16px;
      border: 2px solid #e1e5e9;
      border-radius: 8px;
      font-size: 16px;
      box-sizing: border-box;
      transition: border-color 0.3s ease, box-shadow 0.3s ease;
    }

    .form-control:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .form-control::placeholder {
      color: #999;
    }

    .form-control.ng-invalid.ng-touched {
      border-color: #dc3545;
    }

    .error-message {
      margin: 15px 0;
      padding: 12px 16px;
      background: #fee;
      color: #c33;
      border: 1px solid #fcc;
      border-radius: 6px;
      font-size: 14px;
      text-align: center;
    }

    .form-actions {
      margin: 25px 0 20px 0;
    }

    .btn {
      display: inline-block;
      padding: 12px 24px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-size: 16px;
      font-weight: 500;
      text-decoration: none;
      transition: all 0.3s ease;
    }

    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }

    .btn-primary:disabled {
      background: #ccc;
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }

    .btn-large {
      width: 100%;
      padding: 16px 24px;
      font-size: 18px;
    }

    .form-footer {
      text-align: center;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #eee;
    }

    .form-footer p {
      margin: 0;
      color: #666;
      font-size: 14px;
    }

    .link {
      color: #667eea;
      text-decoration: none;
      font-weight: 500;
    }

    .link:hover {
      text-decoration: underline;
    }

    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
        gap: 0;
      }

      .register-container {
        padding: 10px;
        align-items: flex-start;
        padding-top: 40px;
      }

      .register-card {
        padding: 30px 20px;
      }

      .register-card h2 {
        font-size: 24px;
      }
    }
  `]
})
export class RegisterComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  firstName = '';
  lastName = '';
  email = '';
  city = '';
  password = '';
  confirm = '';

  error = signal<string | null>(null);
  loading = signal(false);

  submit(form: NgForm) {
    this.error.set(null);

    if (form.invalid) {
      this.error.set('Popunite sva obavezna polja.');
      return;
    }
    if (this.password !== this.confirm) {
      this.error.set('Lozinke se ne poklapaju.');
      return;
    }

    this.loading.set(true);
    this.auth.register({
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      password: this.password,
      city: this.city || undefined
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigateByUrl('/login');
      },
      error: (e) => {
        this.loading.set(false);
        this.error.set(e?.error?.error || 'Registracija nije uspela.');
      }
    });
  }
}

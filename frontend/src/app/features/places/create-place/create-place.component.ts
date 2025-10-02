import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlacesService, CreatePlaceRequest } from '../../../core/services/places.service';
import { Router } from '@angular/router';
import { HashtagInputComponent } from '../../../shared/hashtag-input/hashtag-input.component';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, HashtagInputComponent],
  templateUrl: './create-place.component.html',
  styles: [`
    .create-place-form {
      max-width: 600px;
      margin: 0 auto;
      padding: 24px;
      background: #f8f9fa;
      border: 1px solid #dee2e6;
      border-radius: 8px;
    }
    
    .form-group {
      margin-bottom: 20px;
    }
    
    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
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
      text-align: center;
      margin-top: 24px;
    }
    
    .btn {
      padding: 12px 24px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 16px;
      font-weight: 500;
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
    
    .error-message {
      margin-top: 12px;
      padding: 8px 12px;
      background: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
      border-radius: 4px;
      font-size: 14px;
    }
    
    .form-hint {
      margin-top: 16px;
      text-align: center;
    }
    
    .form-hint small {
      color: #6c757d;
      font-size: 12px;
    }
    
    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
      }
      
      .create-place-form {
        margin: 0 16px;
        padding: 16px;
      }
    }
  `]
})
export class CreatePlaceComponent {
  private placesSvc = inject(PlacesService);
  private router = inject(Router);

  name = '';
  country = '';
  city = '';
  description = '';
  hashtags: string[] = [];
  error = signal<string | null>(null);

  isFormValid(): boolean {
    return !!(this.name?.trim() && this.country?.trim() && this.city?.trim());
  }

  submit() {
    if (!this.isFormValid()) {
      this.error.set('Sva obavezna polja moraju biti popunjena.');
      return;
    }

    const request: CreatePlaceRequest = {
      name: this.name.trim(),
      country: this.country.trim(),
      city: this.city.trim(),
      description: this.description?.trim() || undefined,
      hashtags: this.hashtags.length > 0 ? this.hashtags : undefined
    };

    this.placesSvc.create(request).subscribe({
      next: () => {
        this.router.navigateByUrl('/places');
      },
      error: (e) => {
        const errorMsg = e?.error?.error || e?.error?.message || 'Greška pri kreiranju mesta';
        this.error.set(errorMsg);
      }
    });
  }
}

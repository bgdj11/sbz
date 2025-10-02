import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-hashtag-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => HashtagInputComponent),
      multi: true
    }
  ],
  template: `
    <div class="hashtag-container">
      <label class="hashtag-label">{{ label }}</label>
      <div class="hashtag-input-wrapper">
        <input 
          type="text" 
          [(ngModel)]="inputValue" 
          (keydown)="onKeyDown($event)"
          (blur)="onInputBlur()"
          [placeholder]="placeholder"
          class="hashtag-input"
        />
        <small class="hashtag-hint">Pritisni Enter ili zarez da dodaš hashtag</small>
      </div>
      <div class="hashtags-display" *ngIf="hashtags.length > 0">
        <span 
          *ngFor="let tag of hashtags; let i = index" 
          class="hashtag-tag"
        >
          #{{ tag }}
          <button 
            type="button" 
            (click)="removeTag(i)"
            class="remove-tag"
            aria-label="Ukloni hashtag"
          >×</button>
        </span>
      </div>
    </div>
  `,
  styles: [`
    .hashtag-container {
      margin: 10px 0;
    }
    
    .hashtag-label {
      display: block;
      font-weight: 500;
      margin-bottom: 5px;
      color: #333;
    }
    
    .hashtag-input-wrapper {
      position: relative;
    }
    
    .hashtag-input {
      width: 100%;
      padding: 8px 12px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 14px;
      box-sizing: border-box;
    }
    
    .hashtag-input:focus {
      outline: none;
      border-color: #007acc;
      box-shadow: 0 0 0 2px rgba(0, 122, 204, 0.2);
    }
    
    .hashtag-hint {
      display: block;
      margin-top: 4px;
      color: #666;
      font-size: 12px;
    }
    
    .hashtags-display {
      margin-top: 8px;
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }
    
    .hashtag-tag {
      display: inline-flex;
      align-items: center;
      background: #e3f2fd;
      color: #1976d2;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }
    
    .remove-tag {
      background: none;
      border: none;
      color: #1976d2;
      margin-left: 4px;
      cursor: pointer;
      font-size: 14px;
      font-weight: bold;
      padding: 0;
      width: 16px;
      height: 16px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .remove-tag:hover {
      background: #bbdefb;
    }
  `]
})
export class HashtagInputComponent implements ControlValueAccessor {
  @Input() label: string = 'Hashtags';
  @Input() placeholder: string = 'Dodaj hashtags...';
  
  hashtags: string[] = [];
  inputValue: string = '';
  
  private onChange = (value: string[]) => {};
  private onTouched = () => {};

  writeValue(value: string[]): void {
    this.hashtags = value || [];
  }

  registerOnChange(fn: (value: string[]) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' || event.key === ',') {
      event.preventDefault();
      this.addTag();
    }
  }

  onInputBlur(): void {
    this.addTag();
    this.onTouched();
  }

  private addTag(): void {
    const tag = this.inputValue.trim().toLowerCase().replace(/^#+/, '');
    if (tag && !this.hashtags.includes(tag) && this.hashtags.length < 10) {
      this.hashtags.push(tag);
      this.inputValue = '';
      this.onChange(this.hashtags);
    }
  }

  removeTag(index: number): void {
    this.hashtags.splice(index, 1);
    this.onChange(this.hashtags);
  }
}
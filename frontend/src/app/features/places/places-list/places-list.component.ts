import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlacesService } from '../../../core/services/places.service';
import { Place } from '../../../core/models/place.model';

@Component({
  standalone: true,
  imports: [CommonModule],
  templateUrl: './places-list.component.html'
})
export class PlacesListComponent implements OnInit {
  private placesSvc = inject(PlacesService);
  places = signal<Place[]>([]);

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.placesSvc.list().subscribe(p => this.places.set(p));
  }

  rateQuick(pl: Place, score: string) {
    const s = parseInt(score, 10);
    if (!s || s < 1 || s > 5) return;
    this.placesSvc.rate(pl.id as any, s, 'Brza ocena').subscribe(() => {

    });
  }
}

import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'posts/friends', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },

  { path: 'profile', canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent) },

  { path: 'posts/me', canActivate: [authGuard],
    loadComponent: () => import('./features/posts/my-posts/my-posts.component').then(m => m.MyPostsComponent) },

  { path: 'posts/friends', canActivate: [authGuard],
    loadComponent: () => import('./features/posts/friends-feed/friends-feed.component').then(m => m.FriendsFeedComponent) },

  { path: 'places', canActivate: [authGuard],
    loadComponent: () => import('./features/places/places-list/places-list.component').then(m => m.PlacesListComponent) },

  { path: 'places/new', canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/places/create-place/create-place.component').then(m => m.CreatePlaceComponent) },

  { path: 'users/search', canActivate: [authGuard],
    loadComponent: () => import('./features/users/users-search/users-search.component').then(m => m.UsersSearchComponent) },

  { path: '**', redirectTo: 'posts/friends' }
];

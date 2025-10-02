import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  const modifiedReq = req.clone({
    setHeaders: {
      'Cache-Control': 'no-cache',
      'Pragma': 'no-cache'
    },
    withCredentials: true
  });

  return next(modifiedReq).pipe(
    catchError((error) => {
      if (error.status === 401 && !req.url.includes('/auth/')) {
        const currentUrl = router.url;
        if (currentUrl !== '/login' && currentUrl !== '/register') {
          console.log('Unauthorized access, redirecting to login');
          router.navigate(['/login']);
        }
      }

      // Always pass through the error, don't consume it
      return throwError(() => error);
    })
  );
};

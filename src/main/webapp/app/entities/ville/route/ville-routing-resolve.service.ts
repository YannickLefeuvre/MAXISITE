import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVille, Ville } from '../ville.model';
import { VilleService } from '../service/ville.service';

@Injectable({ providedIn: 'root' })
export class VilleRoutingResolveService implements Resolve<IVille> {
  constructor(protected service: VilleService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVille> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ville: HttpResponse<Ville>) => {
          if (ville.body) {
            return of(ville.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ville());
  }
}

import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILien, Lien } from '../lien.model';
import { LienService } from '../service/lien.service';

@Injectable({ providedIn: 'root' })
export class LienRoutingResolveService implements Resolve<ILien> {
  constructor(protected service: LienService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILien> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((lien: HttpResponse<Lien>) => {
          if (lien.body) {
            return of(lien.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Lien());
  }
}

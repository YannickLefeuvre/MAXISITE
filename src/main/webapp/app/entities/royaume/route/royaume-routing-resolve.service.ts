import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRoyaume, Royaume } from '../royaume.model';
import { RoyaumeService } from '../service/royaume.service';

@Injectable({ providedIn: 'root' })
export class RoyaumeRoutingResolveService implements Resolve<IRoyaume> {
  constructor(protected service: RoyaumeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRoyaume> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((royaume: HttpResponse<Royaume>) => {
          if (royaume.body) {
            return of(royaume.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Royaume());
  }
}

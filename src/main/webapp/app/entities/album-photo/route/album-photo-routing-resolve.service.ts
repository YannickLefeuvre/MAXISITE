import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAlbumPhoto, AlbumPhoto } from '../album-photo.model';
import { AlbumPhotoService } from '../service/album-photo.service';

@Injectable({ providedIn: 'root' })
export class AlbumPhotoRoutingResolveService implements Resolve<IAlbumPhoto> {
  constructor(protected service: AlbumPhotoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAlbumPhoto> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((albumPhoto: HttpResponse<AlbumPhoto>) => {
          if (albumPhoto.body) {
            return of(albumPhoto.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AlbumPhoto());
  }
}

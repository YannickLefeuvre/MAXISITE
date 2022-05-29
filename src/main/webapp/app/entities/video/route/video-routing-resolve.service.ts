import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVideo, Video } from '../video.model';
import { VideoService } from '../service/video.service';

@Injectable({ providedIn: 'root' })
export class VideoRoutingResolveService implements Resolve<IVideo> {
  constructor(protected service: VideoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVideo> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((video: HttpResponse<Video>) => {
          if (video.body) {
            return of(video.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Video());
  }
}

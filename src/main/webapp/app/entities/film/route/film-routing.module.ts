import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FilmComponent } from '../list/film.component';
import { FilmDetailComponent } from '../detail/film-detail.component';
import { FilmUpdateComponent } from '../update/film-update.component';
import { FilmRoutingResolveService } from './film-routing-resolve.service';

const filmRoute: Routes = [
  {
    path: '',
    component: FilmComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FilmDetailComponent,
    resolve: {
      film: FilmRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FilmUpdateComponent,
    resolve: {
      film: FilmRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FilmUpdateComponent,
    resolve: {
      film: FilmRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(filmRoute)],
  exports: [RouterModule],
})
export class FilmRoutingModule {}

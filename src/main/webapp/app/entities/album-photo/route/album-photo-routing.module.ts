import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AlbumPhotoComponent } from '../list/album-photo.component';
import { AlbumPhotoDetailComponent } from '../detail/album-photo-detail.component';
import { AlbumPhotoUpdateComponent } from '../update/album-photo-update.component';
import { AlbumPhotoRoutingResolveService } from './album-photo-routing-resolve.service';

const albumPhotoRoute: Routes = [
  {
    path: '',
    component: AlbumPhotoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AlbumPhotoDetailComponent,
    resolve: {
      albumPhoto: AlbumPhotoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AlbumPhotoUpdateComponent,
    resolve: {
      albumPhoto: AlbumPhotoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AlbumPhotoUpdateComponent,
    resolve: {
      albumPhoto: AlbumPhotoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(albumPhotoRoute)],
  exports: [RouterModule],
})
export class AlbumPhotoRoutingModule {}

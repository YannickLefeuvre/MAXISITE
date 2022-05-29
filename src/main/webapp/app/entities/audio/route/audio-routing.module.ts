import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AudioComponent } from '../list/audio.component';
import { AudioDetailComponent } from '../detail/audio-detail.component';
import { AudioUpdateComponent } from '../update/audio-update.component';
import { AudioRoutingResolveService } from './audio-routing-resolve.service';

const audioRoute: Routes = [
  {
    path: '',
    component: AudioComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AudioDetailComponent,
    resolve: {
      audio: AudioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AudioUpdateComponent,
    resolve: {
      audio: AudioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AudioUpdateComponent,
    resolve: {
      audio: AudioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(audioRoute)],
  exports: [RouterModule],
})
export class AudioRoutingModule {}

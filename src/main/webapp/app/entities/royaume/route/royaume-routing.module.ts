import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RoyaumeComponent } from '../list/royaume.component';
import { RoyaumeDetailComponent } from '../detail/royaume-detail.component';
import { RoyaumeUpdateComponent } from '../update/royaume-update.component';
import { RoyaumeRoutingResolveService } from './royaume-routing-resolve.service';

const royaumeRoute: Routes = [
  {
    path: '',
    component: RoyaumeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RoyaumeDetailComponent,
    resolve: {
      royaume: RoyaumeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RoyaumeUpdateComponent,
    resolve: {
      royaume: RoyaumeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RoyaumeUpdateComponent,
    resolve: {
      royaume: RoyaumeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(royaumeRoute)],
  exports: [RouterModule],
})
export class RoyaumeRoutingModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LienComponent } from '../list/lien.component';
import { LienDetailComponent } from '../detail/lien-detail.component';
import { LienUpdateComponent } from '../update/lien-update.component';
import { LienRoutingResolveService } from './lien-routing-resolve.service';

const lienRoute: Routes = [
  {
    path: '',
    component: LienComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LienDetailComponent,
    resolve: {
      lien: LienRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LienUpdateComponent,
    resolve: {
      lien: LienRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LienUpdateComponent,
    resolve: {
      lien: LienRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(lienRoute)],
  exports: [RouterModule],
})
export class LienRoutingModule {}

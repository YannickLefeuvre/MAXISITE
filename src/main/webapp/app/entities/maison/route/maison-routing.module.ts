import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MaisonComponent } from '../list/maison.component';
import { MaisonDetailComponent } from '../detail/maison-detail.component';
import { MaisonUpdateComponent } from '../update/maison-update.component';
import { MaisonRoutingResolveService } from './maison-routing-resolve.service';

const maisonRoute: Routes = [
  {
    path: '',
    component: MaisonComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MaisonDetailComponent,
    resolve: {
      maison: MaisonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MaisonUpdateComponent,
    resolve: {
      maison: MaisonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MaisonUpdateComponent,
    resolve: {
      maison: MaisonRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(maisonRoute)],
  exports: [RouterModule],
})
export class MaisonRoutingModule {}

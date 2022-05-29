import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LienComponent } from './list/lien.component';
import { LienDetailComponent } from './detail/lien-detail.component';
import { LienUpdateComponent } from './update/lien-update.component';
import { LienDeleteDialogComponent } from './delete/lien-delete-dialog.component';
import { LienRoutingModule } from './route/lien-routing.module';

@NgModule({
  imports: [SharedModule, LienRoutingModule],
  declarations: [LienComponent, LienDetailComponent, LienUpdateComponent, LienDeleteDialogComponent],
  entryComponents: [LienDeleteDialogComponent],
})
export class LienModule {}

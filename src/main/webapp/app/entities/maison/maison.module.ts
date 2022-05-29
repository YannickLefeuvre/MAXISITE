import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MaisonComponent } from './list/maison.component';
import { MaisonDetailComponent } from './detail/maison-detail.component';
import { MaisonUpdateComponent } from './update/maison-update.component';
import { MaisonDeleteDialogComponent } from './delete/maison-delete-dialog.component';
import { MaisonRoutingModule } from './route/maison-routing.module';

@NgModule({
  imports: [SharedModule, MaisonRoutingModule],
  declarations: [MaisonComponent, MaisonDetailComponent, MaisonUpdateComponent, MaisonDeleteDialogComponent],
  entryComponents: [MaisonDeleteDialogComponent],
})
export class MaisonModule {}

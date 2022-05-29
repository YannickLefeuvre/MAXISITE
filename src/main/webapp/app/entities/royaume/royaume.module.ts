import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RoyaumeComponent } from './list/royaume.component';
import { RoyaumeDetailComponent } from './detail/royaume-detail.component';
import { RoyaumeUpdateComponent } from './update/royaume-update.component';
import { RoyaumeDeleteDialogComponent } from './delete/royaume-delete-dialog.component';
import { RoyaumeRoutingModule } from './route/royaume-routing.module';

@NgModule({
  imports: [SharedModule, RoyaumeRoutingModule],
  declarations: [RoyaumeComponent, RoyaumeDetailComponent, RoyaumeUpdateComponent, RoyaumeDeleteDialogComponent],
  entryComponents: [RoyaumeDeleteDialogComponent],
})
export class RoyaumeModule {}

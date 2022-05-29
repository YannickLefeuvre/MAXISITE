import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AudioComponent } from './list/audio.component';
import { AudioDetailComponent } from './detail/audio-detail.component';
import { AudioUpdateComponent } from './update/audio-update.component';
import { AudioDeleteDialogComponent } from './delete/audio-delete-dialog.component';
import { AudioRoutingModule } from './route/audio-routing.module';

@NgModule({
  imports: [SharedModule, AudioRoutingModule],
  declarations: [AudioComponent, AudioDetailComponent, AudioUpdateComponent, AudioDeleteDialogComponent],
  entryComponents: [AudioDeleteDialogComponent],
})
export class AudioModule {}

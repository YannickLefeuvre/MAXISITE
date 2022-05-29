import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AlbumPhotoComponent } from './list/album-photo.component';
import { AlbumPhotoDetailComponent } from './detail/album-photo-detail.component';
import { AlbumPhotoUpdateComponent } from './update/album-photo-update.component';
import { AlbumPhotoDeleteDialogComponent } from './delete/album-photo-delete-dialog.component';
import { AlbumPhotoRoutingModule } from './route/album-photo-routing.module';

@NgModule({
  imports: [SharedModule, AlbumPhotoRoutingModule],
  declarations: [AlbumPhotoComponent, AlbumPhotoDetailComponent, AlbumPhotoUpdateComponent, AlbumPhotoDeleteDialogComponent],
  entryComponents: [AlbumPhotoDeleteDialogComponent],
})
export class AlbumPhotoModule {}

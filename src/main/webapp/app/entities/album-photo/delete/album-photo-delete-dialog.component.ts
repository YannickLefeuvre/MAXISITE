import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAlbumPhoto } from '../album-photo.model';
import { AlbumPhotoService } from '../service/album-photo.service';

@Component({
  templateUrl: './album-photo-delete-dialog.component.html',
})
export class AlbumPhotoDeleteDialogComponent {
  albumPhoto?: IAlbumPhoto;

  constructor(protected albumPhotoService: AlbumPhotoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.albumPhotoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

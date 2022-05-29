import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVideo } from '../video.model';
import { VideoService } from '../service/video.service';

@Component({
  templateUrl: './video-delete-dialog.component.html',
})
export class VideoDeleteDialogComponent {
  video?: IVideo;

  constructor(protected videoService: VideoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.videoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

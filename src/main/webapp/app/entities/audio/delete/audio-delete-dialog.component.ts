import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAudio } from '../audio.model';
import { AudioService } from '../service/audio.service';

@Component({
  templateUrl: './audio-delete-dialog.component.html',
})
export class AudioDeleteDialogComponent {
  audio?: IAudio;

  constructor(protected audioService: AudioService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.audioService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

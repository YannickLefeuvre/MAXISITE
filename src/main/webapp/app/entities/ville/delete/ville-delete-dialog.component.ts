import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVille } from '../ville.model';
import { VilleService } from '../service/ville.service';

@Component({
  templateUrl: './ville-delete-dialog.component.html',
})
export class VilleDeleteDialogComponent {
  ville?: IVille;

  constructor(protected villeService: VilleService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.villeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

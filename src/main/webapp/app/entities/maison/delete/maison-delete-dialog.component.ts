import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMaison } from '../maison.model';
import { MaisonService } from '../service/maison.service';

@Component({
  templateUrl: './maison-delete-dialog.component.html',
})
export class MaisonDeleteDialogComponent {
  maison?: IMaison;

  constructor(protected maisonService: MaisonService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.maisonService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

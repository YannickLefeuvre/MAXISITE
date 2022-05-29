import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILien } from '../lien.model';
import { LienService } from '../service/lien.service';

@Component({
  templateUrl: './lien-delete-dialog.component.html',
})
export class LienDeleteDialogComponent {
  lien?: ILien;

  constructor(protected lienService: LienService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.lienService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

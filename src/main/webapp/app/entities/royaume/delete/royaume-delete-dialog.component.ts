import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRoyaume } from '../royaume.model';
import { RoyaumeService } from '../service/royaume.service';

@Component({
  templateUrl: './royaume-delete-dialog.component.html',
})
export class RoyaumeDeleteDialogComponent {
  royaume?: IRoyaume;

  constructor(protected royaumeService: RoyaumeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.royaumeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

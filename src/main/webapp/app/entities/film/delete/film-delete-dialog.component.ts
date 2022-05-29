import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFilm } from '../film.model';
import { FilmService } from '../service/film.service';

@Component({
  templateUrl: './film-delete-dialog.component.html',
})
export class FilmDeleteDialogComponent {
  film?: IFilm;

  constructor(protected filmService: FilmService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.filmService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

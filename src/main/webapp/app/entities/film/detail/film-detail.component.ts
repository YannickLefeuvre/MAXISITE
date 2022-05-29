import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFilm } from '../film.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-film-detail',
  templateUrl: './film-detail.component.html',
})
export class FilmDetailComponent implements OnInit {
  film: IFilm | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ film }) => {
      this.film = film;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}

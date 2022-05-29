import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISerie } from '../serie.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-serie-detail',
  templateUrl: './serie-detail.component.html',
})
export class SerieDetailComponent implements OnInit {
  serie: ISerie | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ serie }) => {
      this.serie = serie;
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

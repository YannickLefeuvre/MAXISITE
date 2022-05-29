import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILien } from '../lien.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-lien-detail',
  templateUrl: './lien-detail.component.html',
})
export class LienDetailComponent implements OnInit {
  lien: ILien | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lien }) => {
      this.lien = lien;
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

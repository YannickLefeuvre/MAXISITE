import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVille } from '../ville.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ville-detail',
  templateUrl: './ville-detail.component.html',
})
export class VilleDetailComponent implements OnInit {
  ville: IVille | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ville }) => {
      this.ville = ville;
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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMaison } from '../maison.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-maison-detail',
  templateUrl: './maison-detail.component.html',
})
export class MaisonDetailComponent implements OnInit {
  maison: IMaison | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ maison }) => {
      this.maison = maison;
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

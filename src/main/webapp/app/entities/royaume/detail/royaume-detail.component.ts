import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRoyaume } from '../royaume.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-royaume-detail',
  templateUrl: './royaume-detail.component.html',
})
export class RoyaumeDetailComponent implements OnInit {
  royaume: IRoyaume | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ royaume }) => {
      this.royaume = royaume;
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

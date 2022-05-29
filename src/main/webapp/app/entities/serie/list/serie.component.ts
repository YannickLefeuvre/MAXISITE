import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISerie } from '../serie.model';
import { SerieService } from '../service/serie.service';
import { SerieDeleteDialogComponent } from '../delete/serie-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-serie',
  templateUrl: './serie.component.html',
})
export class SerieComponent implements OnInit {
  series?: ISerie[];
  isLoading = false;

  constructor(protected serieService: SerieService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.serieService.query().subscribe({
      next: (res: HttpResponse<ISerie[]>) => {
        this.isLoading = false;
        this.series = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ISerie): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(serie: ISerie): void {
    const modalRef = this.modalService.open(SerieDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.serie = serie;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

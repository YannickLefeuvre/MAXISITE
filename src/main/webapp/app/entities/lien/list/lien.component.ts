import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILien } from '../lien.model';
import { LienService } from '../service/lien.service';
import { LienDeleteDialogComponent } from '../delete/lien-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-lien',
  templateUrl: './lien.component.html',
})
export class LienComponent implements OnInit {
  liens?: ILien[];
  isLoading = false;

  constructor(protected lienService: LienService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.lienService.query().subscribe({
      next: (res: HttpResponse<ILien[]>) => {
        this.isLoading = false;
        this.liens = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ILien): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(lien: ILien): void {
    const modalRef = this.modalService.open(LienDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.lien = lien;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

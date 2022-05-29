import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILivre } from '../livre.model';
import { LivreService } from '../service/livre.service';
import { LivreDeleteDialogComponent } from '../delete/livre-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-livre',
  templateUrl: './livre.component.html',
})
export class LivreComponent implements OnInit {
  livres?: ILivre[];
  isLoading = false;

  constructor(protected livreService: LivreService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.livreService.query().subscribe({
      next: (res: HttpResponse<ILivre[]>) => {
        this.isLoading = false;
        this.livres = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ILivre): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(livre: ILivre): void {
    const modalRef = this.modalService.open(LivreDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.livre = livre;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

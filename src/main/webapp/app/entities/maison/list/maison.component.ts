import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMaison } from '../maison.model';
import { MaisonService } from '../service/maison.service';
import { MaisonDeleteDialogComponent } from '../delete/maison-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-maison',
  templateUrl: './maison.component.html',
})
export class MaisonComponent implements OnInit {
  maisons?: IMaison[];
  isLoading = false;

  constructor(protected maisonService: MaisonService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.maisonService.query().subscribe({
      next: (res: HttpResponse<IMaison[]>) => {
        this.isLoading = false;
        this.maisons = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IMaison): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(maison: IMaison): void {
    const modalRef = this.modalService.open(MaisonDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.maison = maison;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

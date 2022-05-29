import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVille } from '../ville.model';
import { VilleService } from '../service/ville.service';
import { VilleDeleteDialogComponent } from '../delete/ville-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ville',
  templateUrl: './ville.component.html',
})
export class VilleComponent implements OnInit {
  villes?: IVille[];
  isLoading = false;

  constructor(protected villeService: VilleService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.villeService.query().subscribe({
      next: (res: HttpResponse<IVille[]>) => {
        this.isLoading = false;
        this.villes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IVille): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(ville: IVille): void {
    const modalRef = this.modalService.open(VilleDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ville = ville;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

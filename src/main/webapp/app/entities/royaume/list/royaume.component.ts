import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRoyaume } from '../royaume.model';
import { RoyaumeService } from '../service/royaume.service';
import { RoyaumeDeleteDialogComponent } from '../delete/royaume-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-royaume',
  templateUrl: './royaume.component.html',
})
export class RoyaumeComponent implements OnInit {
  royaumes?: IRoyaume[];
  isLoading = false;

  constructor(protected royaumeService: RoyaumeService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.royaumeService.query().subscribe({
      next: (res: HttpResponse<IRoyaume[]>) => {
        this.isLoading = false;
        this.royaumes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IRoyaume): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(royaume: IRoyaume): void {
    const modalRef = this.modalService.open(RoyaumeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.royaume = royaume;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

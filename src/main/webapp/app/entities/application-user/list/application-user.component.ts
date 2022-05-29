import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IApplicationUser } from '../application-user.model';
import { ApplicationUserService } from '../service/application-user.service';
import { ApplicationUserDeleteDialogComponent } from '../delete/application-user-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-application-user',
  templateUrl: './application-user.component.html',
})
export class ApplicationUserComponent implements OnInit {
  applicationUsers?: IApplicationUser[];
  isLoading = false;

  constructor(protected applicationUserService: ApplicationUserService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.applicationUserService.query().subscribe({
      next: (res: HttpResponse<IApplicationUser[]>) => {
        this.isLoading = false;
        this.applicationUsers = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IApplicationUser): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(applicationUser: IApplicationUser): void {
    const modalRef = this.modalService.open(ApplicationUserDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.applicationUser = applicationUser;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

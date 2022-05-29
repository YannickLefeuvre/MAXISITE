import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAlbumPhoto } from '../album-photo.model';
import { AlbumPhotoService } from '../service/album-photo.service';
import { AlbumPhotoDeleteDialogComponent } from '../delete/album-photo-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-album-photo',
  templateUrl: './album-photo.component.html',
})
export class AlbumPhotoComponent implements OnInit {
  albumPhotos?: IAlbumPhoto[];
  isLoading = false;

  constructor(protected albumPhotoService: AlbumPhotoService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.albumPhotoService.query().subscribe({
      next: (res: HttpResponse<IAlbumPhoto[]>) => {
        this.isLoading = false;
        this.albumPhotos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IAlbumPhoto): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(albumPhoto: IAlbumPhoto): void {
    const modalRef = this.modalService.open(AlbumPhotoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.albumPhoto = albumPhoto;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

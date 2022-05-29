import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVideo } from '../video.model';
import { VideoService } from '../service/video.service';
import { VideoDeleteDialogComponent } from '../delete/video-delete-dialog.component';

@Component({
  selector: 'jhi-video',
  templateUrl: './video.component.html',
})
export class VideoComponent implements OnInit {
  videos?: IVideo[];
  isLoading = false;

  constructor(protected videoService: VideoService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.videoService.query().subscribe({
      next: (res: HttpResponse<IVideo[]>) => {
        this.isLoading = false;
        this.videos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IVideo): number {
    return item.id!;
  }

  delete(video: IVideo): void {
    const modalRef = this.modalService.open(VideoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.video = video;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

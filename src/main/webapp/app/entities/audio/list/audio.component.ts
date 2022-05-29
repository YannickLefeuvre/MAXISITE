import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAudio } from '../audio.model';
import { AudioService } from '../service/audio.service';
import { AudioDeleteDialogComponent } from '../delete/audio-delete-dialog.component';

@Component({
  selector: 'jhi-audio',
  templateUrl: './audio.component.html',
})
export class AudioComponent implements OnInit {
  audio?: IAudio[];
  isLoading = false;

  constructor(protected audioService: AudioService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.audioService.query().subscribe({
      next: (res: HttpResponse<IAudio[]>) => {
        this.isLoading = false;
        this.audio = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IAudio): number {
    return item.id!;
  }

  delete(audio: IAudio): void {
    const modalRef = this.modalService.open(AudioDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.audio = audio;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}

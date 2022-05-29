import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IVideo, Video } from '../video.model';
import { VideoService } from '../service/video.service';

@Component({
  selector: 'jhi-video-update',
  templateUrl: './video-update.component.html',
})
export class VideoUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    url: [],
  });

  constructor(protected videoService: VideoService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ video }) => {
      this.updateForm(video);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const video = this.createFromForm();
    if (video.id !== undefined) {
      this.subscribeToSaveResponse(this.videoService.update(video));
    } else {
      this.subscribeToSaveResponse(this.videoService.create(video));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVideo>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(video: IVideo): void {
    this.editForm.patchValue({
      id: video.id,
      nom: video.nom,
      url: video.url,
    });
  }

  protected createFromForm(): IVideo {
    return {
      ...new Video(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}

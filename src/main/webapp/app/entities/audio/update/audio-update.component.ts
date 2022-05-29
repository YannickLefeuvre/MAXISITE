import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAudio, Audio } from '../audio.model';
import { AudioService } from '../service/audio.service';

@Component({
  selector: 'jhi-audio-update',
  templateUrl: './audio-update.component.html',
})
export class AudioUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    url: [],
  });

  constructor(protected audioService: AudioService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ audio }) => {
      this.updateForm(audio);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const audio = this.createFromForm();
    if (audio.id !== undefined) {
      this.subscribeToSaveResponse(this.audioService.update(audio));
    } else {
      this.subscribeToSaveResponse(this.audioService.create(audio));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAudio>>): void {
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

  protected updateForm(audio: IAudio): void {
    this.editForm.patchValue({
      id: audio.id,
      nom: audio.nom,
      url: audio.url,
    });
  }

  protected createFromForm(): IAudio {
    return {
      ...new Audio(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}

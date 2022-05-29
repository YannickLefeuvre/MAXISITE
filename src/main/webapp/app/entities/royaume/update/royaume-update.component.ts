import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRoyaume, Royaume } from '../royaume.model';
import { RoyaumeService } from '../service/royaume.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IVille } from 'app/entities/ville/ville.model';
import { VilleService } from 'app/entities/ville/service/ville.service';

@Component({
  selector: 'jhi-royaume-update',
  templateUrl: './royaume-update.component.html',
})
export class RoyaumeUpdateComponent implements OnInit {
  isSaving = false;

  villesSharedCollection: IVille[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    description: [],
    regles: [],
    arriereplan: [],
    arriereplanContentType: [],
    isPublic: [],
    ville: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected royaumeService: RoyaumeService,
    protected villeService: VilleService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ royaume }) => {
      this.updateForm(royaume);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('mobbApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const royaume = this.createFromForm();
    if (royaume.id !== undefined) {
      this.subscribeToSaveResponse(this.royaumeService.update(royaume));
    } else {
      this.subscribeToSaveResponse(this.royaumeService.create(royaume));
    }
  }

  trackVilleById(_index: number, item: IVille): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRoyaume>>): void {
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

  protected updateForm(royaume: IRoyaume): void {
    this.editForm.patchValue({
      id: royaume.id,
      nom: royaume.nom,
      description: royaume.description,
      regles: royaume.regles,
      arriereplan: royaume.arriereplan,
      arriereplanContentType: royaume.arriereplanContentType,
      isPublic: royaume.isPublic,
      ville: royaume.ville,
    });

    this.villesSharedCollection = this.villeService.addVilleToCollectionIfMissing(this.villesSharedCollection, royaume.ville);
  }

  protected loadRelationshipsOptions(): void {
    this.villeService
      .query()
      .pipe(map((res: HttpResponse<IVille[]>) => res.body ?? []))
      .pipe(map((villes: IVille[]) => this.villeService.addVilleToCollectionIfMissing(villes, this.editForm.get('ville')!.value)))
      .subscribe((villes: IVille[]) => (this.villesSharedCollection = villes));
  }

  protected createFromForm(): IRoyaume {
    return {
      ...new Royaume(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      description: this.editForm.get(['description'])!.value,
      regles: this.editForm.get(['regles'])!.value,
      arriereplanContentType: this.editForm.get(['arriereplanContentType'])!.value,
      arriereplan: this.editForm.get(['arriereplan'])!.value,
      isPublic: this.editForm.get(['isPublic'])!.value,
      ville: this.editForm.get(['ville'])!.value,
    };
  }
}

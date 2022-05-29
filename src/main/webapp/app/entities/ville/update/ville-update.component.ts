import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IVille, Ville } from '../ville.model';
import { VilleService } from '../service/ville.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ILien } from 'app/entities/lien/lien.model';
import { LienService } from 'app/entities/lien/service/lien.service';
import { IMaison } from 'app/entities/maison/maison.model';
import { MaisonService } from 'app/entities/maison/service/maison.service';

@Component({
  selector: 'jhi-ville-update',
  templateUrl: './ville-update.component.html',
})
export class VilleUpdateComponent implements OnInit {
  isSaving = false;

  liensSharedCollection: ILien[] = [];
  maisonsSharedCollection: IMaison[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    isCapital: [null, [Validators.required]],
    icone: [],
    iconeContentType: [],
    absisce: [],
    ordonnee: [],
    arriereplan: [],
    arriereplanContentType: [],
    lien: [],
    maison: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected villeService: VilleService,
    protected lienService: LienService,
    protected maisonService: MaisonService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ville }) => {
      this.updateForm(ville);

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
    const ville = this.createFromForm();
    if (ville.id !== undefined) {
      this.subscribeToSaveResponse(this.villeService.update(ville));
    } else {
      this.subscribeToSaveResponse(this.villeService.create(ville));
    }
  }

  trackLienById(_index: number, item: ILien): number {
    return item.id!;
  }

  trackMaisonById(_index: number, item: IMaison): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVille>>): void {
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

  protected updateForm(ville: IVille): void {
    this.editForm.patchValue({
      id: ville.id,
      nom: ville.nom,
      isCapital: ville.isCapital,
      icone: ville.icone,
      iconeContentType: ville.iconeContentType,
      absisce: ville.absisce,
      ordonnee: ville.ordonnee,
      arriereplan: ville.arriereplan,
      arriereplanContentType: ville.arriereplanContentType,
      lien: ville.lien,
      maison: ville.maison,
    });

    this.liensSharedCollection = this.lienService.addLienToCollectionIfMissing(this.liensSharedCollection, ville.lien);
    this.maisonsSharedCollection = this.maisonService.addMaisonToCollectionIfMissing(this.maisonsSharedCollection, ville.maison);
  }

  protected loadRelationshipsOptions(): void {
    this.lienService
      .query()
      .pipe(map((res: HttpResponse<ILien[]>) => res.body ?? []))
      .pipe(map((liens: ILien[]) => this.lienService.addLienToCollectionIfMissing(liens, this.editForm.get('lien')!.value)))
      .subscribe((liens: ILien[]) => (this.liensSharedCollection = liens));

    this.maisonService
      .query()
      .pipe(map((res: HttpResponse<IMaison[]>) => res.body ?? []))
      .pipe(map((maisons: IMaison[]) => this.maisonService.addMaisonToCollectionIfMissing(maisons, this.editForm.get('maison')!.value)))
      .subscribe((maisons: IMaison[]) => (this.maisonsSharedCollection = maisons));
  }

  protected createFromForm(): IVille {
    return {
      ...new Ville(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      isCapital: this.editForm.get(['isCapital'])!.value,
      iconeContentType: this.editForm.get(['iconeContentType'])!.value,
      icone: this.editForm.get(['icone'])!.value,
      absisce: this.editForm.get(['absisce'])!.value,
      ordonnee: this.editForm.get(['ordonnee'])!.value,
      arriereplanContentType: this.editForm.get(['arriereplanContentType'])!.value,
      arriereplan: this.editForm.get(['arriereplan'])!.value,
      lien: this.editForm.get(['lien'])!.value,
      maison: this.editForm.get(['maison'])!.value,
    };
  }
}

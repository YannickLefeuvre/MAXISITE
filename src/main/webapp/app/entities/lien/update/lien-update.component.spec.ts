import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LienService } from '../service/lien.service';
import { ILien, Lien } from '../lien.model';
import { IVille } from 'app/entities/ville/ville.model';
import { VilleService } from 'app/entities/ville/service/ville.service';

import { LienUpdateComponent } from './lien-update.component';

describe('Lien Management Update Component', () => {
  let comp: LienUpdateComponent;
  let fixture: ComponentFixture<LienUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let lienService: LienService;
  let villeService: VilleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LienUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(LienUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LienUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    lienService = TestBed.inject(LienService);
    villeService = TestBed.inject(VilleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call villeOrigine query and add missing value', () => {
      const lien: ILien = { id: 456 };
      const villeOrigine: IVille = { id: 45105 };
      lien.villeOrigine = villeOrigine;

      const villeOrigineCollection: IVille[] = [{ id: 41047 }];
      jest.spyOn(villeService, 'query').mockReturnValue(of(new HttpResponse({ body: villeOrigineCollection })));
      const expectedCollection: IVille[] = [villeOrigine, ...villeOrigineCollection];
      jest.spyOn(villeService, 'addVilleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lien });
      comp.ngOnInit();

      expect(villeService.query).toHaveBeenCalled();
      expect(villeService.addVilleToCollectionIfMissing).toHaveBeenCalledWith(villeOrigineCollection, villeOrigine);
      expect(comp.villeOriginesCollection).toEqual(expectedCollection);
    });

    it('Should call villeCible query and add missing value', () => {
      const lien: ILien = { id: 456 };
      const villeCible: IVille = { id: 23514 };
      lien.villeCible = villeCible;

      const villeCibleCollection: IVille[] = [{ id: 78772 }];
      jest.spyOn(villeService, 'query').mockReturnValue(of(new HttpResponse({ body: villeCibleCollection })));
      const expectedCollection: IVille[] = [villeCible, ...villeCibleCollection];
      jest.spyOn(villeService, 'addVilleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lien });
      comp.ngOnInit();

      expect(villeService.query).toHaveBeenCalled();
      expect(villeService.addVilleToCollectionIfMissing).toHaveBeenCalledWith(villeCibleCollection, villeCible);
      expect(comp.villeCiblesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const lien: ILien = { id: 456 };
      const villeOrigine: IVille = { id: 24048 };
      lien.villeOrigine = villeOrigine;
      const villeCible: IVille = { id: 14994 };
      lien.villeCible = villeCible;

      activatedRoute.data = of({ lien });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(lien));
      expect(comp.villeOriginesCollection).toContain(villeOrigine);
      expect(comp.villeCiblesCollection).toContain(villeCible);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lien>>();
      const lien = { id: 123 };
      jest.spyOn(lienService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lien });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lien }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(lienService.update).toHaveBeenCalledWith(lien);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lien>>();
      const lien = new Lien();
      jest.spyOn(lienService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lien });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lien }));
      saveSubject.complete();

      // THEN
      expect(lienService.create).toHaveBeenCalledWith(lien);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lien>>();
      const lien = { id: 123 };
      jest.spyOn(lienService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lien });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(lienService.update).toHaveBeenCalledWith(lien);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackVilleById', () => {
      it('Should return tracked Ville primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackVilleById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});

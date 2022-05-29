import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VilleService } from '../service/ville.service';
import { IVille, Ville } from '../ville.model';
import { ILien } from 'app/entities/lien/lien.model';
import { LienService } from 'app/entities/lien/service/lien.service';
import { IMaison } from 'app/entities/maison/maison.model';
import { MaisonService } from 'app/entities/maison/service/maison.service';

import { VilleUpdateComponent } from './ville-update.component';

describe('Ville Management Update Component', () => {
  let comp: VilleUpdateComponent;
  let fixture: ComponentFixture<VilleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let villeService: VilleService;
  let lienService: LienService;
  let maisonService: MaisonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VilleUpdateComponent],
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
      .overrideTemplate(VilleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VilleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    villeService = TestBed.inject(VilleService);
    lienService = TestBed.inject(LienService);
    maisonService = TestBed.inject(MaisonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Lien query and add missing value', () => {
      const ville: IVille = { id: 456 };
      const lien: ILien = { id: 50766 };
      ville.lien = lien;

      const lienCollection: ILien[] = [{ id: 73374 }];
      jest.spyOn(lienService, 'query').mockReturnValue(of(new HttpResponse({ body: lienCollection })));
      const additionalLiens = [lien];
      const expectedCollection: ILien[] = [...additionalLiens, ...lienCollection];
      jest.spyOn(lienService, 'addLienToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      expect(lienService.query).toHaveBeenCalled();
      expect(lienService.addLienToCollectionIfMissing).toHaveBeenCalledWith(lienCollection, ...additionalLiens);
      expect(comp.liensSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Maison query and add missing value', () => {
      const ville: IVille = { id: 456 };
      const maison: IMaison = { id: 26324 };
      ville.maison = maison;

      const maisonCollection: IMaison[] = [{ id: 21154 }];
      jest.spyOn(maisonService, 'query').mockReturnValue(of(new HttpResponse({ body: maisonCollection })));
      const additionalMaisons = [maison];
      const expectedCollection: IMaison[] = [...additionalMaisons, ...maisonCollection];
      jest.spyOn(maisonService, 'addMaisonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      expect(maisonService.query).toHaveBeenCalled();
      expect(maisonService.addMaisonToCollectionIfMissing).toHaveBeenCalledWith(maisonCollection, ...additionalMaisons);
      expect(comp.maisonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ville: IVille = { id: 456 };
      const lien: ILien = { id: 24191 };
      ville.lien = lien;
      const maison: IMaison = { id: 90578 };
      ville.maison = maison;

      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ville));
      expect(comp.liensSharedCollection).toContain(lien);
      expect(comp.maisonsSharedCollection).toContain(maison);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ville>>();
      const ville = { id: 123 };
      jest.spyOn(villeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ville }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(villeService.update).toHaveBeenCalledWith(ville);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ville>>();
      const ville = new Ville();
      jest.spyOn(villeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ville }));
      saveSubject.complete();

      // THEN
      expect(villeService.create).toHaveBeenCalledWith(ville);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ville>>();
      const ville = { id: 123 };
      jest.spyOn(villeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(villeService.update).toHaveBeenCalledWith(ville);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackLienById', () => {
      it('Should return tracked Lien primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLienById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackMaisonById', () => {
      it('Should return tracked Maison primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackMaisonById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});

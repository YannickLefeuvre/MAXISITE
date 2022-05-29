import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RoyaumeService } from '../service/royaume.service';
import { IRoyaume, Royaume } from '../royaume.model';
import { IVille } from 'app/entities/ville/ville.model';
import { VilleService } from 'app/entities/ville/service/ville.service';

import { RoyaumeUpdateComponent } from './royaume-update.component';

describe('Royaume Management Update Component', () => {
  let comp: RoyaumeUpdateComponent;
  let fixture: ComponentFixture<RoyaumeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let royaumeService: RoyaumeService;
  let villeService: VilleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RoyaumeUpdateComponent],
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
      .overrideTemplate(RoyaumeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RoyaumeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    royaumeService = TestBed.inject(RoyaumeService);
    villeService = TestBed.inject(VilleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ville query and add missing value', () => {
      const royaume: IRoyaume = { id: 456 };
      const ville: IVille = { id: 38382 };
      royaume.ville = ville;

      const villeCollection: IVille[] = [{ id: 76588 }];
      jest.spyOn(villeService, 'query').mockReturnValue(of(new HttpResponse({ body: villeCollection })));
      const additionalVilles = [ville];
      const expectedCollection: IVille[] = [...additionalVilles, ...villeCollection];
      jest.spyOn(villeService, 'addVilleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ royaume });
      comp.ngOnInit();

      expect(villeService.query).toHaveBeenCalled();
      expect(villeService.addVilleToCollectionIfMissing).toHaveBeenCalledWith(villeCollection, ...additionalVilles);
      expect(comp.villesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const royaume: IRoyaume = { id: 456 };
      const ville: IVille = { id: 11654 };
      royaume.ville = ville;

      activatedRoute.data = of({ royaume });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(royaume));
      expect(comp.villesSharedCollection).toContain(ville);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Royaume>>();
      const royaume = { id: 123 };
      jest.spyOn(royaumeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ royaume });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: royaume }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(royaumeService.update).toHaveBeenCalledWith(royaume);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Royaume>>();
      const royaume = new Royaume();
      jest.spyOn(royaumeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ royaume });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: royaume }));
      saveSubject.complete();

      // THEN
      expect(royaumeService.create).toHaveBeenCalledWith(royaume);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Royaume>>();
      const royaume = { id: 123 };
      jest.spyOn(royaumeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ royaume });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(royaumeService.update).toHaveBeenCalledWith(royaume);
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

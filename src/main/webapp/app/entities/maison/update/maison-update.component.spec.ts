import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MaisonService } from '../service/maison.service';
import { IMaison, Maison } from '../maison.model';

import { MaisonUpdateComponent } from './maison-update.component';

describe('Maison Management Update Component', () => {
  let comp: MaisonUpdateComponent;
  let fixture: ComponentFixture<MaisonUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let maisonService: MaisonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MaisonUpdateComponent],
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
      .overrideTemplate(MaisonUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MaisonUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    maisonService = TestBed.inject(MaisonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const maison: IMaison = { id: 456 };

      activatedRoute.data = of({ maison });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(maison));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Maison>>();
      const maison = { id: 123 };
      jest.spyOn(maisonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ maison });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: maison }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(maisonService.update).toHaveBeenCalledWith(maison);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Maison>>();
      const maison = new Maison();
      jest.spyOn(maisonService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ maison });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: maison }));
      saveSubject.complete();

      // THEN
      expect(maisonService.create).toHaveBeenCalledWith(maison);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Maison>>();
      const maison = { id: 123 };
      jest.spyOn(maisonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ maison });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(maisonService.update).toHaveBeenCalledWith(maison);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

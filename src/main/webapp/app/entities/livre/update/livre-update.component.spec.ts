import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LivreService } from '../service/livre.service';
import { ILivre, Livre } from '../livre.model';

import { LivreUpdateComponent } from './livre-update.component';

describe('Livre Management Update Component', () => {
  let comp: LivreUpdateComponent;
  let fixture: ComponentFixture<LivreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let livreService: LivreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LivreUpdateComponent],
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
      .overrideTemplate(LivreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LivreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    livreService = TestBed.inject(LivreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const livre: ILivre = { id: 456 };

      activatedRoute.data = of({ livre });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(livre));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Livre>>();
      const livre = { id: 123 };
      jest.spyOn(livreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: livre }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(livreService.update).toHaveBeenCalledWith(livre);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Livre>>();
      const livre = new Livre();
      jest.spyOn(livreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: livre }));
      saveSubject.complete();

      // THEN
      expect(livreService.create).toHaveBeenCalledWith(livre);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Livre>>();
      const livre = { id: 123 };
      jest.spyOn(livreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(livreService.update).toHaveBeenCalledWith(livre);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

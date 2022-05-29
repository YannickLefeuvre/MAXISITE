import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AudioService } from '../service/audio.service';
import { IAudio, Audio } from '../audio.model';

import { AudioUpdateComponent } from './audio-update.component';

describe('Audio Management Update Component', () => {
  let comp: AudioUpdateComponent;
  let fixture: ComponentFixture<AudioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let audioService: AudioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AudioUpdateComponent],
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
      .overrideTemplate(AudioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AudioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    audioService = TestBed.inject(AudioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const audio: IAudio = { id: 456 };

      activatedRoute.data = of({ audio });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(audio));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Audio>>();
      const audio = { id: 123 };
      jest.spyOn(audioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ audio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: audio }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(audioService.update).toHaveBeenCalledWith(audio);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Audio>>();
      const audio = new Audio();
      jest.spyOn(audioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ audio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: audio }));
      saveSubject.complete();

      // THEN
      expect(audioService.create).toHaveBeenCalledWith(audio);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Audio>>();
      const audio = { id: 123 };
      jest.spyOn(audioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ audio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(audioService.update).toHaveBeenCalledWith(audio);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

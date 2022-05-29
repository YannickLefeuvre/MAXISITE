import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VideoService } from '../service/video.service';
import { IVideo, Video } from '../video.model';

import { VideoUpdateComponent } from './video-update.component';

describe('Video Management Update Component', () => {
  let comp: VideoUpdateComponent;
  let fixture: ComponentFixture<VideoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let videoService: VideoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VideoUpdateComponent],
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
      .overrideTemplate(VideoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VideoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    videoService = TestBed.inject(VideoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const video: IVideo = { id: 456 };

      activatedRoute.data = of({ video });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(video));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Video>>();
      const video = { id: 123 };
      jest.spyOn(videoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ video });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: video }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(videoService.update).toHaveBeenCalledWith(video);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Video>>();
      const video = new Video();
      jest.spyOn(videoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ video });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: video }));
      saveSubject.complete();

      // THEN
      expect(videoService.create).toHaveBeenCalledWith(video);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Video>>();
      const video = { id: 123 };
      jest.spyOn(videoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ video });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(videoService.update).toHaveBeenCalledWith(video);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

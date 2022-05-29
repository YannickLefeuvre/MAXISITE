import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AlbumPhotoService } from '../service/album-photo.service';
import { IAlbumPhoto, AlbumPhoto } from '../album-photo.model';

import { AlbumPhotoUpdateComponent } from './album-photo-update.component';

describe('AlbumPhoto Management Update Component', () => {
  let comp: AlbumPhotoUpdateComponent;
  let fixture: ComponentFixture<AlbumPhotoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let albumPhotoService: AlbumPhotoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AlbumPhotoUpdateComponent],
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
      .overrideTemplate(AlbumPhotoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AlbumPhotoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    albumPhotoService = TestBed.inject(AlbumPhotoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const albumPhoto: IAlbumPhoto = { id: 456 };

      activatedRoute.data = of({ albumPhoto });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(albumPhoto));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AlbumPhoto>>();
      const albumPhoto = { id: 123 };
      jest.spyOn(albumPhotoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ albumPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: albumPhoto }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(albumPhotoService.update).toHaveBeenCalledWith(albumPhoto);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AlbumPhoto>>();
      const albumPhoto = new AlbumPhoto();
      jest.spyOn(albumPhotoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ albumPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: albumPhoto }));
      saveSubject.complete();

      // THEN
      expect(albumPhotoService.create).toHaveBeenCalledWith(albumPhoto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AlbumPhoto>>();
      const albumPhoto = { id: 123 };
      jest.spyOn(albumPhotoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ albumPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(albumPhotoService.update).toHaveBeenCalledWith(albumPhoto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

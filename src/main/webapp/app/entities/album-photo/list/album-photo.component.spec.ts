import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AlbumPhotoService } from '../service/album-photo.service';

import { AlbumPhotoComponent } from './album-photo.component';

describe('AlbumPhoto Management Component', () => {
  let comp: AlbumPhotoComponent;
  let fixture: ComponentFixture<AlbumPhotoComponent>;
  let service: AlbumPhotoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [AlbumPhotoComponent],
    })
      .overrideTemplate(AlbumPhotoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AlbumPhotoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AlbumPhotoService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.albumPhotos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

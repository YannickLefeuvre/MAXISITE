import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { VideoService } from '../service/video.service';

import { VideoComponent } from './video.component';

describe('Video Management Component', () => {
  let comp: VideoComponent;
  let fixture: ComponentFixture<VideoComponent>;
  let service: VideoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [VideoComponent],
    })
      .overrideTemplate(VideoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VideoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VideoService);

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
    expect(comp.videos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

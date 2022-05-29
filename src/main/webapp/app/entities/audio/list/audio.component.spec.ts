import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AudioService } from '../service/audio.service';

import { AudioComponent } from './audio.component';

describe('Audio Management Component', () => {
  let comp: AudioComponent;
  let fixture: ComponentFixture<AudioComponent>;
  let service: AudioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [AudioComponent],
    })
      .overrideTemplate(AudioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AudioComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AudioService);

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
    expect(comp.audio?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

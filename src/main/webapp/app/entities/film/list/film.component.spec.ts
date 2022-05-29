import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { FilmService } from '../service/film.service';

import { FilmComponent } from './film.component';

describe('Film Management Component', () => {
  let comp: FilmComponent;
  let fixture: ComponentFixture<FilmComponent>;
  let service: FilmService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [FilmComponent],
    })
      .overrideTemplate(FilmComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FilmComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FilmService);

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
    expect(comp.films?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

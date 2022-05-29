import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { LivreService } from '../service/livre.service';

import { LivreComponent } from './livre.component';

describe('Livre Management Component', () => {
  let comp: LivreComponent;
  let fixture: ComponentFixture<LivreComponent>;
  let service: LivreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [LivreComponent],
    })
      .overrideTemplate(LivreComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LivreComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LivreService);

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
    expect(comp.livres?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

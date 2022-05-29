import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { LienService } from '../service/lien.service';

import { LienComponent } from './lien.component';

describe('Lien Management Component', () => {
  let comp: LienComponent;
  let fixture: ComponentFixture<LienComponent>;
  let service: LienService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [LienComponent],
    })
      .overrideTemplate(LienComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LienComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LienService);

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
    expect(comp.liens?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

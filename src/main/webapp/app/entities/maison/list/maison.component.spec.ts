import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { MaisonService } from '../service/maison.service';

import { MaisonComponent } from './maison.component';

describe('Maison Management Component', () => {
  let comp: MaisonComponent;
  let fixture: ComponentFixture<MaisonComponent>;
  let service: MaisonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MaisonComponent],
    })
      .overrideTemplate(MaisonComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MaisonComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MaisonService);

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
    expect(comp.maisons?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

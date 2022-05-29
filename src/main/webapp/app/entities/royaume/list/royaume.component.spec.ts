import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RoyaumeService } from '../service/royaume.service';

import { RoyaumeComponent } from './royaume.component';

describe('Royaume Management Component', () => {
  let comp: RoyaumeComponent;
  let fixture: ComponentFixture<RoyaumeComponent>;
  let service: RoyaumeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RoyaumeComponent],
    })
      .overrideTemplate(RoyaumeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RoyaumeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RoyaumeService);

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
    expect(comp.royaumes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});

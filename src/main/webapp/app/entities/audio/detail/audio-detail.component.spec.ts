import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AudioDetailComponent } from './audio-detail.component';

describe('Audio Management Detail Component', () => {
  let comp: AudioDetailComponent;
  let fixture: ComponentFixture<AudioDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AudioDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ audio: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AudioDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AudioDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load audio on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.audio).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

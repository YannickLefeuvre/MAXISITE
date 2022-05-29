import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAudio, Audio } from '../audio.model';

import { AudioService } from './audio.service';

describe('Audio Service', () => {
  let service: AudioService;
  let httpMock: HttpTestingController;
  let elemDefault: IAudio;
  let expectedResult: IAudio | IAudio[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AudioService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      url: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Audio', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Audio()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Audio', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Audio', () => {
      const patchObject = Object.assign({}, new Audio());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Audio', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Audio', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAudioToCollectionIfMissing', () => {
      it('should add a Audio to an empty array', () => {
        const audio: IAudio = { id: 123 };
        expectedResult = service.addAudioToCollectionIfMissing([], audio);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(audio);
      });

      it('should not add a Audio to an array that contains it', () => {
        const audio: IAudio = { id: 123 };
        const audioCollection: IAudio[] = [
          {
            ...audio,
          },
          { id: 456 },
        ];
        expectedResult = service.addAudioToCollectionIfMissing(audioCollection, audio);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Audio to an array that doesn't contain it", () => {
        const audio: IAudio = { id: 123 };
        const audioCollection: IAudio[] = [{ id: 456 }];
        expectedResult = service.addAudioToCollectionIfMissing(audioCollection, audio);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(audio);
      });

      it('should add only unique Audio to an array', () => {
        const audioArray: IAudio[] = [{ id: 123 }, { id: 456 }, { id: 99627 }];
        const audioCollection: IAudio[] = [{ id: 123 }];
        expectedResult = service.addAudioToCollectionIfMissing(audioCollection, ...audioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const audio: IAudio = { id: 123 };
        const audio2: IAudio = { id: 456 };
        expectedResult = service.addAudioToCollectionIfMissing([], audio, audio2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(audio);
        expect(expectedResult).toContain(audio2);
      });

      it('should accept null and undefined values', () => {
        const audio: IAudio = { id: 123 };
        expectedResult = service.addAudioToCollectionIfMissing([], null, audio, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(audio);
      });

      it('should return initial array if no Audio is added', () => {
        const audioCollection: IAudio[] = [{ id: 123 }];
        expectedResult = service.addAudioToCollectionIfMissing(audioCollection, undefined, null);
        expect(expectedResult).toEqual(audioCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

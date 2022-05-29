import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRoyaume, Royaume } from '../royaume.model';

import { RoyaumeService } from './royaume.service';

describe('Royaume Service', () => {
  let service: RoyaumeService;
  let httpMock: HttpTestingController;
  let elemDefault: IRoyaume;
  let expectedResult: IRoyaume | IRoyaume[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RoyaumeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      description: 'AAAAAAA',
      regles: 'AAAAAAA',
      arriereplanContentType: 'image/png',
      arriereplan: 'AAAAAAA',
      isPublic: false,
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

    it('should create a Royaume', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Royaume()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Royaume', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          description: 'BBBBBB',
          regles: 'BBBBBB',
          arriereplan: 'BBBBBB',
          isPublic: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Royaume', () => {
      const patchObject = Object.assign(
        {
          regles: 'BBBBBB',
          arriereplan: 'BBBBBB',
          isPublic: true,
        },
        new Royaume()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Royaume', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          description: 'BBBBBB',
          regles: 'BBBBBB',
          arriereplan: 'BBBBBB',
          isPublic: true,
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

    it('should delete a Royaume', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRoyaumeToCollectionIfMissing', () => {
      it('should add a Royaume to an empty array', () => {
        const royaume: IRoyaume = { id: 123 };
        expectedResult = service.addRoyaumeToCollectionIfMissing([], royaume);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(royaume);
      });

      it('should not add a Royaume to an array that contains it', () => {
        const royaume: IRoyaume = { id: 123 };
        const royaumeCollection: IRoyaume[] = [
          {
            ...royaume,
          },
          { id: 456 },
        ];
        expectedResult = service.addRoyaumeToCollectionIfMissing(royaumeCollection, royaume);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Royaume to an array that doesn't contain it", () => {
        const royaume: IRoyaume = { id: 123 };
        const royaumeCollection: IRoyaume[] = [{ id: 456 }];
        expectedResult = service.addRoyaumeToCollectionIfMissing(royaumeCollection, royaume);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(royaume);
      });

      it('should add only unique Royaume to an array', () => {
        const royaumeArray: IRoyaume[] = [{ id: 123 }, { id: 456 }, { id: 23622 }];
        const royaumeCollection: IRoyaume[] = [{ id: 123 }];
        expectedResult = service.addRoyaumeToCollectionIfMissing(royaumeCollection, ...royaumeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const royaume: IRoyaume = { id: 123 };
        const royaume2: IRoyaume = { id: 456 };
        expectedResult = service.addRoyaumeToCollectionIfMissing([], royaume, royaume2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(royaume);
        expect(expectedResult).toContain(royaume2);
      });

      it('should accept null and undefined values', () => {
        const royaume: IRoyaume = { id: 123 };
        expectedResult = service.addRoyaumeToCollectionIfMissing([], null, royaume, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(royaume);
      });

      it('should return initial array if no Royaume is added', () => {
        const royaumeCollection: IRoyaume[] = [{ id: 123 }];
        expectedResult = service.addRoyaumeToCollectionIfMissing(royaumeCollection, undefined, null);
        expect(expectedResult).toEqual(royaumeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

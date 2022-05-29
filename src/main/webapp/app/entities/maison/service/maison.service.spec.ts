import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMaison, Maison } from '../maison.model';

import { MaisonService } from './maison.service';

describe('Maison Service', () => {
  let service: MaisonService;
  let httpMock: HttpTestingController;
  let elemDefault: IMaison;
  let expectedResult: IMaison | IMaison[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MaisonService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      iconeContentType: 'image/png',
      icone: 'AAAAAAA',
      absisce: 0,
      ordonnee: 0,
      arriereplanContentType: 'image/png',
      arriereplan: 'AAAAAAA',
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

    it('should create a Maison', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Maison()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Maison', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          icone: 'BBBBBB',
          absisce: 1,
          ordonnee: 1,
          arriereplan: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Maison', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
          icone: 'BBBBBB',
          ordonnee: 1,
          arriereplan: 'BBBBBB',
        },
        new Maison()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Maison', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          icone: 'BBBBBB',
          absisce: 1,
          ordonnee: 1,
          arriereplan: 'BBBBBB',
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

    it('should delete a Maison', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMaisonToCollectionIfMissing', () => {
      it('should add a Maison to an empty array', () => {
        const maison: IMaison = { id: 123 };
        expectedResult = service.addMaisonToCollectionIfMissing([], maison);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(maison);
      });

      it('should not add a Maison to an array that contains it', () => {
        const maison: IMaison = { id: 123 };
        const maisonCollection: IMaison[] = [
          {
            ...maison,
          },
          { id: 456 },
        ];
        expectedResult = service.addMaisonToCollectionIfMissing(maisonCollection, maison);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Maison to an array that doesn't contain it", () => {
        const maison: IMaison = { id: 123 };
        const maisonCollection: IMaison[] = [{ id: 456 }];
        expectedResult = service.addMaisonToCollectionIfMissing(maisonCollection, maison);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(maison);
      });

      it('should add only unique Maison to an array', () => {
        const maisonArray: IMaison[] = [{ id: 123 }, { id: 456 }, { id: 80989 }];
        const maisonCollection: IMaison[] = [{ id: 123 }];
        expectedResult = service.addMaisonToCollectionIfMissing(maisonCollection, ...maisonArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const maison: IMaison = { id: 123 };
        const maison2: IMaison = { id: 456 };
        expectedResult = service.addMaisonToCollectionIfMissing([], maison, maison2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(maison);
        expect(expectedResult).toContain(maison2);
      });

      it('should accept null and undefined values', () => {
        const maison: IMaison = { id: 123 };
        expectedResult = service.addMaisonToCollectionIfMissing([], null, maison, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(maison);
      });

      it('should return initial array if no Maison is added', () => {
        const maisonCollection: IMaison[] = [{ id: 123 }];
        expectedResult = service.addMaisonToCollectionIfMissing(maisonCollection, undefined, null);
        expect(expectedResult).toEqual(maisonCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

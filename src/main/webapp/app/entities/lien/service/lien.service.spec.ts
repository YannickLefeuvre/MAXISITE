import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILien, Lien } from '../lien.model';

import { LienService } from './lien.service';

describe('Lien Service', () => {
  let service: LienService;
  let httpMock: HttpTestingController;
  let elemDefault: ILien;
  let expectedResult: ILien | ILien[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LienService);
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

    it('should create a Lien', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Lien()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Lien', () => {
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

    it('should partial update a Lien', () => {
      const patchObject = Object.assign({}, new Lien());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Lien', () => {
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

    it('should delete a Lien', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLienToCollectionIfMissing', () => {
      it('should add a Lien to an empty array', () => {
        const lien: ILien = { id: 123 };
        expectedResult = service.addLienToCollectionIfMissing([], lien);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(lien);
      });

      it('should not add a Lien to an array that contains it', () => {
        const lien: ILien = { id: 123 };
        const lienCollection: ILien[] = [
          {
            ...lien,
          },
          { id: 456 },
        ];
        expectedResult = service.addLienToCollectionIfMissing(lienCollection, lien);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Lien to an array that doesn't contain it", () => {
        const lien: ILien = { id: 123 };
        const lienCollection: ILien[] = [{ id: 456 }];
        expectedResult = service.addLienToCollectionIfMissing(lienCollection, lien);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(lien);
      });

      it('should add only unique Lien to an array', () => {
        const lienArray: ILien[] = [{ id: 123 }, { id: 456 }, { id: 25425 }];
        const lienCollection: ILien[] = [{ id: 123 }];
        expectedResult = service.addLienToCollectionIfMissing(lienCollection, ...lienArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const lien: ILien = { id: 123 };
        const lien2: ILien = { id: 456 };
        expectedResult = service.addLienToCollectionIfMissing([], lien, lien2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(lien);
        expect(expectedResult).toContain(lien2);
      });

      it('should accept null and undefined values', () => {
        const lien: ILien = { id: 123 };
        expectedResult = service.addLienToCollectionIfMissing([], null, lien, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(lien);
      });

      it('should return initial array if no Lien is added', () => {
        const lienCollection: ILien[] = [{ id: 123 }];
        expectedResult = service.addLienToCollectionIfMissing(lienCollection, undefined, null);
        expect(expectedResult).toEqual(lienCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

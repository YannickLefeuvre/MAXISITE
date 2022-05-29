import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IVille, Ville } from '../ville.model';

import { VilleService } from './ville.service';

describe('Ville Service', () => {
  let service: VilleService;
  let httpMock: HttpTestingController;
  let elemDefault: IVille;
  let expectedResult: IVille | IVille[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VilleService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      isCapital: false,
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

    it('should create a Ville', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Ville()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ville', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          isCapital: true,
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

    it('should partial update a Ville', () => {
      const patchObject = Object.assign(
        {
          arriereplan: 'BBBBBB',
        },
        new Ville()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ville', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          isCapital: true,
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

    it('should delete a Ville', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addVilleToCollectionIfMissing', () => {
      it('should add a Ville to an empty array', () => {
        const ville: IVille = { id: 123 };
        expectedResult = service.addVilleToCollectionIfMissing([], ville);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ville);
      });

      it('should not add a Ville to an array that contains it', () => {
        const ville: IVille = { id: 123 };
        const villeCollection: IVille[] = [
          {
            ...ville,
          },
          { id: 456 },
        ];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, ville);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ville to an array that doesn't contain it", () => {
        const ville: IVille = { id: 123 };
        const villeCollection: IVille[] = [{ id: 456 }];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, ville);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ville);
      });

      it('should add only unique Ville to an array', () => {
        const villeArray: IVille[] = [{ id: 123 }, { id: 456 }, { id: 74844 }];
        const villeCollection: IVille[] = [{ id: 123 }];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, ...villeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ville: IVille = { id: 123 };
        const ville2: IVille = { id: 456 };
        expectedResult = service.addVilleToCollectionIfMissing([], ville, ville2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ville);
        expect(expectedResult).toContain(ville2);
      });

      it('should accept null and undefined values', () => {
        const ville: IVille = { id: 123 };
        expectedResult = service.addVilleToCollectionIfMissing([], null, ville, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ville);
      });

      it('should return initial array if no Ville is added', () => {
        const villeCollection: IVille[] = [{ id: 123 }];
        expectedResult = service.addVilleToCollectionIfMissing(villeCollection, undefined, null);
        expect(expectedResult).toEqual(villeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

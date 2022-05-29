import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFilm, Film } from '../film.model';

import { FilmService } from './film.service';

describe('Film Service', () => {
  let service: FilmService;
  let httpMock: HttpTestingController;
  let elemDefault: IFilm;
  let expectedResult: IFilm | IFilm[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FilmService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      imagesContentType: 'image/png',
      images: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Film', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Film()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Film', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          images: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Film', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
          images: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Film()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Film', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          images: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Film', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFilmToCollectionIfMissing', () => {
      it('should add a Film to an empty array', () => {
        const film: IFilm = { id: 123 };
        expectedResult = service.addFilmToCollectionIfMissing([], film);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(film);
      });

      it('should not add a Film to an array that contains it', () => {
        const film: IFilm = { id: 123 };
        const filmCollection: IFilm[] = [
          {
            ...film,
          },
          { id: 456 },
        ];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, film);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Film to an array that doesn't contain it", () => {
        const film: IFilm = { id: 123 };
        const filmCollection: IFilm[] = [{ id: 456 }];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, film);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(film);
      });

      it('should add only unique Film to an array', () => {
        const filmArray: IFilm[] = [{ id: 123 }, { id: 456 }, { id: 89266 }];
        const filmCollection: IFilm[] = [{ id: 123 }];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, ...filmArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const film: IFilm = { id: 123 };
        const film2: IFilm = { id: 456 };
        expectedResult = service.addFilmToCollectionIfMissing([], film, film2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(film);
        expect(expectedResult).toContain(film2);
      });

      it('should accept null and undefined values', () => {
        const film: IFilm = { id: 123 };
        expectedResult = service.addFilmToCollectionIfMissing([], null, film, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(film);
      });

      it('should return initial array if no Film is added', () => {
        const filmCollection: IFilm[] = [{ id: 123 }];
        expectedResult = service.addFilmToCollectionIfMissing(filmCollection, undefined, null);
        expect(expectedResult).toEqual(filmCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

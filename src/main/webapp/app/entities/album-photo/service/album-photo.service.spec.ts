import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAlbumPhoto, AlbumPhoto } from '../album-photo.model';

import { AlbumPhotoService } from './album-photo.service';

describe('AlbumPhoto Service', () => {
  let service: AlbumPhotoService;
  let httpMock: HttpTestingController;
  let elemDefault: IAlbumPhoto;
  let expectedResult: IAlbumPhoto | IAlbumPhoto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AlbumPhotoService);
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

    it('should create a AlbumPhoto', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AlbumPhoto()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AlbumPhoto', () => {
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

    it('should partial update a AlbumPhoto', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
        },
        new AlbumPhoto()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AlbumPhoto', () => {
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

    it('should delete a AlbumPhoto', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAlbumPhotoToCollectionIfMissing', () => {
      it('should add a AlbumPhoto to an empty array', () => {
        const albumPhoto: IAlbumPhoto = { id: 123 };
        expectedResult = service.addAlbumPhotoToCollectionIfMissing([], albumPhoto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(albumPhoto);
      });

      it('should not add a AlbumPhoto to an array that contains it', () => {
        const albumPhoto: IAlbumPhoto = { id: 123 };
        const albumPhotoCollection: IAlbumPhoto[] = [
          {
            ...albumPhoto,
          },
          { id: 456 },
        ];
        expectedResult = service.addAlbumPhotoToCollectionIfMissing(albumPhotoCollection, albumPhoto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AlbumPhoto to an array that doesn't contain it", () => {
        const albumPhoto: IAlbumPhoto = { id: 123 };
        const albumPhotoCollection: IAlbumPhoto[] = [{ id: 456 }];
        expectedResult = service.addAlbumPhotoToCollectionIfMissing(albumPhotoCollection, albumPhoto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(albumPhoto);
      });

      it('should add only unique AlbumPhoto to an array', () => {
        const albumPhotoArray: IAlbumPhoto[] = [{ id: 123 }, { id: 456 }, { id: 33193 }];
        const albumPhotoCollection: IAlbumPhoto[] = [{ id: 123 }];
        expectedResult = service.addAlbumPhotoToCollectionIfMissing(albumPhotoCollection, ...albumPhotoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const albumPhoto: IAlbumPhoto = { id: 123 };
        const albumPhoto2: IAlbumPhoto = { id: 456 };
        expectedResult = service.addAlbumPhotoToCollectionIfMissing([], albumPhoto, albumPhoto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(albumPhoto);
        expect(expectedResult).toContain(albumPhoto2);
      });

      it('should accept null and undefined values', () => {
        const albumPhoto: IAlbumPhoto = { id: 123 };
        expectedResult = service.addAlbumPhotoToCollectionIfMissing([], null, albumPhoto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(albumPhoto);
      });

      it('should return initial array if no AlbumPhoto is added', () => {
        const albumPhotoCollection: IAlbumPhoto[] = [{ id: 123 }];
        expectedResult = service.addAlbumPhotoToCollectionIfMissing(albumPhotoCollection, undefined, null);
        expect(expectedResult).toEqual(albumPhotoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

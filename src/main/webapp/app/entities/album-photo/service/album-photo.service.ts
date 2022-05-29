import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAlbumPhoto, getAlbumPhotoIdentifier } from '../album-photo.model';

export type EntityResponseType = HttpResponse<IAlbumPhoto>;
export type EntityArrayResponseType = HttpResponse<IAlbumPhoto[]>;

@Injectable({ providedIn: 'root' })
export class AlbumPhotoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/album-photos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(albumPhoto: IAlbumPhoto): Observable<EntityResponseType> {
    return this.http.post<IAlbumPhoto>(this.resourceUrl, albumPhoto, { observe: 'response' });
  }

  update(albumPhoto: IAlbumPhoto): Observable<EntityResponseType> {
    return this.http.put<IAlbumPhoto>(`${this.resourceUrl}/${getAlbumPhotoIdentifier(albumPhoto) as number}`, albumPhoto, {
      observe: 'response',
    });
  }

  partialUpdate(albumPhoto: IAlbumPhoto): Observable<EntityResponseType> {
    return this.http.patch<IAlbumPhoto>(`${this.resourceUrl}/${getAlbumPhotoIdentifier(albumPhoto) as number}`, albumPhoto, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAlbumPhoto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAlbumPhoto[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAlbumPhotoToCollectionIfMissing(
    albumPhotoCollection: IAlbumPhoto[],
    ...albumPhotosToCheck: (IAlbumPhoto | null | undefined)[]
  ): IAlbumPhoto[] {
    const albumPhotos: IAlbumPhoto[] = albumPhotosToCheck.filter(isPresent);
    if (albumPhotos.length > 0) {
      const albumPhotoCollectionIdentifiers = albumPhotoCollection.map(albumPhotoItem => getAlbumPhotoIdentifier(albumPhotoItem)!);
      const albumPhotosToAdd = albumPhotos.filter(albumPhotoItem => {
        const albumPhotoIdentifier = getAlbumPhotoIdentifier(albumPhotoItem);
        if (albumPhotoIdentifier == null || albumPhotoCollectionIdentifiers.includes(albumPhotoIdentifier)) {
          return false;
        }
        albumPhotoCollectionIdentifiers.push(albumPhotoIdentifier);
        return true;
      });
      return [...albumPhotosToAdd, ...albumPhotoCollection];
    }
    return albumPhotoCollection;
  }
}

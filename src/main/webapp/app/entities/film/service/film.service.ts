import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFilm, getFilmIdentifier } from '../film.model';

export type EntityResponseType = HttpResponse<IFilm>;
export type EntityArrayResponseType = HttpResponse<IFilm[]>;

@Injectable({ providedIn: 'root' })
export class FilmService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/films');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(film: IFilm): Observable<EntityResponseType> {
    return this.http.post<IFilm>(this.resourceUrl, film, { observe: 'response' });
  }

  update(film: IFilm): Observable<EntityResponseType> {
    return this.http.put<IFilm>(`${this.resourceUrl}/${getFilmIdentifier(film) as number}`, film, { observe: 'response' });
  }

  partialUpdate(film: IFilm): Observable<EntityResponseType> {
    return this.http.patch<IFilm>(`${this.resourceUrl}/${getFilmIdentifier(film) as number}`, film, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFilm>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFilm[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFilmToCollectionIfMissing(filmCollection: IFilm[], ...filmsToCheck: (IFilm | null | undefined)[]): IFilm[] {
    const films: IFilm[] = filmsToCheck.filter(isPresent);
    if (films.length > 0) {
      const filmCollectionIdentifiers = filmCollection.map(filmItem => getFilmIdentifier(filmItem)!);
      const filmsToAdd = films.filter(filmItem => {
        const filmIdentifier = getFilmIdentifier(filmItem);
        if (filmIdentifier == null || filmCollectionIdentifiers.includes(filmIdentifier)) {
          return false;
        }
        filmCollectionIdentifiers.push(filmIdentifier);
        return true;
      });
      return [...filmsToAdd, ...filmCollection];
    }
    return filmCollection;
  }
}

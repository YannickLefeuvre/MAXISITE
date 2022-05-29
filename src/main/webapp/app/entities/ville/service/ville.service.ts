import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVille, getVilleIdentifier } from '../ville.model';

export type EntityResponseType = HttpResponse<IVille>;
export type EntityArrayResponseType = HttpResponse<IVille[]>;

@Injectable({ providedIn: 'root' })
export class VilleService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/villes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ville: IVille): Observable<EntityResponseType> {
    return this.http.post<IVille>(this.resourceUrl, ville, { observe: 'response' });
  }

  update(ville: IVille): Observable<EntityResponseType> {
    return this.http.put<IVille>(`${this.resourceUrl}/${getVilleIdentifier(ville) as number}`, ville, { observe: 'response' });
  }

  partialUpdate(ville: IVille): Observable<EntityResponseType> {
    return this.http.patch<IVille>(`${this.resourceUrl}/${getVilleIdentifier(ville) as number}`, ville, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVille>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVille[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVilleToCollectionIfMissing(villeCollection: IVille[], ...villesToCheck: (IVille | null | undefined)[]): IVille[] {
    const villes: IVille[] = villesToCheck.filter(isPresent);
    if (villes.length > 0) {
      const villeCollectionIdentifiers = villeCollection.map(villeItem => getVilleIdentifier(villeItem)!);
      const villesToAdd = villes.filter(villeItem => {
        const villeIdentifier = getVilleIdentifier(villeItem);
        if (villeIdentifier == null || villeCollectionIdentifiers.includes(villeIdentifier)) {
          return false;
        }
        villeCollectionIdentifiers.push(villeIdentifier);
        return true;
      });
      return [...villesToAdd, ...villeCollection];
    }
    return villeCollection;
  }
}

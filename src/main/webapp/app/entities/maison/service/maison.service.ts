import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMaison, getMaisonIdentifier } from '../maison.model';

export type EntityResponseType = HttpResponse<IMaison>;
export type EntityArrayResponseType = HttpResponse<IMaison[]>;

@Injectable({ providedIn: 'root' })
export class MaisonService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/maisons');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(maison: IMaison): Observable<EntityResponseType> {
    return this.http.post<IMaison>(this.resourceUrl, maison, { observe: 'response' });
  }

  update(maison: IMaison): Observable<EntityResponseType> {
    return this.http.put<IMaison>(`${this.resourceUrl}/${getMaisonIdentifier(maison) as number}`, maison, { observe: 'response' });
  }

  partialUpdate(maison: IMaison): Observable<EntityResponseType> {
    return this.http.patch<IMaison>(`${this.resourceUrl}/${getMaisonIdentifier(maison) as number}`, maison, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMaison>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMaison[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMaisonToCollectionIfMissing(maisonCollection: IMaison[], ...maisonsToCheck: (IMaison | null | undefined)[]): IMaison[] {
    const maisons: IMaison[] = maisonsToCheck.filter(isPresent);
    if (maisons.length > 0) {
      const maisonCollectionIdentifiers = maisonCollection.map(maisonItem => getMaisonIdentifier(maisonItem)!);
      const maisonsToAdd = maisons.filter(maisonItem => {
        const maisonIdentifier = getMaisonIdentifier(maisonItem);
        if (maisonIdentifier == null || maisonCollectionIdentifiers.includes(maisonIdentifier)) {
          return false;
        }
        maisonCollectionIdentifiers.push(maisonIdentifier);
        return true;
      });
      return [...maisonsToAdd, ...maisonCollection];
    }
    return maisonCollection;
  }
}

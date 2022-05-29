import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILien, getLienIdentifier } from '../lien.model';

export type EntityResponseType = HttpResponse<ILien>;
export type EntityArrayResponseType = HttpResponse<ILien[]>;

@Injectable({ providedIn: 'root' })
export class LienService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/liens');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(lien: ILien): Observable<EntityResponseType> {
    return this.http.post<ILien>(this.resourceUrl, lien, { observe: 'response' });
  }

  update(lien: ILien): Observable<EntityResponseType> {
    return this.http.put<ILien>(`${this.resourceUrl}/${getLienIdentifier(lien) as number}`, lien, { observe: 'response' });
  }

  partialUpdate(lien: ILien): Observable<EntityResponseType> {
    return this.http.patch<ILien>(`${this.resourceUrl}/${getLienIdentifier(lien) as number}`, lien, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILien>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILien[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLienToCollectionIfMissing(lienCollection: ILien[], ...liensToCheck: (ILien | null | undefined)[]): ILien[] {
    const liens: ILien[] = liensToCheck.filter(isPresent);
    if (liens.length > 0) {
      const lienCollectionIdentifiers = lienCollection.map(lienItem => getLienIdentifier(lienItem)!);
      const liensToAdd = liens.filter(lienItem => {
        const lienIdentifier = getLienIdentifier(lienItem);
        if (lienIdentifier == null || lienCollectionIdentifiers.includes(lienIdentifier)) {
          return false;
        }
        lienCollectionIdentifiers.push(lienIdentifier);
        return true;
      });
      return [...liensToAdd, ...lienCollection];
    }
    return lienCollection;
  }
}

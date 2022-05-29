import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRoyaume, getRoyaumeIdentifier } from '../royaume.model';

export type EntityResponseType = HttpResponse<IRoyaume>;
export type EntityArrayResponseType = HttpResponse<IRoyaume[]>;

@Injectable({ providedIn: 'root' })
export class RoyaumeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/royaumes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(royaume: IRoyaume): Observable<EntityResponseType> {
    return this.http.post<IRoyaume>(this.resourceUrl, royaume, { observe: 'response' });
  }

  update(royaume: IRoyaume): Observable<EntityResponseType> {
    return this.http.put<IRoyaume>(`${this.resourceUrl}/${getRoyaumeIdentifier(royaume) as number}`, royaume, { observe: 'response' });
  }

  partialUpdate(royaume: IRoyaume): Observable<EntityResponseType> {
    return this.http.patch<IRoyaume>(`${this.resourceUrl}/${getRoyaumeIdentifier(royaume) as number}`, royaume, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRoyaume>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRoyaume[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRoyaumeToCollectionIfMissing(royaumeCollection: IRoyaume[], ...royaumesToCheck: (IRoyaume | null | undefined)[]): IRoyaume[] {
    const royaumes: IRoyaume[] = royaumesToCheck.filter(isPresent);
    if (royaumes.length > 0) {
      const royaumeCollectionIdentifiers = royaumeCollection.map(royaumeItem => getRoyaumeIdentifier(royaumeItem)!);
      const royaumesToAdd = royaumes.filter(royaumeItem => {
        const royaumeIdentifier = getRoyaumeIdentifier(royaumeItem);
        if (royaumeIdentifier == null || royaumeCollectionIdentifiers.includes(royaumeIdentifier)) {
          return false;
        }
        royaumeCollectionIdentifiers.push(royaumeIdentifier);
        return true;
      });
      return [...royaumesToAdd, ...royaumeCollection];
    }
    return royaumeCollection;
  }
}

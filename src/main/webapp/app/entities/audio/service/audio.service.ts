import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAudio, getAudioIdentifier } from '../audio.model';

export type EntityResponseType = HttpResponse<IAudio>;
export type EntityArrayResponseType = HttpResponse<IAudio[]>;

@Injectable({ providedIn: 'root' })
export class AudioService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/audio');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(audio: IAudio): Observable<EntityResponseType> {
    return this.http.post<IAudio>(this.resourceUrl, audio, { observe: 'response' });
  }

  update(audio: IAudio): Observable<EntityResponseType> {
    return this.http.put<IAudio>(`${this.resourceUrl}/${getAudioIdentifier(audio) as number}`, audio, { observe: 'response' });
  }

  partialUpdate(audio: IAudio): Observable<EntityResponseType> {
    return this.http.patch<IAudio>(`${this.resourceUrl}/${getAudioIdentifier(audio) as number}`, audio, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAudio>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAudio[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAudioToCollectionIfMissing(audioCollection: IAudio[], ...audioToCheck: (IAudio | null | undefined)[]): IAudio[] {
    const audio: IAudio[] = audioToCheck.filter(isPresent);
    if (audio.length > 0) {
      const audioCollectionIdentifiers = audioCollection.map(audioItem => getAudioIdentifier(audioItem)!);
      const audioToAdd = audio.filter(audioItem => {
        const audioIdentifier = getAudioIdentifier(audioItem);
        if (audioIdentifier == null || audioCollectionIdentifiers.includes(audioIdentifier)) {
          return false;
        }
        audioCollectionIdentifiers.push(audioIdentifier);
        return true;
      });
      return [...audioToAdd, ...audioCollection];
    }
    return audioCollection;
  }
}

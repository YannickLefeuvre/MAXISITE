import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVideo, getVideoIdentifier } from '../video.model';

export type EntityResponseType = HttpResponse<IVideo>;
export type EntityArrayResponseType = HttpResponse<IVideo[]>;

@Injectable({ providedIn: 'root' })
export class VideoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/videos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(video: IVideo): Observable<EntityResponseType> {
    return this.http.post<IVideo>(this.resourceUrl, video, { observe: 'response' });
  }

  update(video: IVideo): Observable<EntityResponseType> {
    return this.http.put<IVideo>(`${this.resourceUrl}/${getVideoIdentifier(video) as number}`, video, { observe: 'response' });
  }

  partialUpdate(video: IVideo): Observable<EntityResponseType> {
    return this.http.patch<IVideo>(`${this.resourceUrl}/${getVideoIdentifier(video) as number}`, video, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVideo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVideo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVideoToCollectionIfMissing(videoCollection: IVideo[], ...videosToCheck: (IVideo | null | undefined)[]): IVideo[] {
    const videos: IVideo[] = videosToCheck.filter(isPresent);
    if (videos.length > 0) {
      const videoCollectionIdentifiers = videoCollection.map(videoItem => getVideoIdentifier(videoItem)!);
      const videosToAdd = videos.filter(videoItem => {
        const videoIdentifier = getVideoIdentifier(videoItem);
        if (videoIdentifier == null || videoCollectionIdentifiers.includes(videoIdentifier)) {
          return false;
        }
        videoCollectionIdentifiers.push(videoIdentifier);
        return true;
      });
      return [...videosToAdd, ...videoCollection];
    }
    return videoCollection;
  }
}

export interface IVideo {
  id?: number;
  nom?: string;
  url?: string | null;
}

export class Video implements IVideo {
  constructor(public id?: number, public nom?: string, public url?: string | null) {}
}

export function getVideoIdentifier(video: IVideo): number | undefined {
  return video.id;
}

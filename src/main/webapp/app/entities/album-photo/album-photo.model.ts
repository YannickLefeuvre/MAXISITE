export interface IAlbumPhoto {
  id?: number;
  nom?: string;
  imagesContentType?: string | null;
  images?: string | null;
  description?: string | null;
}

export class AlbumPhoto implements IAlbumPhoto {
  constructor(
    public id?: number,
    public nom?: string,
    public imagesContentType?: string | null,
    public images?: string | null,
    public description?: string | null
  ) {}
}

export function getAlbumPhotoIdentifier(albumPhoto: IAlbumPhoto): number | undefined {
  return albumPhoto.id;
}

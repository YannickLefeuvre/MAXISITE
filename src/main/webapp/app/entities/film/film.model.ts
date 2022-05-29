export interface IFilm {
  id?: number;
  nom?: string;
  imagesContentType?: string | null;
  images?: string | null;
  description?: string | null;
}

export class Film implements IFilm {
  constructor(
    public id?: number,
    public nom?: string,
    public imagesContentType?: string | null,
    public images?: string | null,
    public description?: string | null
  ) {}
}

export function getFilmIdentifier(film: IFilm): number | undefined {
  return film.id;
}

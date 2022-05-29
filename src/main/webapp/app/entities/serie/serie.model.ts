export interface ISerie {
  id?: number;
  nom?: string;
  imagesContentType?: string | null;
  images?: string | null;
  description?: string | null;
}

export class Serie implements ISerie {
  constructor(
    public id?: number,
    public nom?: string,
    public imagesContentType?: string | null,
    public images?: string | null,
    public description?: string | null
  ) {}
}

export function getSerieIdentifier(serie: ISerie): number | undefined {
  return serie.id;
}

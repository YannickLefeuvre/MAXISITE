export interface ILivre {
  id?: number;
  nom?: string;
  imagesContentType?: string | null;
  images?: string | null;
  description?: string | null;
}

export class Livre implements ILivre {
  constructor(
    public id?: number,
    public nom?: string,
    public imagesContentType?: string | null,
    public images?: string | null,
    public description?: string | null
  ) {}
}

export function getLivreIdentifier(livre: ILivre): number | undefined {
  return livre.id;
}

import { IVille } from 'app/entities/ville/ville.model';

export interface ILien {
  id?: number;
  nom?: string;
  iconeContentType?: string | null;
  icone?: string | null;
  absisce?: number | null;
  ordonnee?: number | null;
  arriereplanContentType?: string | null;
  arriereplan?: string | null;
  villeOrigine?: IVille | null;
  villeCible?: IVille | null;
}

export class Lien implements ILien {
  constructor(
    public id?: number,
    public nom?: string,
    public iconeContentType?: string | null,
    public icone?: string | null,
    public absisce?: number | null,
    public ordonnee?: number | null,
    public arriereplanContentType?: string | null,
    public arriereplan?: string | null,
    public villeOrigine?: IVille | null,
    public villeCible?: IVille | null
  ) {}
}

export function getLienIdentifier(lien: ILien): number | undefined {
  return lien.id;
}

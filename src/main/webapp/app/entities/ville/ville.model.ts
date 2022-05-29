import { ILien } from 'app/entities/lien/lien.model';
import { IMaison } from 'app/entities/maison/maison.model';

export interface IVille {
  id?: number;
  nom?: string;
  isCapital?: boolean;
  iconeContentType?: string | null;
  icone?: string | null;
  absisce?: number | null;
  ordonnee?: number | null;
  arriereplanContentType?: string | null;
  arriereplan?: string | null;
  lien?: ILien | null;
  maison?: IMaison | null;
  lienOrigine?: ILien | null;
  lienCible?: ILien | null;
}

export class Ville implements IVille {
  constructor(
    public id?: number,
    public nom?: string,
    public isCapital?: boolean,
    public iconeContentType?: string | null,
    public icone?: string | null,
    public absisce?: number | null,
    public ordonnee?: number | null,
    public arriereplanContentType?: string | null,
    public arriereplan?: string | null,
    public lien?: ILien | null,
    public maison?: IMaison | null,
    public lienOrigine?: ILien | null,
    public lienCible?: ILien | null
  ) {
    this.isCapital = this.isCapital ?? false;
  }
}

export function getVilleIdentifier(ville: IVille): number | undefined {
  return ville.id;
}

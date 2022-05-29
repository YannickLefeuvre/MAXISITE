export interface IMaison {
  id?: number;
  nom?: string;
  iconeContentType?: string | null;
  icone?: string | null;
  absisce?: number | null;
  ordonnee?: number | null;
  arriereplanContentType?: string | null;
  arriereplan?: string | null;
}

export class Maison implements IMaison {
  constructor(
    public id?: number,
    public nom?: string,
    public iconeContentType?: string | null,
    public icone?: string | null,
    public absisce?: number | null,
    public ordonnee?: number | null,
    public arriereplanContentType?: string | null,
    public arriereplan?: string | null
  ) {}
}

export function getMaisonIdentifier(maison: IMaison): number | undefined {
  return maison.id;
}

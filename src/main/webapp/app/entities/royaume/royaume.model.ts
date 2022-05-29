import { IVille } from 'app/entities/ville/ville.model';

export interface IRoyaume {
  id?: number;
  nom?: string;
  description?: string | null;
  regles?: string | null;
  arriereplanContentType?: string | null;
  arriereplan?: string | null;
  isPublic?: boolean | null;
  ville?: IVille | null;
}

export class Royaume implements IRoyaume {
  constructor(
    public id?: number,
    public nom?: string,
    public description?: string | null,
    public regles?: string | null,
    public arriereplanContentType?: string | null,
    public arriereplan?: string | null,
    public isPublic?: boolean | null,
    public ville?: IVille | null
  ) {
    this.isPublic = this.isPublic ?? false;
  }
}

export function getRoyaumeIdentifier(royaume: IRoyaume): number | undefined {
  return royaume.id;
}

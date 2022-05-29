export interface IApplicationUser {
  id?: number;
  photoprincipalContentType?: string | null;
  photoprincipal?: string | null;
}

export class ApplicationUser implements IApplicationUser {
  constructor(public id?: number, public photoprincipalContentType?: string | null, public photoprincipal?: string | null) {}
}

export function getApplicationUserIdentifier(applicationUser: IApplicationUser): number | undefined {
  return applicationUser.id;
}

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'film',
        data: { pageTitle: 'mobbApp.film.home.title' },
        loadChildren: () => import('./film/film.module').then(m => m.FilmModule),
      },
      {
        path: 'royaume',
        data: { pageTitle: 'mobbApp.royaume.home.title' },
        loadChildren: () => import('./royaume/royaume.module').then(m => m.RoyaumeModule),
      },
      {
        path: 'application-user',
        data: { pageTitle: 'mobbApp.applicationUser.home.title' },
        loadChildren: () => import('./application-user/application-user.module').then(m => m.ApplicationUserModule),
      },
      {
        path: 'serie',
        data: { pageTitle: 'mobbApp.serie.home.title' },
        loadChildren: () => import('./serie/serie.module').then(m => m.SerieModule),
      },
      {
        path: 'livre',
        data: { pageTitle: 'mobbApp.livre.home.title' },
        loadChildren: () => import('./livre/livre.module').then(m => m.LivreModule),
      },
      {
        path: 'album-photo',
        data: { pageTitle: 'mobbApp.albumPhoto.home.title' },
        loadChildren: () => import('./album-photo/album-photo.module').then(m => m.AlbumPhotoModule),
      },
      {
        path: 'photo',
        data: { pageTitle: 'mobbApp.photo.home.title' },
        loadChildren: () => import('./photo/photo.module').then(m => m.PhotoModule),
      },
      {
        path: 'video',
        data: { pageTitle: 'mobbApp.video.home.title' },
        loadChildren: () => import('./video/video.module').then(m => m.VideoModule),
      },
      {
        path: 'audio',
        data: { pageTitle: 'mobbApp.audio.home.title' },
        loadChildren: () => import('./audio/audio.module').then(m => m.AudioModule),
      },
      {
        path: 'ville',
        data: { pageTitle: 'mobbApp.ville.home.title' },
        loadChildren: () => import('./ville/ville.module').then(m => m.VilleModule),
      },
      {
        path: 'maison',
        data: { pageTitle: 'mobbApp.maison.home.title' },
        loadChildren: () => import('./maison/maison.module').then(m => m.MaisonModule),
      },
      {
        path: 'lien',
        data: { pageTitle: 'mobbApp.lien.home.title' },
        loadChildren: () => import('./lien/lien.module').then(m => m.LienModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

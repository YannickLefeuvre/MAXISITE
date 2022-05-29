import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAudio } from '../audio.model';

@Component({
  selector: 'jhi-audio-detail',
  templateUrl: './audio-detail.component.html',
})
export class AudioDetailComponent implements OnInit {
  audio: IAudio | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ audio }) => {
      this.audio = audio;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

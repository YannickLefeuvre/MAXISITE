export interface IAudio {
  id?: number;
  nom?: string;
  url?: string | null;
}

export class Audio implements IAudio {
  constructor(public id?: number, public nom?: string, public url?: string | null) {}
}

export function getAudioIdentifier(audio: IAudio): number | undefined {
  return audio.id;
}

import { WebPlugin } from '@capacitor/core';
import { DashlyBlinkPlugin } from './definitions';

export class DashlyBlinkWeb extends WebPlugin implements DashlyBlinkPlugin {
  constructor() {
    super({
      name: 'DashlyBlink',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }
}

const DashlyBlink = new DashlyBlinkWeb();

export { DashlyBlink };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(DashlyBlink);

import { WebPlugin } from '@capacitor/core';
import { DashlyBlinkPlugin } from './definitions';

export class DashlyBlinkWeb extends WebPlugin implements DashlyBlinkPlugin {
  constructor() {
    super({
      name: 'DashlyBlink',
      platforms: ['web']
    });
  }

  async checkWifiLogin(_ssid:string, _password: string): Promise<boolean> {
    const message = 'Web not supported' 
    console.log(message)
    return false
  }

  async connectToMagnet(_ssid: string): Promise<{value: string}> {
    const message = 'Web not supported' 
    console.log(message)
    return { value: message }
  }

  async sendWifiLogin(_ssid:string, _password: string): Promise<{value: string}>
  {
    const message = 'Web not supported' 
    console.log(message)
    return { value: message }
  }
}

const DashlyBlink = new DashlyBlinkWeb();

export { DashlyBlink };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(DashlyBlink);

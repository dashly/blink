import { WebPlugin } from '@capacitor/core';
import { DashlyBlinkPlugin } from './definitions';
export declare class DashlyBlinkWeb extends WebPlugin implements DashlyBlinkPlugin {
    constructor();
    checkWifiLogin(_ssid: string, _password: string): Promise<boolean>;
    connectToMagnet(_ssid: string): Promise<{
        value: string;
    }>;
    sendWifiLogin(_ssid: string, _password: string): Promise<{
        value: string;
    }>;
}
declare const DashlyBlink: DashlyBlinkWeb;
export { DashlyBlink };

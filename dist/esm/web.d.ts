import { WebPlugin } from '@capacitor/core';
import { DashlyBlinkPlugin } from './definitions';
export declare class DashlyBlinkWeb extends WebPlugin implements DashlyBlinkPlugin {
    constructor();
    enableLocationSevices(_ssid: string, _password: string): Promise<string>;
    isLocationServicesEnabled(_ssid: string, _password: string): Promise<boolean>;
    getCurrentWifiSSID(_ssid: string, _password: string): Promise<string>;
    connectToMagnet(_ssid: string): Promise<string>;
    sendWifiLoginToMagnet(_ssid: string, _password: string): Promise<{
        value: string;
    }>;
}
declare const DashlyBlink: DashlyBlinkWeb;
export { DashlyBlink };

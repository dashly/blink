import { WebPlugin } from '@capacitor/core';
import { DashlyBlinkPlugin } from './definitions';
export declare class DashlyBlinkWeb extends WebPlugin implements DashlyBlinkPlugin {
    constructor();
    enableLocationSevices(_ssid: string, _password: string): Promise<{
        value: string;
    }>;
    isLocationServicesEnabled(_ssid: string, _password: string): Promise<{
        value: boolean;
    }>;
    getCurrentWifiSSID(_ssid: string, _password: string): Promise<{
        value: string;
    }>;
    connectToMagnet(_ssid: string): Promise<{
        value: string;
    }>;
    sendWifiLoginToMagnet(_ssid: string, _password: string): Promise<{
        value: string;
    }>;
}
declare const DashlyBlink: DashlyBlinkWeb;
export { DashlyBlink };

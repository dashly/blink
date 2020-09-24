import { WebPlugin } from '@capacitor/core';
import { DashlyBlinkPlugin } from './definitions';
export declare class DashlyBlinkWeb extends WebPlugin implements DashlyBlinkPlugin {
    constructor();
    isBlinkSupported(): Promise<{
        value: boolean;
    }>;
    enableLocationSevices(): Promise<{
        value: string;
    }>;
    isLocationServicesEnabled(): Promise<{
        value: string;
    }>;
    getCurrentWifiSSID(): Promise<{
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

declare module "@capacitor/core" {
    interface PluginRegistry {
        DashlyBlink: DashlyBlinkPlugin;
    }
}
export interface DashlyBlinkPlugin {
    enableLocationSevices(ssid: string, password: string): Promise<string>;
    isLocationServicesEnabled(ssid: string, password: string): Promise<boolean>;
    getCurrentWifiSSID(ssid: string, password: string): Promise<string>;
    connectToMagnet(ssid: string): Promise<string>;
    sendWifiLoginToMagnet(ssid: string, password: string): Promise<{
        value: string;
    }>;
}

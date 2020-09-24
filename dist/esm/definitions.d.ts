declare module "@capacitor/core" {
    interface PluginRegistry {
        DashlyBlink: DashlyBlinkPlugin;
    }
}
export interface DashlyBlinkPlugin {
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
    connectToMagnet(ssid: string): Promise<{
        value: string;
    }>;
    sendWifiLoginToMagnet(ssid: string, password: string): Promise<{
        value: string;
    }>;
}

declare module "@capacitor/core" {
    interface PluginRegistry {
        DashlyBlink: DashlyBlinkPlugin;
    }
}
export interface DashlyBlinkPlugin {
    checkWifiLogin(ssid: string, password: string): Promise<boolean>;
    connectToMagnet(ssid: string): Promise<{
        value: string;
    }>;
    sendWifiLogin(ssid: string, password: string): Promise<{
        value: string;
    }>;
}

declare module "@capacitor/core" {
  interface PluginRegistry {
    DashlyBlink: DashlyBlinkPlugin;
  }
}

export interface DashlyBlinkPlugin {
  connectToMagnet(ssid: string): Promise<{value: string}>;
  sendWifiLogin(ssid:string, password: string): Promise<{value: string}>;
}

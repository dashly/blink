declare module "@capacitor/core" {
  interface PluginRegistry {
    DashlyBlink: DashlyBlinkPlugin;
  }
}

export interface DashlyBlinkPlugin {
  enableLocationSevices(ssid:string, password: string): Promise<{value: string}>
  isLocationServicesEnabled(ssid:string, password: string): Promise<{value: boolean}>
  getCurrentWifiSSID(ssid:string, password: string): Promise<{value: string}>
  connectToMagnet(ssid: string): Promise<{value: string}>
  sendWifiLoginToMagnet(ssid:string, password: string): Promise<{value: string}>
}

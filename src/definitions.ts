declare module "@capacitor/core" {
  interface PluginRegistry {
    DashlyBlink: DashlyBlinkPlugin;
  }
}

export interface DashlyBlinkPlugin {
  enableLocationSevices(): Promise<{value: string}>
  isLocationServicesEnabled(): Promise<{value: boolean}>
  getCurrentWifiSSID(): Promise<{value: string}>
  connectToMagnet(ssid: string): Promise<{value: string}>
  sendWifiLoginToMagnet(ssid:string, password: string): Promise<{value: string}>
}

declare module "@capacitor/core" {
  interface PluginRegistry {
    DashlyBlink: DashlyBlinkPlugin;
  }
}

export interface DashlyBlinkPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}

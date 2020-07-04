#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(DashlyBlink, "DashlyBlink",
    CAP_PLUGIN_METHOD(enableLocationSevices, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(isLocationServicesEnabled, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(getCurrentWifiSSID, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(connectToMagnet, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(sendWifiLoginToMagnet, CAPPluginReturnPromise);
)

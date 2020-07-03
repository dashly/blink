#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(DashlyBlink, "DashlyBlink",
    CAP_PLUGIN_METHOD(checkWifiLogin, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(connectToMagnet, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(sendWifiLogin, CAPPluginReturnPromise);
)

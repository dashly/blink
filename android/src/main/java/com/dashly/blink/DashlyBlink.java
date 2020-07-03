package com.dashly.blink;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin()
public class DashlyBlink extends Plugin {

    @PluginMethod()
    public void checkWifiLogin(PluginCall call) {
        String ssid = call.getString("ssid");
        String password = call.getString("password");

        JSObject ret = new JSObject();
        ret.put("ssid", ssid);
        ret.put("password", password);
        call.success(ret);
    }

    @PluginMethod()
    public void connectToMagnet(PluginCall call) {
        String ssid = call.getString("ssid");

        JSObject ret = new JSObject();
        ret.put("ssid", ssid);
        call.success(ret);
    }

    @PluginMethod()
    public void sendWifiLogin(PluginCall call) {
        String ssid = call.getString("ssid");
        String password = call.getString("password");

        JSObject ret = new JSObject();
        ret.put("ssid", ssid);
        ret.put("password", password);
        call.success(ret);
    }
}

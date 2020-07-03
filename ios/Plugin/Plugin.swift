import Foundation
import Capacitor
import NetworkExtension

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(DashlyBlink)
public class DashlyBlink: CAPPlugin {
    
    @objc func checkWifiLogin(_ call: CAPPluginCall) {
        guard let ssid = call.options["ssid"] as? String else {
            call.reject("Must provide an ssid")
            return
        }
        guard let password = call.options["password"] as? String else {
            call.reject("Must provide an password")
            return
        }

        print("checkWifiLogin: " + ssid + " " + password)

        call.resolve()
    }

    @objc func connectToMagnet(_ call: CAPPluginCall) {      
        guard let ssid = call.options["ssid"] as? String else {
            call.reject("Must provide an ssid")
            return
        }
    
        print("connectToMagnet: " + ssid)
        
        call.resolve()
        // let configuration = NEHotspotConfiguration.init(ssid: ssid)
        // configuration.joinOnce = true

        // NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
        //     if error != nil {
        //         /*print(error?.localizedDescription ?? "")
        //         call.reject(error?.localizedDescription., error, [
        //             "item1": true
        //         ])*/
        //         call.resolve();
        //         //an error occurred
        //     }
        //     else {
        //         //success
        //         call.resolve();
        //     }
        }
        
        
        /*
        Pass data back
        call.resolve([
            "added": true,
            "info": [
                "id": id
            ]
        ])
        
        Pass error back
        call.reject(error.localizedDescription, error, [
            "item1": true
        ])
        */
    }

    @objc func sendWifiLogin(_ call: CAPPluginCall) {
        guard let ssid = call.options["ssid"] as? String else {
            call.reject("Must provide an ssid")
            return
        }
        guard let password = call.options["password"] as? String else {
            call.reject("Must provide an password")
            return
        }

        print("sendWifiLogin: " + ssid + " " + password)

        call.resolve()
    }
}

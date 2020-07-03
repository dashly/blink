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
    
        

         let configuration = NEHotspotConfiguration.init(ssid: ssid)
         configuration.joinOnce = true

         NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
             if error != nil {
                 print(error?.localizedDescription ?? "")
                 /*call.reject(error?.localizedDescription, error, [
                     "item1": true
                 ])*/
                 //an error occurred
                call.resolve();
             }
             else {
                 //success
                print("Magnet connection success: " + ssid)
                
                 call.resolve();
             }
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
        
        print(call.options["ssid"] as? String)
        guard let ssid = call.options["ssid"] as? String else {
            call.reject("Must provide an ssid")
            return
        }
        guard let password = call.options["password"] as? String else {
            call.reject("Must provide an password")
            return
        }
        
        /*let client:TCPClient = TCPClient(addr: "127.0.0.1", port: 8080)
        var (success,errmsg)=client.connect(timeout: 1)
        if success{
            var (success,errmsg)=client.send(str:"|~\0" )
            if success{
                let data=client.read(1024*10)
                if let d=data{
                    if let str=String(bytes: d, encoding: NSUTF8StringEncoding){
                        print(str)
                    }
                }
            }else{
                print(errmsg)
            }
        }else{
            print(errmsg)
        }*/

        print("sendWifiLogin: " + ssid + " " + password)

        call.resolve()
    }
}

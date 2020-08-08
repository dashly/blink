import Foundation
import Capacitor
import NetworkExtension
import SwiftSocket
import SystemConfiguration.CaptiveNetwork
import CoreLocation.CLLocationManager

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(DashlyBlink)
public class DashlyBlink: CAPPlugin, CLLocationManagerDelegate {
    
    var locationPermissionResult: String = "PermissionNotGranted"
    var locationManager: CLLocationManager?
    private var enableLocationServicesCallback: CAPPluginCall?
    
    // retrieve the current SSID from a connected Wifi network
    private func retrieveCurrentSSID() -> String? {
        let supportedInterfaces = CNCopySupportedInterfaces()
        let interfaces = supportedInterfaces as? [String]
        let interface = interfaces?
            .compactMap { [weak self] in self?.retrieveInterfaceInfo(from: $0) }
            .first

        return interface
    }

    // Retrieve information about a specific network interface
    private func retrieveInterfaceInfo(from interface: String) -> String? {
        guard let interfaceInfo = CNCopyCurrentNetworkInfo(interface as CFString) as? [String: AnyObject],
            let ssid = interfaceInfo[kCNNetworkInfoKeySSID as String] as? String
            else {
                return nil
        }
        return ssid
    }
    
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        
        if status == .authorizedAlways {
            print("🚨[Dashly Blink] Location Services Permission Result: authorizedAlways")
            self.locationPermissionResult = "authorizedAlways"
            self.enableLocationServicesCallback?.resolve(["result" :"authorizedAlways"])
        }
        else if status == .authorizedWhenInUse {
            print("🚨[Dashly Blink] Location Services Permission Result: authorizedWhenInUse")
            self.locationPermissionResult = "authorizedWhenInUse"
            self.enableLocationServicesCallback?.resolve(["result" :"authorizedWhenInUse"])
        }
        else if status == .denied {
            print("🚨[Dashly Blink] Location Services Permission Result: denied")
            self.locationPermissionResult = "denied"
            self.enableLocationServicesCallback?.resolve(["result" :"denied"])
        }
        else if status == .restricted {
            print("🚨[Dashly Blink] Location Services Permission Result: restricted")
            self.locationPermissionResult = "restricted"
            self.enableLocationServicesCallback?.resolve(["result" :"restricted"])
        }
//        else {
//             "notDetermined"
//        }
    }

    @objc func enableLocationSevices(_ call: CAPPluginCall) {
        OperationQueue.main.addOperation{
            if self.locationManager == nil {
                self.locationManager = CLLocationManager()
            }
            self.locationManager?.delegate = self
            self.locationManager?.requestWhenInUseAuthorization()
        }
        
        self.enableLocationServicesCallback = call
    }

    @objc func isLocationServicesEnabled (_ call: CAPPluginCall)  {
        OperationQueue.main.addOperation{
            if (self.locationManager == nil) {
                self.locationManager = CLLocationManager()
            }
            if (CLLocationManager.locationServicesEnabled()) {
                let status: CLAuthorizationStatus = CLLocationManager.authorizationStatus()

                if (status == CLAuthorizationStatus.authorizedAlways ||
                    status == CLAuthorizationStatus.authorizedWhenInUse )
                {
                    print("🚨[Dashly Blink] Is location enabled: Yes")
                    call.resolve(["result" : "BlinkOK"])
                }
                else {
                    print("🚨[Dashly Blink] Is location enabled: No")
                    call.resolve(["result" : "BlinkNotOK"])
                }
            }
            else {
                print("🚨[Dashly Blink] Is location enabled: No")
                call.resolve(["result" : "BlinkNotOK"])
            }
        }
    }

    @objc func getCurrentWifiSSID(_ call: CAPPluginCall) {
        let WiFissid = retrieveCurrentSSID() as String?
        
        if (WiFissid == nil) {
             print("🚨[Dashly Blink] WiFI SSID not found")
            call.resolve(["result": ""])
        }
        else {
            print("🚨[Dashly Blink] Current Wifi SSID: " + WiFissid!)
            call.resolve(["result": WiFissid as String?])
        }
    }

    @objc func connectToMagnet(_ call: CAPPluginCall) {
        
        print("🚨[Dashly Blink] connectToMagnet")
        
        guard let ssid = call.options["ssid"] as? String else {
            print("🚨[Dashly Blink] Error: The magnet SSID was invalid.")
            call.reject("Blink108")
            return
        }
    
        let configuration = NEHotspotConfiguration.init(ssid: ssid)
        configuration.joinOnce = true

        NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
            if error != nil {
                let errorCode = error as NSError?
                print("🚨[Dashly Blink] Error: Couldn't connect to blink SSID. " + String(errorCode!.code) )
                call.reject("Blink102")
            }
            else {
                // This doe not mean it was a success
                //
                // Wait a few seconds for the case of showing "Unable to join the..." dialog.
                // Check reachability to the device because "error == nil" does not means success.
                
                let checkAttempts = 0
                while (checkAttempts < 6) {
                    checkAttempts++
                    sleep(1)
                    
                    let WiFissid = self.retrieveCurrentSSID() as String?
                    if (WiFissid != ssid) {
                        call.reject("Blink102");
                    }
                    else {
                        call.resolve(["result": "BlinkOK"]);
                    }
                }
            }
        }
    }

    @objc func sendWifiLoginToMagnet(_ call: CAPPluginCall) {
        
        call.resolve(["result": "BlinkOK"])
        
        /*guard let ssid = call.options["ssid"] as? String else {
            return call.reject("Blink106")
        }
        guard let password = call.options["password"] as? String else {
            return call.reject("Blink107")
        }
        
        print("🚨[Dashly Blink] SSID: " + ssid + " Password: " + password)
        
        let packet = String(ssid.count) + " " + String(password.count) + " " +  ssid + password
        
        let client = TCPClient(address: "192.168.4.22", port: 80)
        
        var finish = false
        var failed = false
        var connectionAttempts = 0
        while (!finish) {
            print("🚨[Dashly Blink] Socket connection attempt " + String(connectionAttempts))
            switch client.connect(timeout: 3) {
                case .success:
                    finish = true
                case .failure( _):
                    sleep(1)
                    connectionAttempts += 1
            }
            if (connectionAttempts == 10) {
                finish = true
                failed = true
            }
        }
        
        if (failed == true) {
            print("🚨[Dashly Blink] Couldn't create a socket connection")
            call.reject("Blink103")
        }
        else {
            print("🚨[Dashly Blink] Sending packet")
            switch client.send(string: packet) {
                case .success:
                   //guard let data = client.read(1024*10) else { return }
                   //if let response = String(bytes: data, encoding: .utf8) {
                    print("🚨[Dashly Blink] Magnet connection success 🎉")
                    call.resolve(["result": "BlinkOK"])
                
               case .failure(let error):
                   print("🚨[Dashly Blink] Error connecting" + error.localizedDescription)
                   call.reject("Blink104")
               }
        }*/
    }
    
    private func sendPacket(packet: String) -> Bool {
        let client = TCPClient(address: "192.168.4.22", port: 80)
        
        // Returns -1 for success
        // Returns error 2 for bad wifi password
        switch client.connect(timeout: 10) {
            case .success:
                switch client.send(string: packet) {
                    case .success:
                        guard let data = client.read(1024*10) else { return false }
                        if let response = String(bytes: data, encoding: .utf8) {
                            client.close()
                            print("🚨[Dashly Blink] Magnet connection success 🎉" + response)
                            return true
                        }
                    case .failure(let error):
                        client.close()
                        print("🚨[Dashly Blink] Error connecting" + error.localizedDescription)
                        return false
                }
            
            case .failure(let error):
                client.close()
                print("🚨[Dashly Blink] Error " + error.localizedDescription)
                return false
        }
        
        return false
    }
}

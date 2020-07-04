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
    
    var wait: Bool = true
    var locationPermissionResult: String = "PermissionNotGranted"
    var locationManager: CLLocationManager?
    
    // retrieve the current SSID from a connected Wifi network
    private func retrieveCurrentSSID() -> String? {
        let interfaces = CNCopySupportedInterfaces() as? [String]
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
        self.wait = false
        
        if status == .authorizedAlways {
            self.locationPermissionResult = "authorizedAlways"
        }
        else if status == .authorizedWhenInUse {
            self.locationPermissionResult = "autauthorizedWhenInUse"
        }
        else if status == .denied {
            self.locationPermissionResult = "denied"
        }
        else if status == .restricted {
            self.locationPermissionResult = "restricted"
        }
        else if status == .notDetermined {
            self.locationPermissionResult = "notDetermined"
        }
    }

    @objc func enableLocationSevices(_ call: CAPPluginCall) {
        
        OperationQueue.main.addOperation{
            if self.locationManager == nil {
                self.locationManager = CLLocationManager()
            }
            self.locationManager?.delegate = self
            self.wait = false
            self.locationManager?.requestWhenInUseAuthorization()
        }
        
        while self.wait { } // TODO - REMOVE THIS - NEED TO LOCK THIS THREAD INSTEAD?
        
        call.resolve(["result" : self.locationPermissionResult])
    }

    @objc func isLocationServicesEnabled (_ call: CAPPluginCall)  {
        if (self.locationManager == nil) {
            self.locationManager = CLLocationManager()
        }
        call.resolve(["result" : CLLocationManager.locationServicesEnabled()])
    }

    @objc func getCurrentWifiSSID(_ call: CAPPluginCall) {
        let WiFissid = retrieveCurrentSSID() as String?
        call.resolve(["result": WiFissid])
    }

    @objc func connectToMagnet(_ call: CAPPluginCall) {      
        guard let ssid = call.options["ssid"] as? String else {
            print("[Dashly Blink] Error: The magnet SSID was invalid.")
            call.reject("InvalidBlinkSSID")
            return
        }
    
        let configuration = NEHotspotConfiguration.init(ssid: ssid)
        configuration.joinOnce = true

        NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
            if error != nil {
                print("[Dashly Blink] Error: Couldn't connect to blink SSID.")
                call.reject("BlinkWifiConnectionFailed")
            }
            else {
                call.resolve(["result": "BlinkConnected"]);
            }
        }
    }

    @objc func sendWifiLoginToMagnet(_ call: CAPPluginCall) {
        
        print(call.options["ssid"] as? String)
        guard let ssid = call.options["ssid"] as? String else {
            call.reject("InvalidWifiSSID")
            return
        }
        guard let password = call.options["password"] as? String else {
            call.reject("InvalidWifiPassword")
            return
        }
        
        let client = TCPClient(address: "192.168.4.22", port: 80)
        
        // Returns -1 for success
        // Returns error 2 for bad wifi password
        switch client.connect(timeout: 3) {
            case .success:
                switch client.send(string: String(ssid.count) + " " + String(password.count) as String + " " +  ssid + password ) {
                    case .success:
                        guard let data = client.read(1024*10) else { return }
                        if let response = String(bytes: data, encoding: .utf8) {
                            print("[Dashly Blink] Magnet connection success 🎉" + response)
                            call.resolve(["result": "BlinkSetupSuccessful"])
                        }
                    case .failure(let error):
                        print("[Dashly Blink] Error connecting" + error.localizedDescription)
                        call.reject("BlinkSetupFailed")
                }
            
            case .failure(let error):
                print("[Dashly Blink] Error " + error.localizedDescription)
                call.reject("TCPBlinkConnectionFailed")
        }
    }
}

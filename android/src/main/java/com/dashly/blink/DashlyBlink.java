package com.dashly.blink;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.style.IconMarginSpan;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

import static androidx.core.content.ContextCompat.getSystemService;

@NativePlugin(
        requestCodes={0}
)
public class DashlyBlink extends Plugin {
    PluginCall permissionCall;
    private WebSocketClient webSocketClient;

    @PluginMethod()
    public void enableLocationSevices(PluginCall call) {
        pluginRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION,0);
        permissionCall = call;
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions,
                                               int[] grantResults) {
        switch (requestCode) {
            case 0:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    JSObject ret = new JSObject();
                    ret.put("result", "authorizedAlways");
                    permissionCall.success(ret);

                }  else {
                    JSObject ret = new JSObject();
                    ret.put("result", "denied");
                    permissionCall.success(ret);
                }
                return;
        }
    }

    @PluginMethod()
    public void isLocationServicesEnabled(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("result", hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
        call.success(ret);
    }

    @PluginMethod()
    public void getCurrentWifiSSID(PluginCall call) {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext()
                .getSystemService(getContext().getApplicationContext().WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = "";
        if (info != null) {
            ssid = info.getSSID();
        }

        JSObject ret = new JSObject();
        ret.put("result", ssid.substring(1, ssid.length()-1));
        call.success(ret);
    }

    @PluginMethod()
    public void connectToMagnet(PluginCall call) {
        String ssid = call.getString("ssid");
        WifiConfiguration conf = new WifiConfiguration();

        conf.SSID = "\"" + ssid + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext()
                .getSystemService(getContext().getApplicationContext().WIFI_SERVICE);
        wifiManager.addNetwork(conf);

      /*  int netId = wifiManager.addNetwork(conf);
        Log.d("Blink ", "Net ID = " + netId );
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
*/
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSObject ret = new JSObject();
        ret.put("result", "BlinkConnected");
        call.success(ret);
    }

    @PluginMethod()
    public void sendWifiLoginToMagnet(final PluginCall call) {
        final String ssid = call.getString("ssid");
        final String password = call.getString("password");
        Log.d("Blink", "SOCKET ATTEMPT");

        try {
            Socket socket = new Socket("192.168.4.22", 80);

            OutputStream out = socket.getOutputStream();
            PrintWriter output = new PrintWriter(out);

            String packet = ssid.length() + " " + password.length() + " " + ssid + password;
            output.println(packet);
            output.flush();
            output.close();
            out.flush();
            out.close();
            socket.close();

        }
        catch (IOException e) {

        }
       /* try {
            Socket socket = new Socket("192.168.4.22", 80);
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            String packet = ssid.length() + " " + password.length() + " " + ssid + password;
            DOS.writeUTF(packet);
            socket.close();

            JSObject ret = new JSObject();
            ret.put("result", "BlinkSetupSuccessful");
            call.success(ret);
        }
        catch (IOException exception) {

        }*/
       /*GreetClient client = new GreetClient();
        try {
            Thread.sleep(5000);

            String packet = ssid.length() + " " + password.length() + " " + ssid + password;
            client.startConnection("192.168.4.22", 80);
            //client.sendMessage(packet);
            Thread.sleep(5000);
            client.stopConnection();
        }
        catch (IOException | InterruptedException error) {
            Log.d("Blink", String.valueOf(error));
        }*/



       /* URI uri;
        try {
            uri = new URI("ws://192.168.4.22:80");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }


        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {

                System.out.println(" BLINK onOpen");

                String packet = ssid.length() + " " + password.length() + " " + ssid + password;
                webSocketClient.send(packet);

                JSObject ret = new JSObject();
                ret.put("result", "BlinkSetupSuccessful");
                call.success(ret);
            }

            @Override
            public void onTextReceived(String message) {
                System.out.println("BLINK onTextReceived");

                JSObject ret = new JSObject();
                ret.put("result", "BlinkSetupSuccessful");
                call.success(ret);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                System.out.println("BLINK onBinaryReceived");

                JSObject ret = new JSObject();
                ret.put("result", "BlinkSetupSuccessful");
                call.success(ret);
            }

            @Override
            public void onPingReceived(byte[] data) {
                System.out.println("BLINK onPingReceived");

                JSObject ret = new JSObject();
                ret.put("result", "BlinkSetupSuccessful");
                call.success(ret);
            }

            @Override
            public void onPongReceived(byte[] data) {
                System.out.println("BLINK onPongReceived");

                JSObject ret = new JSObject();
                ret.put("result", "BlinkSetupSuccessful");
                call.success(ret);
            }

            @Override
            public void onException(Exception e) {
                System.out.println( "BLINK " + e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                System.out.println("BLINK onCloseReceived");

                JSObject ret = new JSObject();
                ret.put("result", "BlinkSetupSuccessful");
                call.success(ret);
            }
        };

        webSocketClient.setConnectTimeout(1000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(1000);
        webSocketClient.connect();



*/

    }
}
//
//
//class GreetClient {
//    private Socket clientSocket;
//    private PrintWriter out;
//    private BufferedReader in;
//
//    public void startConnection(String ip, int port) throws IOException {
//        clientSocket = new Socket(ip, port);
//        out = new PrintWriter(clientSocket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//    }
//
//    public String sendMessage(String msg) throws IOException {
//        out.println(msg);
//        String resp = in.readLine();
//        return resp;
//    }
//
//    public void stopConnection() throws IOException {
//        in.close();
//        out.close();
//        clientSocket.close();
//    }
//}
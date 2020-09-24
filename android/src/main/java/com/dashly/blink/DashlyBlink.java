package com.dashly.blink;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.style.IconMarginSpan;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;
import static androidx.core.content.ContextCompat.getSystemService;

@NativePlugin(
        requestCodes={1}
)
public class DashlyBlink extends Plugin {
    PluginCall permissionCall;
    private WebSocketClient webSocketClient;
    private WifiReceiver wifiReceiver;
    private Network blinkNetwork;

    @PluginMethod()
    public void isBlinkSupported(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("result", true);
        call.success(ret);
    }

    @PluginMethod()
    public void enableLocationSevices(PluginCall call) {
        Log.d("Blink", "Enable Location services");
        pluginRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION,1);
        permissionCall = call;
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions,
                                               int[] grantResults) {

        Log.d("Blink", "Enable Location services RESULT: " + requestCode);

        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Blink", "PERMISSION_GRANTED");

                    JSObject ret = new JSObject();
                    ret.put("result", "authorizedAlways");

                    if (permissionCall != null)
                        permissionCall.success(ret);

                }  else {

                    Log.d("Blink", "PERMISSION_DENIED");
                    JSObject ret = new JSObject();
                    ret.put("result", "denied");

                    if (permissionCall != null)
                        permissionCall.success(ret);
                }
                return;
        }
    }

    @PluginMethod()
    public void isLocationServicesEnabled(PluginCall call) {
        JSObject ret = new JSObject();
        Log.d("Blink", "Has Location services enables" + hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));

        // Note
        // We don't ever pass back "BlinkLocationDenied" because it is not possible
        // to permanently deny permission like it is on iOS
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            ret.put("result", "BlinkLocationEnabled");
        else {
            ret.put("result", "BlinkLocationNotEnabled");
        }
        call.success(ret);
    }

    private String currentSSID() {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext()
                .getSystemService(getContext().getApplicationContext().WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = "";
        if (info != null) {
            ssid = info.getSSID();
        }
        return ssid;
    }

    @PluginMethod()
    public void getCurrentWifiSSID(PluginCall call) {

        String ssid = currentSSID();
        Log.d("Blink", "Current Home SSID: " + ssid);

        // If unknown ssid return a blank string.  This means the user has
        // turned of location for the whole device
        if (ssid.compareTo("unknown ssid") == 0) {
            JSObject ret = new JSObject();
            ret.put("result", "");
            call.success(ret);
        }
        else {
            JSObject ret = new JSObject();
            ret.put("result", ssid.substring(1, ssid.length()-1));
            call.success(ret);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @PluginMethod()
    public void connectToMagnet(final PluginCall call) {

        final String ssid = call.getString("ssid");


        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid).build();

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(TRANSPORT_WIFI) // we want WiFi
                .removeCapability(NET_CAPABILITY_INTERNET) // Internet not required
                .setNetworkSpecifier(specifier) // we want _our_ network
                .build();

        final android.net.ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                blinkNetwork = network;

                Log.d("Blink", "Network is: " + network.toString());

                String currentSsid = currentSSID();

                if (ssid.compareTo(currentSsid) == 0) {
                    Log.d("Blink", "Connected to dashly blink: " + currentSsid);
                }
                // bind so all api calls are performed over this new network
                //connectivity.bindProcessToNetwork(network);

                JSObject ret = new JSObject();
                ret.put("result", "BlinkOK");
                call.success(ret);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Log.d("Blink", "onUnavailable");

                call.reject("Blink102");
            }
        };

        connectivity.requestNetwork(request, networkCallback);

        /*final Intent i = new Intent(Settings.ACTION_WIFI_IP_SETTINGS);
        final PackageManager mgr = getContext().getPackageManager();
        if( mgr.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null)
        {
            getContext().startActivity(i);
        }*/

        /*WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid).build();

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(TRANSPORT_WIFI) // we want WiFi
                .removeCapability(NET_CAPABILITY_INTERNET) // Internet not required
                .setNetworkSpecifier(specifier) // we want _our_ network
                .build();

        final android.net.ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);

                Log.d("Blink", "onAvailable");

                // bind so all api calls are performed over this new network
                connectivity.bindProcessToNetwork(network);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Log.d("Blink", "onUnavailable");
            }
        };

        connectivity.requestNetwork(request, networkCallback);
        */



        /*this.wifiReceiver = new WifiReceiver();
        getContext().registerReceiver(this.wifiReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));



        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext()
                .getSystemService(getContext().getApplicationContext().WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }


        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int netId = wifiManager.addNetwork(conf);

          List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                    //wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    //wifiManager.reconnect();

                    break;
                }
            }*/






//        try {
//            Thread.sleep(4000);
//            /*try {
//                Socket socket = new Socket("192.168.4.22", 80);
//
//                OutputStream out = socket.getOutputStream();
//                PrintWriter output = new PrintWriter(out);
//
//                String packet =  "8" + " " + "10" + " " + "SKYC1207" + "WVSXVSRDYC";
//                output.println(packet);
//                output.flush();
//                output.close();
//                out.flush();
//                out.close();
//                socket.close();
//
//            }
//            catch (IOException e) {
//
//            }*/
//
//            try {
//                // Establish connection
//                Socket socket = new Socket("192.168.4.22", 80);
//
//                // Request data
//                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//                String data = "8" + " " + "10" + " " + "SKYC1207" + "WVSXVSRDYC";
//                Log.d("Blink", "Sent packet : " + data);
//                //outputStream.writeUTF(data);
//
//                outputStream.writeChars(data);
//                //outputStream.(data);
//
//                outputStream.flush();
//
//                // Shut down socket
//                socket.shutdownInput();
//                socket.shutdownOutput();
//                socket.close();
//            } catch (IOException io) {
//                Log.d("Blink", "Problem : " + io.getMessage());
//                io.printStackTrace();
//            }
//
//            Thread.sleep(4000);
//
//        } catch (InterruptedException e) {
//            Log.d("Blink", "Problem 2: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//
//        wifiManager.disconnect();
//        wifiManager.removeNetwork(netId);

//        JSObject ret = new JSObject();
//        ret.put("result", "BlinkConnected");
//        call.success(ret);
    }

    @PluginMethod()
    public void sendWifiLoginToMagnet(final PluginCall call) {
        final String ssid = call.getString("ssid");
        final String password = call.getString("password");


        String currentSsid = currentSSID();
        Log.d("Blink", "SSID connected for socket is: " + currentSsid);

        Socket socket = null;
        OutputStreamWriter osw;
        try {
            socket = this.blinkNetwork.getSocketFactory().createSocket("192.168.4.22", 80);
            Log.d("Blink", "Socket Created");

            String packet = ssid.length() + " " + password.length() + " " + ssid + password;
            Log.d("Blink", "Packet: " + packet);

            osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            osw.write(packet, 0, packet.length());

        } catch (UnknownHostException e) {
            System.err.print(e);
            e.printStackTrace();
            Log.d("Blink", "Error creating socket UnknownHostException: " + e.getMessage());

        } catch (IOException e) {
            System.err.print(e);
            e.printStackTrace();
            Log.d("Blink", "Error creating socket IOException: " + e.getMessage());

        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Blink", "Error closing socket: " + e.getMessage());
            }
        }

        try {
            Thread.sleep(3000);
            Log.d("Blink", "SSID connected after socket is: " + currentSSID());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       /* try {
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
*/
 /*       try {
            try {
                Thread.sleep(5000);
                // Establish connection
                Socket socket = new Socket("192.168.4.22", 80);

                // Request data
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                String packet = ssid.length() + " " + password.length() + " " + ssid + password;
                Log.d("Blink", "Sent packet : " + packet);
                //outputStream.writeUTF(packet);
                outputStream.writeChars(packet);

                outputStream.flush();

                // Shut down socket
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();

                JSObject ret = new JSObject();
                ret.put("result", "BlinkOK");
                call.success(ret);


            } catch (IOException io) {
                Log.d("Blink", "Problem : " + io.getMessage());
                io.printStackTrace();

                call.reject("Blink104");
            }
        }
        catch (InterruptedException error) {
            Log.d("Blink", String.valueOf(error));
        }
*/
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


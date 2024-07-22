package com.bedetaxi.bedetaxi.Routing;

/**
 * Created by LENOVO on 7/9/2017.
 */


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class SignalRHubConnection extends Service{
    // HubConnection
    public static HubConnection mHubConnection;
    public static HubProxy mHubProxy;
    public static String connectionID;

    public SignalRHubConnection() {

    }

    //
     /*
    This function try to connect with chat hub and return connection ID.
     */
    public static void startSignalR() {

            try {
                Platform.loadPlatformComponent(new AndroidPlatformComponent());
                mHubConnection = new HubConnection("http://bedetaxi.cloudapp.net");
                mHubProxy = mHubConnection.createHubProxy("BedeTaxiHub");
                ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
                SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);
                signalRFuture.get();
                //set connection id
                connectionID = mHubConnection.getConnectionId();
                // To get onLine user list
            }catch (InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static String getConnectionID() {
        return connectionID;
    }
}

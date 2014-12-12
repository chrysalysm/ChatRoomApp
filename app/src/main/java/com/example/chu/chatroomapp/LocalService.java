package com.example.chu.chatroomapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/*
TruTel Communications - August 2014
Created by Benjamin Chu - Simple G2Sky Chat Application w/ XMPP Connection to eJabberd server
 */
public class LocalService extends Service {

    private static final String TAG = "LocalService";
    Lobby lobbyActivity;
    XMPPConnection connection;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
        void setListener(Lobby listener) {
            lobbyActivity = listener;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service is starting");
        return START_STICKY;
    }

    public void connectToServer(final String host, final int port, final String service, final String username, final String password) {
        Log.i(TAG, "Connection is setting up...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(host, port, service);
                connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

                connection = new XMPPTCPConnection(connConfig);
                Log.i(TAG, "[LocalService] Connecting to " + connection.getHost());
                //SASLAuthentication.unregisterSASLMechanism("DIGEST-MD5");
                //SASLAuthentication.unregisterSASLMechanism("PLAIN");
                //SASLAuthentication.unsupportSASLMechanism("DIGEST-MD5");
                //SASLAuthentication.supportSASLMechanism("DIGEST-MD5");
                //connConfig.setSASLAuthenticationEnabled(true);

                // try connection
                try {
                    connection.connect();
                    Log.i(TAG, "Reporting XMPPConnection ID: " + connection);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // if connected try login
                try {
                    connection.login(username, password);
                    Log.i(TAG, "Logged in as " + connection.getUser());
                    Log.i(TAG, "Reporting XMPPConnection ID: " + connection);
                    Lobby.loggedIn = 1;
                    // Set the status to available
                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendPacket(presence);
                    lobbyActivity.connectToMUC();

                    // THE FOLLOWING CODE ONLY WORKS FOR CHAT MESSAGES DIRECTED AT THE USER FROM CONNECTION NOT CHAT ROOM
                    PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
                    connection.addPacketListener(new PacketListener() {

                        public void processPacket(Packet packet) {
                            Message message = (Message) packet;
                            if (message.getBody() != null) {

                                String fromName = StringUtils.parseBareAddress(message.getFrom());
                                Log.i(TAG, "Got text [" + message.getBody() + "] from [" + fromName + "]");

                                // Only the original thread that created a view hierarchy can touch its views.
                                lobbyActivity.messagePass(fromName, message.getBody(), message);
                            }
                        }
                    }, filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnectFromServer() {
        //connection.disconnect();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service is destroyed");
        try {
            connection.disconnect();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service is created");
    }
}


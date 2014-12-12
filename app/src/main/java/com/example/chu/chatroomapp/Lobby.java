package com.example.chu.chatroomapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;

import java.util.Collection;
import java.util.List;

/*
TruTel Communications - August 2014
Created by Benjamin Chu - Simple G2Sky Chat Application w/ XMPP Connection to eJabberd server
 */

public class Lobby extends Activity implements chatFragment.OnFragmentInteractionListener {

    public LocalService mService;
    private boolean mBound = false;
    private XMPPConnection connectionActivity = null;
    private Handler mHandler = new Handler();
    private static final String TAG = "LobbyActivity";
    protected chatFragment mFragment = null;
    MultiUserChat muc = null;

    public static int loggedIn = 0;

    protected static String userName;
    private static String password;
    private static String serverAddress;
    private static int portNumber;
    protected static int lengthOfRoom;
    protected static String listOfNames;

    /*
    BUTTON CODE FOR WHAT THE BUTTON WITHIN CHAT FRAGMENT DOES.
     */
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "Fragment Interaction! Button clicked.");
    }

    /*
    TEST FUNCTION CODE FOR LOCALSERVICE TO EXECUTE
     */
    public void messagePass(String fromName, String messageBody, Message incomingMessage) {
        mFragment = (chatFragment) getFragmentManager().findFragmentByTag("TAGNAME");
        mFragment.smackMessages.add(incomingMessage);
        mHandler.post(new Runnable() {
            public void run() {
                mFragment.notifyAdapter();
            }
        });
    }

    /*
    FUNCTION TO SEND MESSAGE TO MUC
     */
    public void sendMessageMUC(String message) {
        Message message1 = new Message();
        message1.setBody(message);
        message1.setFrom(userName);
        message1.setType(Message.Type.groupchat);
        message1.setTo("lobby@conference.ejabberd.server.com");

        try {
            muc.sendMessage(message1);
            Log.i(TAG, "IS IT EVEN SENDING? THIS MEANS YES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    ONCREATE CODE FOR WHEN THE LOBBY ACTIVITY IS INITIALLY CREATED
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new chatFragment(), "TAGNAME").commit();
        }
    }

    /*
    CODE FOR MENU FORMATTING AND INFLATION
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lobby, menu);
        return true;
    }

    /*
    CODE TO DEFINE WHAT HAPPENS WHEN THE MENU ITEM IS SELECTED
    IN THIS PARTICULAR CASE IT IS FOR THE LOGOUT BUTTON
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.i(TAG, "CLOSE THE SERVICE");
            Log.i(TAG, "FINISH THE ACTIVITY");
            Log.i(TAG, "GO TO THE LOGIN SCREEN"); // done
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    ACTIVITY LIFECYCLE CALL BACK METHODS ARE STORED BELOW
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "Entered the onStart() method.");
        Intent intent = new Intent(this, LocalService.class);
        if (bindService(intent, mConnection, Context.BIND_AUTO_CREATE)) {
            Log.i(TAG, "Service was bound");
        } else {
            Log.i(TAG, "Service was not bound");
        }
    }

    /*
    NECESSARY CODE FOR BINDING ACTIVITY TO LOCAL SERVICE
    Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            binder.setListener(Lobby.this);
            mBound = true;
            Intent incomingIntent = getIntent();
            userName = incomingIntent.getStringExtra("KeyName");
            password = incomingIntent.getStringExtra("KeyPass");
            serverAddress = incomingIntent.getStringExtra("KeyAddress");
            portNumber = incomingIntent.getIntExtra("KeyPortNum", 5222);
            Log.i(TAG, "Intent captured: " + userName + " and " + password + " and " + serverAddress);
            Log.i(TAG, "What is the mService value: " + mService);
            if (mService.connection == null && loggedIn == 0) {
                mService.connectToServer(serverAddress, portNumber, serverAddress, userName, password);
            } else {
                Log.i(TAG, "Connection already exists : " + mService.connection);
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    PacketListener mucPacket = new PacketListener() {
        @Override
        public void processPacket(Packet packet) throws SmackException.NotConnectedException {
            Message message = (Message) packet;
            if (message.getBody() != null) {
                String fromName = StringUtils.parseName(message.getFrom());
                Log.i(TAG, "Got text [" + message.getBody() + "] from [" + fromName + "]");
                //messagePass(fromName, message.getBody());

                Log.i(TAG, "Capture information for Message: " + message.getFrom());

                String input = message.getFrom().toString();
                lengthOfRoom = muc.getRoom().toString().length();
                Log.i(TAG, "Name Extracted is: " + input.substring(lengthOfRoom + 1));
                Log.i(TAG, "Length of room is: " + lengthOfRoom);
                messagePass(input.substring(lengthOfRoom + 1), message.getBody(), message);

            }
        }
    };

    public void connectToMUC() {
        // Multi User Chat connection code
        connectionActivity = mService.connection;
        Log.i(TAG, "Connecting to lobby...");
        muc = new MultiUserChat(connectionActivity, "lobby@conference.ejabberd.server.com");
        try {
            if (muc.createOrJoin(userName)) {
                Log.i(TAG, "Created/Joined the Lobby");
            } else {
                Log.i(TAG, "Failed to Create/Join the Lobby");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (muc.isJoined()) {
            Log.i(TAG, userName + " has joined the lobby");
        } else {
            Log.i(TAG, userName + " has not joined the lobby");
        }

        try {
            muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
        } catch (Exception e) {
            e.printStackTrace();
        }



        muc.addMessageListener(mucPacket);

        // Some Debug information collected about the hosted rooms
        Collection<HostedRoom> rooms = null;
        try {
            rooms = muc.getHostedRooms(connectionActivity, "conference.ejabberd.server.com");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "List of hosted rooms: " + rooms.iterator().next().getName());
        Log.i(TAG, "ID of Hosted Rooms: " + rooms.iterator().next().getJid());

        RoomInfo info = null;
        try {
            info = muc.getRoomInfo(connectionActivity, "lobby@conference.ejabberd.server.com");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Number of Occupants in Room: " + info.getOccupantsCount());
        Log.i(TAG, "The Name of the room is: " + muc.getRoom());

        mFragment = (chatFragment) getFragmentManager().findFragmentByTag("TAGNAME");

        List<String> occupantList;
        occupantList = muc.getOccupants();
        int numberOccupants = info.getOccupantsCount();

        StringBuilder nameList = new StringBuilder();
        nameList.append("The following users are connected: ");

        for (int i = 0; i < numberOccupants; i++) {
            Log.i(TAG, "Who is in this room: " + occupantList.get(i));
            String name = occupantList.get(i);
            nameList.append(name.substring(muc.getRoom().toString().length() + 1));
            nameList.append(" ");
        }

        listOfNames = nameList.toString();

        Message welcomeMessage = new Message();
        Log.i(TAG, "SENDING MESSAGE TO ADAPTER");
        welcomeMessage.setBody(listOfNames);
        welcomeMessage.setFrom("Welcome to the G2 Sky Lobby");
        welcomeMessage.setType(Message.Type.headline);

        Message joinMessage = new Message();
        joinMessage.setBody("I have joined the lobby.");
        joinMessage.setTo("lobby@conference.ejabberd.server.com");
        joinMessage.setType(Message.Type.groupchat);

        mFragment.smackMessages.add(welcomeMessage);

        try {
            muc.sendMessage(joinMessage);
            Log.i(TAG, "IS IT SENDING THE JOIN MESSAGE?");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mHandler.post(new Runnable() {
            public void run() {
                mFragment.notifyAdapter();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Entered the onResume() method.");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Entered the onPause() method.");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "Entered the onStop() method.");
        if (mBound) {
            Log.i(TAG, "Unbinding Service");
            unbindService(mConnection);
            mBound = false;
        }
    }
    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(TAG, "Entered the onRestart() method.");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Entered the onDestroy() method.");
        mService.connection = null;
        loggedIn = 0;
        mService.disconnectFromServer();
        muc.removeMessageListener(mucPacket);
    }




}

package org.appspot.apprtc;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.ThreadUtils;

import java.util.concurrent.ExecutorService;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by nikhil on 9/1/17.
 */

public class SocketIOChannelClient {
    public static String CHAT_SERVER_URL="http://35.164.196.224";
    private Socket mSocket;
    ExecutorService executor;
    ThreadUtils.ThreadChecker executorThreadCheck;
    SocketIOChannelEvents eventListener;

    public interface SocketIOChannelEvents{
        void onConnect();
        void onDisconnect();
        void onConnectError();
        void onLineUserNames(JSONArray usernames);
        void onIncomingCall(String from,String sdp);
        void onRingingResponse(String from);
        void onRemoteAnswer(String from,String sdp);
        void onAck(String from);
    }

    SocketIOChannelClient(Activity activity, SocketIOChannelEvents eventListener){
        this.executor=executor;
      /*  executorThreadCheck=new ThreadUtils.ThreadChecker();
        executorThreadCheck.detachThread();*/
        this.eventListener=eventListener;
        SocketIOApplication app=(SocketIOApplication)activity.getApplication();
        mSocket=app.getSocket();
        if(!mSocket.connected()) {
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("usernames", onlineUserNames);
            mSocket.on("offer", onIncomingCall);
            mSocket.on("ringing", onRingingResponse);
            mSocket.on("answer", onRemoteAnswer);
            mSocket.on("ack", onAcknowledgement);
            mSocket.connect();
        }
    }

    public void sendMessage(String type,JSONObject message){
        mSocket.emit(type,message);
    }
    public void sendMessage(String type,JSONObject message,Ack ack){
        mSocket.emit(type,message,ack);
    }

    public boolean isConnected(){
        return mSocket.connected();
    }

    public void loginAttempt(String username, Ack ack){
        mSocket.emit("new user",username,ack);
    }

    private Emitter.Listener onConnect=new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            eventListener.onConnect();
        }
    };

    private Emitter.Listener onDisconnect=new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            eventListener.onDisconnect();
        }
    };

    private Emitter.Listener onConnectError=new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            eventListener.onConnectError();
        }
    };

    private Emitter.Listener onlineUserNames=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            eventListener.onLineUserNames((JSONArray) args[0]);
        }
    };

    private Emitter.Listener onIncomingCall=new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            JSONObject jsonObject=(JSONObject) args[0];
            try {
                String from=jsonObject.getString("from");
                String sdp=jsonObject.getString("sdp");
                eventListener.onIncomingCall(from,sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    };

    private Emitter.Listener onRingingResponse=new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            eventListener.onRingingResponse("");
                    }
    };

    private Emitter.Listener onRemoteAnswer=new Emitter.Listener(){

        @Override
        public void call(Object... args) {
           eventListener.onRemoteAnswer("","");
        }
    };


    private Emitter.Listener onAcknowledgement=new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            eventListener.onAck("");
        }
    };












}

package org.appspot.apprtc;

import android.app.Activity;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import io.socket.client.Ack;

/**
 * Created by nikhil on 9/1/17.
 */

public class SocketIORTCClient implements AppRTCClient,SocketIOChannelClient.SocketIOChannelEvents {
    private static final String TAG = "WSRTCClient";

    private enum ConnectionState { NEW, CONNECTED, CLOSED, ERROR }
    private ConnectionState roomState;
    SocketIOChannelClient channelClient;
    private String username;
    private String toUsername;
    Activity activity;

    SocketIOConnectionListener socketIOConnectionListener;
    SocketIOIncomingListener socketIOIncomingListener;
    SocketIOOutgoingLIstener socketIOOutgoingLIstener;
    SignalingEvents signalingEvents;

    public interface SocketIOConnectionListener{
        void onConnect();
        void onDisconnect();
        void onConnectError();
        void onLineUserNames(JSONArray usernames);
        void onIncomingCall(String from,String sdp);
    }

    public interface SocketIOIncomingListener{
        void onAck(String from);
    }

    public interface SocketIOOutgoingLIstener{
        void onRingingResponse(String from);
        void onRemoteAnswer(String from,String sdp);
    }

    SocketIORTCClient(SocketIOApplication socketIOApplication){
        roomState=ConnectionState.NEW;
        channelClient=new SocketIOChannelClient(socketIOApplication,this);
       /* this.username=username;
        this.toUsername=toUsername;*/

    }

    public void setSocketIOConnectionListener(SocketIOConnectionListener socketIOConnectionListener) {
        this.socketIOConnectionListener = socketIOConnectionListener;
    }


    public void setSocketIOIncomingListener(SocketIOIncomingListener socketIOIncomingListener) {
        this.socketIOIncomingListener = socketIOIncomingListener;
    }

    public void setSocketIOOutgoingLIstener(SocketIOOutgoingLIstener socketIOOutgoingLIstener) {
        this.socketIOOutgoingLIstener = socketIOOutgoingLIstener;
    }


    public void setSignalingEvents(SignalingEvents signalingEvents) {
        this.signalingEvents = signalingEvents;
    }

    Ack errorMessageAck=new Ack() {
        @Override
        public void call(final Object... args) {

        }
    };
    @Override
    public void connectToRoom(RoomConnectionParameters connectionParameters) {

    }

    @Override
    public void sendOfferSdp(SessionDescription sdp) {
        Log.e("Offer sdp",sdp.description);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("from",username);
            jsonObject.put("to",toUsername.trim());
            jsonObject.put("sdp",sdp.description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        channelClient.sendMessage("offer",jsonObject,errorMessageAck);
    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp) {
        Log.e("sendAnswerSdp", sdp.description);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("from",username);
            jsonObject.put("to",toUsername);
            jsonObject.put("sdp",sdp.description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        channelClient.sendMessage("answer",jsonObject,errorMessageAck);

    }

    @Override
    public void sendLocalIceCandidate(IceCandidate candidate) {
        Log.e("Ice candidate send:",candidate.toString());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("from",username);
            jsonObject.put("to",toUsername);
            jsonObject.put("sdpMid",candidate.sdpMid);
            jsonObject.put("sdpMLineIndex",candidate.sdpMLineIndex);
            jsonObject.put("sdp",candidate.sdp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        channelClient.sendMessage("ice",jsonObject);

    }

    @Override
    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {
        Log.e("Ice to remove (size):",candidates.length+"");
        for(int i=0;i<candidates.length;i++){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("from",username);
                jsonObject.put("to",toUsername);
                jsonObject.put("sdpMid",candidates[i].sdpMid);
                jsonObject.put("sdpMLineIndex",candidates[i].sdpMLineIndex);
                jsonObject.put("sdp",candidates[i].sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            channelClient.sendMessage("iceremove",jsonObject);
        }
    }

    @Override
    public void disconnectFromRoom() {

    }

    @Override
    public void onConnect() {
        if(socketIOConnectionListener!=null)
            socketIOConnectionListener.onConnect();
        else{
            try {
                Thread.sleep(1000);
                socketIOConnectionListener.onConnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisconnect() {
        socketIOConnectionListener.onDisconnect();
    }

    @Override
    public void onConnectError() {
        socketIOConnectionListener.onConnectError();
    }

    @Override
    public void onLineUserNames(JSONArray usernames) {
        socketIOConnectionListener.onLineUserNames(usernames);
    }

    @Override
    public void onIncomingCall(String from, String sdp) {
        Log.e("onIncomingCall",sdp);
        socketIOConnectionListener.onIncomingCall(from,sdp);

    }

    @Override
    public void onRingingResponse(String from) {
        Log.e("onRingingResponse","Ringing "+from);

    }

    @Override
    public void onRemoteAnswer(String from, String sdp) {
        Log.e("onRemoteAnswer",sdp);
        SessionDescription sessionDescription=new SessionDescription(SessionDescription.Type.ANSWER,sdp);
        signalingEvents.onRemoteDescription(sessionDescription);

    }

    @Override
    public void onRemoteIceCandidate(String from, IceCandidate iceCandidate) {
        if(signalingEvents!=null)
            signalingEvents.onRemoteIceCandidate(iceCandidate);
        else{
            PeerConnectionClient.getInstance().addRemoteIceCandidate(iceCandidate);
        }

    }

    @Override
    public void onRemoteIceRemovals(String from, String candidate) {

    }

    @Override
    public void onAck(String from) {

    }


    public void checkLogin(String username, Ack ack){
        channelClient.loginAttempt(username,ack);
    }

    public boolean isConnected(){
        return channelClient.isConnected();
    }
    public void setUsername(String username){
        this.username=username;
    }
    public void setToUsername(String toUsername){
        this.toUsername=toUsername;
    }
}

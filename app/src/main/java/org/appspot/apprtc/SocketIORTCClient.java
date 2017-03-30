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
    private SignalingEvents events;
    private enum ConnectionState { NEW, CONNECTED, CLOSED, ERROR }
    private ConnectionState roomState;
    SocketIOChannelClient channelClient;
    private String username;
    private String toUsername;
    Activity activity;

    SocketIORTCClient(Activity activity,SignalingEvents events,String username,String toUsername){
        this.events=events;
        this.activity=activity;
        roomState=ConnectionState.NEW;
        channelClient=new SocketIOChannelClient(activity,this);
        this.username=username;
        this.toUsername=toUsername;

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

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnectError() {

    }

    @Override
    public void onLineUserNames(JSONArray usernames) {

    }

    @Override
    public void onIncomingCall(String from, String sdp) {
        Log.e("onIncomingCall",sdp);

    }

    @Override
    public void onRingingResponse(String from) {
        Log.e("onRingingResponse","Ringing "+from);

    }

    @Override
    public void onRemoteAnswer(String from, String sdp) {
        Log.e("onRemoteAnswer",sdp);
        SessionDescription sessionDescription=new SessionDescription(SessionDescription.Type.ANSWER,sdp);
        events.onRemoteDescription(sessionDescription);

    }

    @Override
    public void onAck(String from) {

    }
}

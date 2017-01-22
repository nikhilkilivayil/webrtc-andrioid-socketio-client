package org.appspot.apprtc;

import android.app.Activity;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONArray;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Created by nikhil on 9/1/17.
 */

public class SocketIORTCClient implements AppRTCClient,SocketIOChannelClient.SocketIOChannelEvents {
    private static final String TAG = "WSRTCClient";
    private SignalingEvents events;
    private enum ConnectionState { NEW, CONNECTED, CLOSED, ERROR }
    private ConnectionState roomState;
    SocketIOChannelClient channelClient;

    SocketIORTCClient(Activity activity,SignalingEvents events){
        this.events=events;
        roomState=ConnectionState.NEW;
        channelClient=new SocketIOChannelClient(activity,this);

    }

    @Override
    public void connectToRoom(RoomConnectionParameters connectionParameters) {

    }

    @Override
    public void sendOfferSdp(SessionDescription sdp) {

        Log.e("Offer sdp",sdp.description);

    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp) {

    }

    @Override
    public void sendLocalIceCandidate(IceCandidate candidate) {
        Log.e("Ice candidate",candidate.toString());

    }

    @Override
    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {

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

    }

    @Override
    public void onRingingResponse(String from) {

    }

    @Override
    public void onRemoteAnswer(String from, String sdp) {

    }

    @Override
    public void onAck(String from) {

    }
}

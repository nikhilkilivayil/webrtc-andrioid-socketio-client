package org.appspot.apprtc;

import android.app.Application;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
/**
 * Created by nikhil on 9/1/17.
 */

public class SocketIOApplication extends Application {
    private Socket mSocket;
    {
        try {
            IO.Options options=new IO.Options();
//            options.hostname="http://35.164.196.224";
//            options.port=80;
            options.forceNew=true;
            options.reconnection=false;

            mSocket = IO.socket(SocketIOChannelClient.CHAT_SERVER_URL,options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}

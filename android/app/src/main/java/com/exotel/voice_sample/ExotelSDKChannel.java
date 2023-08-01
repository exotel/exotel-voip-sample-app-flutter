package com.exotel.voice_sample;



import android.content.Context;


import com.exotel.voice.Call;

import java.util.Date;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

//Exotel Channel Class
// it is a Channel class b/w flutter and native.
public class ExotelSDKChannel implements VoiceAppStatusEvents,CallEvents {
    private static final String CHANNEL = "android/exotel_sdk";
    private static final String TAG = "ExotelSDKChannel";
    FlutterEngine flutterEngine;

    private VoiceAppService mService;
    private boolean mBound;

    private String appHostname;
    private String accountSid;
    private String username;
    private String password;

    private Context context;
    private MethodChannel channel;
    private Call call;

    /**
     * Constructor
     * @param flutterEngine flutter engine to register channel
     * @param context activity context
     */
    public ExotelSDKChannel(FlutterEngine flutterEngine, Context context) {
        this.flutterEngine = flutterEngine;
        this.context = context;
        mService = new VoiceAppService(context);
        /**
         * setting event listener to handle incomgin events from mediator class
         */
        mService.addStatusEventListener(ExotelSDKChannel.this);
        mService.addCallEventListener(ExotelSDKChannel.this);
        appHostname = "https://bellatrix.apac-sg.exotel.in/v1"; // [hard-coded]
        accountSid = "exotel675"; // [hard-coded]
    }

    /**
     * register channel for communication b/w flutter and android
     */
    void registerMethodChannel() {
        // Channel is created for communication b/w flutter and native
        channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL);
        // handle messages from flutter to android native
        channel.setMethodCallHandler(
                ((call, result) -> {
                    System.out.println("Entered in Native Android");
                    switch (call.method) {
                        case "login":
                            /**
                             * [sdk-initialization-flow] flutter invoking SDK initialization
                             */
                            login();
                            result.success("...");
                            break;
                        case "call":
                            call();
                            result.success("...");
                            break;
                        default:
                            System.out.println("fail");
                            result.notImplemented();
                            break;
                    }
                })
        );

    }

    /**
     * calling the dial number
     */
    private void call() {
        String dialNumber = ""; // [hard-coded]
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
        String contextMessage = sharedPreferencesHelper.getString(ApplicationSharedPreferenceData.CONTACT_DISPLAY_NAME.toString());
        String updatedDestination = mService.getUpdatedNumberToDial(dialNumber);
        try {
            call = mService.dial(updatedDestination, contextMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * will initialize the exotel client SDK
     */
    private void login() {

        username = ""; // [hard-coded]
        password = ""; // [hard-coded]
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
        sharedPreferencesHelper.putString(ApplicationSharedPreferenceData.USER_NAME.toString(),username);
        sharedPreferencesHelper.putString(ApplicationSharedPreferenceData.DISPLAY_NAME.toString(),username);
        sharedPreferencesHelper.putString(ApplicationSharedPreferenceData.APP_HOSTNAME.toString(),appHostname);
        sharedPreferencesHelper.putString(ApplicationSharedPreferenceData.ACCOUNT_SID.toString(),accountSid);
        sharedPreferencesHelper.putString(ApplicationSharedPreferenceData.PASSWORD.toString(),password);
        /**
         * [sdk-initialization-flow] calling mediator login API to get subscriber token
         */
        mService.login(appHostname, accountSid, username, password);
    }

    @Override
    public void onStatusChange() {
        VoiceAppLogger.debug(TAG, "Received On Status Change in LoginActivty");
        /**
         * [sdk-initialization-flow] sending message to flutter about the sdk inialization status
         */
        channel.invokeMethod("loggedInStatus",mService.getCurrentStatus().getMessage());
    }

    @Override
    public void onAuthFailure() {
        VoiceAppLogger.debug(TAG, "On Authentication failure");
        /**
         * [sdk-initialization-flow] sending message to flutter about the sdk inialization status
         * when authentication failed
         */
        channel.invokeMethod("loggedInStatus","Authentication Failed");
    }

    @Override
    public void onCallInitiated(Call call) {
        VoiceAppLogger.debug(TAG,"onCallInitiated");
    }

    @Override
    public void onCallRinging(Call call) {
        VoiceAppLogger.debug(TAG,"onCallRinging");
        /**
         * [sdk-calling-flow] sending message to flutter that dialer number is ringing
         */
        channel.invokeMethod("callStatus","Ringing");
    }

    @Override
    public void onCallEstablished(Call call) {
        VoiceAppLogger.debug(TAG,"onCallEstablished");
        /**
         * [sdk-calling-flow] sending message to flutter that call is connected
         */
        channel.invokeMethod("callStatus","Connected");
    }

    @Override
    public void onCallEnded(Call call) {
        VoiceAppLogger.debug(TAG,"onCallEnded");
        /**
         * [sdk-calling-flow] sending message to flutter that call is disconnected
         */
        channel.invokeMethod("callStatus","Ended");
    }

    @Override
    public void onMissedCall(String remoteUserId, Date time) {
        VoiceAppLogger.debug(TAG,"onMissedCall");
    }

    @Override
    public void onMediaDisrupted(Call call) {

    }

    @Override
    public void onRenewingMedia(Call call) {

    }
}
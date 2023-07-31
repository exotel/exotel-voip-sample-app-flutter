# Android integration with Flutter 

This is guide to integrate exotel SDK with flutter. 

In this integration , 

- **MethodChannnel** has been used for communication b/w flutter and native android code. 

- Here , Native Android code is mediator b/w flutter and exotel SDK which has integrated the exotel SDK as per [integration guide](https://github.com/exotel/exotel-voip-sdk-android/blob/main/Exotel%20Voice%20Client%20Android%20SDK%20Integration%20Guide.pdf) from [exotel-voip-sdk-android repo](https://github.com/exotel/exotel-voip-sdk-android).


### Communication through Method Channel

#### Flutter calls native android code

- Create a MethodChannel and register the channel name, generally using “***package name/identity***” as the channel name.
- An asynchronous call is initiated through **invokeMethod**.

    ```
    class _SampleAppState extends State<SampleApp> {

    static const androidChannel = MethodChannel('android/exotel_sdk');
    ...

    void logInButton() async{
        ...
        final String value = await androidChannel.invokeMethod('login');
        ...
    }

    void callButtonPressed() async {
        ...
        final String value = await androidChannel.invokeMethod('call');
        ...
    }
    ...
    }
    ```
Next, the following functions are implemented in native (android):
- Create a MethodChannel using the same registration string as the flutter.
- Implement and set the login.
- Return the result to flutter through result.

    ```
    public class MainActivity extends FlutterActivity {
        private ExotelSDKChannel exotelSDKChannel;
        @Override
        public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
            GeneratedPluginRegistrant.registerWith(flutterEngine);

            exotelSDKChannel = new ExotelSDKChannel(flutterEngine,this);
            exotelSDKChannel.registerMethodChannel();
        }
    }
    ```

    ```
    public class ExotelSDKChannel {
        private static final String CHANNEL = "android/exotel_sdk";
        private MethodChannel channel;
        public ExotelSDKChannel(FlutterEngine flutterEngine, Context context) {
            this.flutterEngine = flutterEngine;
            this.context = context;
        }
        channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL);
        channel.setMethodCallHandler(
                ((call, result) -> {
                    System.out.println("Entered in Native Android");
                    switch (call.method) {
                        case "login":
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
    ```

#### Native calls flutter

- The code implementation of android calling flutter is similar to that of flutter calling native (android) which via invokeMethod.
  ```
  public class ExotelSDKChannel {
    ...
    public void onStatusChange() {
        channel.invokeMethod("loggedInStatus",mService.getCurrentStatus().getMessage());
    }

    @Override
    public void onAuthFailure() {
        channel.invokeMethod("loggedInStatus","Authentication Failed");
    }

    @Override
    public void onCallRinging(Call call) {
        channel.invokeMethod("callStatus","Ringing");
    }

    @Override
    public void onCallEstablished(Call call) {
        channel.invokeMethod("callStatus","Connected");
    }

    @Override
    public void onCallEnded(Call call) {
        channel.invokeMethod("callStatus","Ended");
    }
  }
  ```
The flutter mainly implements the registration of MethodCallHandler:

    ```
    class _SampleAppState extends State<SampleApp> {
        static const androidChannel = MethodChannel('android/exotel_sdk');

        @override
        void initState() {
            super.initState();
            androidChannel.setMethodCallHandler(flutterCallHandler);
        }

        Future<String> flutterCallHandler(MethodCall call) async {
            switch (call.method) {  
            case "loggedInStatus":
                // update UI
                break;
            case "callStatus":
                // update UI
                break;
            default:
                break;
            }
            return "";
        }
    }
    ```


## Notes

- **MethodChannel** is used for demo / integration purpose. There  are other platform channels also also available which can be implemented as per design and use case.
  


---

Go to [README.md](README.md)

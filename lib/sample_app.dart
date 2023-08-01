import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class SampleApp extends StatefulWidget {
  const SampleApp({super.key, required this.title});

  final String title;

  @override
  State<SampleApp> createState() => _SampleAppState();
}

class _SampleAppState extends State<SampleApp> {
  String userStatus = "user status";
  String callStatus = "call status";
  // Method Channel for communication between android and flutter
  static const androidChannel = MethodChannel('android/exotel_sdk');
  @override
  void initState() {
    super.initState();
    // handle messages from android to flutter
    androidChannel.setMethodCallHandler(flutterCallHandler);
  }

  Future<String> flutterCallHandler(MethodCall call) async {
    String loginStatus = "not ready";
    String callingStatus = "blank";
   switch (call.method) {
      case "loggedInStatus":
        loginStatus =  call.arguments.toString();
        setState(() {
          userStatus = loginStatus;
        });
        break;
      case "callStatus":
        callingStatus =  call.arguments.toString();
        log(callingStatus);
        setState(() {
          callStatus = callingStatus;
        });
        break;
      default:
        break;
    }

    
    return "";
  }

  void logInButton() async{
    String response = "";
    try {
      // send message from flutter to android for exotel client SDK initialization
      final String value = await androidChannel.invokeMethod('login');
      response = value;
    } catch (e) {
      response = "Failed to Invoke: '${e.toString()}'.";
      log(response);
    }
    setState(() {
      userStatus = response;
    });
  }

  void callButtonPressed() async {
    String response = "";
    try {
      // send message from flutter to android for calling through exotel SDK
      final String value = await androidChannel.invokeMethod('call');
      response = value;
    } catch (e) {
      response = "Failed to Invoke: '${e.toString()}'.";
      log(response);
    }
    setState(() {
      callStatus = response;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextBox(txt: userStatus),
            const SizedBox(width: 50),
            ElevatedButton(
              child: const Text('login'),
              onPressed: () {
                logInButton();
              },
            ),
            TextBox(txt: callStatus),
            const SizedBox(width: 50),
            ElevatedButton(
              child: const Text('call'),
              onPressed: () {
                callButtonPressed();
              },
            ),
          ],
        ),
      ),
    );
  }
}

class TextBox extends StatelessWidget {
  const TextBox({
    super.key,
    required this.txt,
  });

  final String txt;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final style = theme.textTheme.displayMedium!.copyWith(
      color: theme.colorScheme.onPrimary,
    ); 
    return Card(
      color: theme.colorScheme.primary,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Text(txt,style: style),
      ),
    );
  }
}

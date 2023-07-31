# exotel_voice_sample

A basic sample Flutter project based on Exotel SDK.

## Getting Started

### check flutter configuartion 
```
flutter doctor
```

### clean flutter application
```
flutter clean
```

### install dependencies
```
flutter pub get
```

### upgrade dependencies
```
flutter pub upgrade
```

### run flutter app
```
flutter run
```

## Limitations

- as of now, sample UI only support login and calling .

- *Credentials are hard coded*

     **workaround** : please add credential in ExotelSDKChannel class of android project.
     ```
     public class ExotelSDKChannel {
         private void login() {
            ...
            username = "<username>"; // [hard-coded]
            password = "<password>"; // [hard-coded]
            ...
         }
     }
     ```
     To get the credential, please connect with exotel team.

- *Dial Number are hard coded*

     **workaround** : please add dial number in ExotelSDKChannel class of android project.
     ```
     public class ExotelSDKChannel {
         private void call() {
            String dialNumber = ""; // [hard-coded]
            ...
         }
     }
     ```

- *Ask permission code is not added yet*

    **workaround** : after app is installed, please enable all the permission(microphone , Phone, Notifications) from applicaions settings .



## How to integrate exotel android SDK to flutter 

please check out : [android_integration_guide.md](android_integration_guide.md)


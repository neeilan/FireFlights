# FireFlights
Real-time mock flight-booking app for Android

![alt text](https://github.com/neeilan/FireFlights/blob/master/raw/ffgif.gif "Gif")


To run:

1. Create a Firebase app and [add your Android project](https://firebase.google.com/docs/android/setup). 
2. Replace google-services.json with your own credentials.

I used a separate Node.js web app to upload user/flight info without rooting the device. If you wish to use it, replace firebaseServiceAccount.json with info for [your own service account](https://firebase.google.com/docs/admin/setup), and enter your Firebase app's url on line 12 of app.js.

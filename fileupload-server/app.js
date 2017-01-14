var express = require('express'),
    app = express(),
    port = process.env.PORT || 8080,
    multer = require('multer'),
    csv = require('csv'),
    bodyParser = require('body-parser'),
    firebaseServiceAccount = require('./firebaseServiceAccount.json'),
    firebase = require('firebase');

firebase.initializeApp({
    serviceAccount : firebaseServiceAccount, // gives administrative access
    databaseURL: 'https://YOUR_DB_URL_HERE.firebaseio.com', // you url here
});

var Users = firebase.database().ref().child('Users');
var Flights = firebase.database().ref().child('Flights');


app.use(express.static('public'))
var fileUpload = multer({ storage: multer.MemoryStorage });

app.post('/flights', fileUpload.single('file'),
    (req, res) => {
        res.writeHead(200, {'Content-Type': 'text/html'});

    try {
        var flightStrs = req.file.buffer.toString().split('\n');
        var flights = flightStrs.map(flightStr => flightStr.split(';'))
            .filter(flightInfoArray => flightInfoArray.length == 8)
            .map(flightInfoArray =>({
                flightNum : flightInfoArray[0],
                departsAt : flightInfoArray[1],
                arrivesAt : flightInfoArray[2],
                airline : flightInfoArray[3],
                origin : flightInfoArray [4],
                destination : flightInfoArray[5],
                cost : parseFloat(flightInfoArray [6]),
                numSeats : parseInt(flightInfoArray[7])
            }))
        // Check that dates are valid
        flights.forEach((flight)=>
            {new Date(flight.departsAt); new Date (flight.arrivesAt);});

        Promise.all(flights.map(flight => uploadFlightToFirebase(flight)))
        .then(()=>res.end('<script type="text/javascript">alert("Changes have been saved.")</script>'))
    }
    catch (e){
        res.end('<script type="text/javascript">alert("There was an error saving the data. Please ensure you are using the correct format, with valid dates.")</script>')
    }
})

app.post('/users', fileUpload.single('file'),
    (req, res) => {
    res.writeHead(200, {'Content-Type': 'text/html'});
    try {
        var userStrs = req.file.buffer.toString().split('\n');
        var users = userStrs.map(userStr => userStr.split(';'))
            .filter(userInfoArray => userInfoArray.length == 6)
            .map(userInfoArray =>({
                lastName : userInfoArray[0],
                firstName : userInfoArray[1],
                email : userInfoArray[2],
                address : userInfoArray[3],
                ccNum : userInfoArray [4],
                ccExpiry : userInfoArray[5]
            }))
        Promise.all(users.map(user => uploadUserToFirebase(user)))
        .then(()=>res.end('<script type="text/javascript">alert("Changes have been saved.")</script>'))

    }
    catch (e){
        res.end('<script type="text/javascript">alert("There was an error saving the data. Please ensure you are using the correct format.")</script>')
    }
})

function uploadUserToFirebase(user){
    var emailKey = user.email.toLowerCase().split('.').join('DOT').split('_').join('US').split('-').join('DASH');
    return Users.child(emailKey).set(user);
}

function uploadFlightToFirebase(flight){
    return Flights.child(flight.flightNum).set(flight);
}

app.listen(port, () => console.log('Listening on ' + port));

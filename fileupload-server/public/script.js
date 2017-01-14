$(document).ready(function(){

  // Firebase config
  var config = {
    apiKey: "",
    authDomain: "",
    databaseURL: "",
    storageBucket: "",
    messagingSenderId: ""
  };
  firebase.initializeApp(config);

  $("#signIn").click(function(){
      var email = $("#email").val();
      var password = $("#password").val();

      firebase.auth().signInWithEmailAndPassword(email, password)
          .then(function(user){
              if (user.email == "neeilans@live.com" || user.email == "tito@ku.com"){
                  showUploadsPage();
              }
              else {
                  alert("You are not an admin ."+user.email+".");
              }
;                    })
          .catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        if (errorCode === 'auth/wrong-password') {
          alert('Wrong password.');
        } else {
          alert(errorMessage);
        }
        console.log(error);
      });

  })

  function showUploadsPage(){
      $("#signInDiv").hide();
      $("#uploadsDiv").show();

  }
})

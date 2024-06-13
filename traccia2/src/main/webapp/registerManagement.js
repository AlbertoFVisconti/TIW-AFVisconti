(function() { // avoid variables ending up in the global scope

  document.getElementById("registerbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    // Retrieve password values
    var password = form.querySelector("input[name='pwd']").value;
    var repeatPassword = form.querySelector("input[name='rpwd']").value;

    // Check if passwords match
    if (password !== repeatPassword) {
      document.getElementById("Rerrormessage").textContent = "Passwords do not match.";
      return;
    }
    if (form.checkValidity()) {
      makeCall("POST", 'CheckRegister', e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
            	sessionStorage.setItem('username', message);
                window.location.href = "Home.html";
                break;
              case 400: // bad request
                document.getElementById("Rerrormessage").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("Rerrormessage").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("Rerrormessage").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();
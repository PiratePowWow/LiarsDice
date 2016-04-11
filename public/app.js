var stompClient = null;

 function setConnected(connected) {
     document.getElementById('newConnect').disabled = connected;
     document.getElementById('roomConnect').disabled = connected;
 }

 function connectAndCreateNewGame() {

     var socket = new SockJS('/hello');
     stompClient = Stomp.over(socket);
     console.log("TEST SOCK",socket);
     console.log("TEST STOMP",stompClient);
     var name = $("#name").val();
     stompClient.connect({name: name, roomCode: ""}, function(frame) {
         setConnected(true);
         console.log('Connected: ' + frame);
         stompClient.subscribe('/topic/lobby/' + roomCode, function(playerList) {
           console.log(playerList);
           showPlayerList(JSON.parse(playerList.body))
         });

        //  $.ajax({
        //    url: '/tpoc/lobb',
        //    method: 'GET',
        //    success: function(playerList) {
        //
        //    }
        //  })
// sbscribe=get
// send is post
         stompClient.send('app/lobby/' + roomCode, {}, "");
     });
 }


 function connectToExistingGame() {
     var socket = new SockJS('/hello');
     stompClient = Stomp.over(socket);
     console.log("TEST SOCK",socket);
     console.log("TEST STOMP",stompClient);
     var roomCode = $("#roomCode").val();
     var name = $("#nameTwo").val();
     stompClient.connect({name: name, roomCode: roomCode}, function(frame) {
         setConnected(true);
         console.log('Connected: ' + frame);
         stompClient.subscribe('/topic/lobby/' + roomCode, function(playerList) {
             showPlayerList(JSON.parse(playerList.body))
         });
         stompClient.send('app/lobby/' + roomCode, {}, "");
     });
 }

 function disconnect() {
     if (stompClient != null) {
         stompClient.disconnect();
     }
     setConnected(false);
     console.log("Disconnected");
 }

 function showPlayerList(playerList) {
     var playersDomElement = document.getElementById('players');
     var playerListDomElement = document.getElementById('playerList');
     if(playerListDomElement) {
         playersDomElement.removeChild(playerListDomElement);
     }
     var ul = document.createElement('ul');
     ul.setAttribute("id", "playerList");
     playersDomElement.appendChild(ul);
     for (i = 0; i < playerList.length; i++) {
         var li = document.createElement('li');
         var textnode = document.createTextNode(playerList[i].name);
         li.appendChild(textnode);
         ul.appendChild(li);
     }
 }

 function sendName() {
     var name = document.getElementById('name').value;
     stompClient.send('/app/hello', {}, JSON.stringify({'name': name}));
 }

 function showGreeting(message) {
     var response = document.getElementById('response');
     var p = document.createElement('p');
     p.style.wordWrap = 'break-word';
     p.appendChild(document.createTextNode(message));
     response.appendChild(p);
 }

 function sendPlayer() {
     var name = document.getElementById('player').value;
     var score = document.getElementById('score').value;
     var player = {
         name: name,
         score: score
     }
     stompClient.send('/app/scoreboard/', {}, JSON.stringify(player));
 };

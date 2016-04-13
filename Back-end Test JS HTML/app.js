       var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var name = document.getElementById('player').value;
            var roomCode = document.getElementById('roomCode').value;
            var socket = new SockJS('/hello');
            stompClient = Stomp.over(socket);
            stompClient.connect({name: name, roomCode: roomCode}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/lobby/' + roomCode, function(playerList) {
                    showPlayerList(JSON.parse(playerList.body))
                });
                stompClient.send('app/lobby/' + roomCode, {}, roomCode);
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
//
//        function sendName() {
//            var name = document.getElementById('name').value;
//            stompClient.send('/app/hello', {}, JSON.stringify({'name': name}));
//        }
//
//        function showGreeting(message) {
//            var response = document.getElementById('response');
//            var p = document.createElement('p');
//            p.style.wordWrap = 'break-word';
//            p.appendChild(document.createTextNode(message));
//            response.appendChild(p);
//        }
//
//        function sendPlayer() {
//            var name = document.getElementById('player').value;
//            var score = document.getElementById('score').value;
//            var player = {
//                name: name,
//                score: score
//            }
//            stompClient.send('/app/scoreboard/', {}, JSON.stringify(player));
//        }

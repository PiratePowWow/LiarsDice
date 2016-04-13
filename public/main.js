var socket; // Socket connection
var playerId;
var queryParam;
var queryParam2;
var queryParam3;
var queryParam4;
var queryParam5;
var isConnected = false;
$(document).ready(function() {
  liarsDice.init();
});
var liarsDice = {

  url: {
    lobby: "/lobbyOrWhatever",
    gameroom: "/gameroomOrWhatever",
  },
  init: function() {
    liarsDice.events();
    liarsDice.styling();
  },
  events: function () {

    $('.submit').on('click', function(event){
      var name = $('input[name="name"]').val();
      var roomCode = $('input[roomCode="roomCode"]').val();
      // socket stuff
          var connectSocket = function() {
            var ws = new SockJS("/liarsDice")
            socket = Stomp.over(ws)
            socket.connect({name:name, roomCode:roomCode}, onSocketConnected)
          };
          var onSocketConnected = function() {
            var url = socket.ws._transport.url.split("/");
            playerId = url[url.length-2];
            console.log('Connected to socket server')
            isConnected = true


      // PURPOSE - Connect to the server and join an existing game
      // PURPOSE - Signal to the server the player wishes to roll their dice
      // PURPOSE - Signal to the server the player wishes to set their stake(raise the stakes)
      // PURPOSE - Signal to the server the player wants to reset the game(start a new game, but keep same roomCode and existing players)
            socket.subscribe("/topic/playerList", onPlayerList);
      // PURPOSE - player's dice)
            socket.subscribe("/topic/lobby/" + playerId, onLobby);
      // @return loserDto - this is the PlayerDto of the losing player
            socket.subscribe("/topic/loser", onLoser);
            socket.send("app/lobby/" + playerId, {}, "");
          };
      event.preventDefault();
      connectSocket();
      console.log("you clicked submit");
      $('.lobby').removeClass('inactive');
      $('.homePage').addClass('inactive');
    });
    $('.box').on('click', function(event){

      var onLobby = function(data){
        queryParam = data.dice[0];
        queryParam2 = data.dice[1];
        queryParam3 = data.dice[2];
        queryParam4 = data.dice[3];
        queryParam5 = data.dice[4];
      };

      event.preventDefault();
      onLobby();
      console.log("you clicked start");
      $('.bigSection').removeClass('inactive');
      $('.lobby').addClass('inactive');
      $('.title').css('margin-top',"1%");
      // cup that lifts and disapears
      $('.cup2').stop(true, true).delay(2100).animate({
        marginTop: -1000
      }, 1200);
    });
    var onPlayerList = function(playerList) {
      _.each()
      $('.nameContent').html("");
    };
    var onLobby = function(data){
      queryParam = data.dice[0];
      queryParam2 = data.dice[1];
      queryParam3 = data.dice[2];
      queryParam4 = data.dice[3];
      queryParam5 = data.dice[4];
    };
    var onLoser = function(){
      $('.bluff').on('click', function(event){
      console.log("you clicked the bluff button");
      socket.send("/topic/loser", {}, JSON.stringify({id: playerId}));
      });
    };

    // press space bar to view dice
    $(window).keydown(function(e) {
      if (e.which === 32) {
        $('.cup').animate({
          opacity: .2
        }, 10);
      }
    });
    // returns to main view when space bar is released
    $(window).keyup(function(e) {
      if (e.which === 32) {
        $('.cup').animate({
          opacity: 1
        }, 10);
      }
    });
    //  lobby
  },
  styling: function() {

// query params= the number the dice lands on
// spinCount=how many times the dice spins before it lands on the number
// dice one
    var faceOne = 1;
    var spinCount = 0;
    var currentSpinCount = 0;
    var showOne = function() {
      $('#cube').attr('class', 'showOne' + faceOne);
      if (faceOne == 6) {
        faceOne = 1;
      } else {
        faceOne++;
      }
      if (currentSpinCount == spinCount) {
        return faceOne = queryParam;
      }
      currentSpinCount++;
    };
    var timer1 = setInterval(showOne, 500);
    // dice two
    var faceTwo = 1;
    var spinCount2 = 2;
    var currentSpinCount2 = 0;
    var showTwo = function() {
      $('#cube2').attr('class', 'showTwo' + faceTwo);
      if (faceTwo == 6) {
        faceTwo = 1;
      } else {
        faceTwo++;
      }
      if (currentSpinCount2 == spinCount2) {
        return faceTwo = queryParam2;
      }
      currentSpinCount2++;
    };
    var timer2 = setInterval(showTwo, 500);
    // third dice
    var faceThree = 1;
    var spinCount3 = 2;
    var currentSpinCount3 = 0;
    var showThree = function() {
      $('#cube3').attr('class', 'showThree' + faceThree);
      if (faceThree == 6) {
        faceThree = 1;
      } else {
        faceThree++;
      }
      if (currentSpinCount3 == spinCount3) {
        return faceThree = queryParam3;
      }
      currentSpinCount3++;
    };
    var timer3 = setInterval(showThree, 500);
    // fourth dice
    var faceFour = 1;
    var spinCount4 = 1;
    var currentSpinCount4 = 0;
    var showFour = function() {
      $('#cube4').attr('class', 'showFour' + faceFour);
      if (faceFour == 6) {
        faceFour = 1;
      } else {
        faceFour++;
      }
      if (currentSpinCount4 == spinCount4) {
        return faceFour = queryParam4;
      }
      currentSpinCount4++;
    };
    var timer4 = setInterval(showFour, 500);
    // fifth dice
    var faceFive = 1;
    var spinCount5 = 2;
    var currentSpinCount5 = 0;
    var showFive = function() {
      $('#cube5').attr('class', 'showFive' + faceFive);
      if (faceFive == 6) {
        faceFive = 1;
      } else {
        faceFive++;
      }
      if (currentSpinCount5 == spinCount5) {
        return faceFive = queryParam5;
      }
      currentSpinCount5++;
    };
    var timer5 = setInterval(showFive, 500);
  }
  // createName: function() {
  //   var name = $('input[name="name"]').val();
  //   return {
  //     name: name,
  //   };
  // },
}

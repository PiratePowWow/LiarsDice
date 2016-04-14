var socket; // Socket connection
var playerId;
var queryParam;
var queryParam2;
var queryParam3;
var queryParam4;
var queryParam5;
var isConnected = false;
var socket = new Socket();
window.socket = socket;

$(document).ready(function() {
  liarsDice.init();
});
var liarsDice = {

  init: function() {
    liarsDice.events();
  },
  events: function () {

    $('.submit').on('click', function(event){
      event.preventDefault();
      console.log("you clicked submit");

      var name = $('input[name="name"]').val();
      var roomCode = $('input[name="roomCode"]').val();
      if (roomCode === "") {
        roomCode = "undefined";
      } else {
         roomCode = roomCode
      };
      // var onPlayerList = function(playerList) {
      //   _.each()
      //   $('.nameContent').html("");
      // };

      $('.lobby').removeClass('inactive');
      $('.homePage').addClass('inactive');
      socket.connectSocket(name,roomCode);
      window.player = function() {
        socket.getPlayerList();
      }
      // socket.sendFirstConnection();
    });
    $('.box').on('click', function(event){
      socket.playRollDie();

      setTimeout(function() {
        rollDicePage(window.diceToDisplay)
      },2000);

      event.preventDefault();
      // onLobby();
      console.log("you clicked start");
      $('.bigSection').removeClass('inactive');
      $('.lobby').addClass('inactive');
      $('.title').css('margin-top',"1%");
      // cup that lifts and disapears
      $('.cup2').stop(true, true).delay(2100).animate({
        marginTop: -1000
      }, 1200);
    });

// Submit a wager
    $('.submitDice').on('click', function(event){
      event.preventDefault();
      console.log("you clicked dice submit");
      socket.raiseStake();
   });
// submit bs
   $('.bluff').on('click', function(event){
     event.preventDefault();
     console.log("you clicked Bull Shit");
     socket.callBullShit();
   });
// start next round and rerequest dice
  $('.nextRound').on('click', function(event){
    event.preventDefault();
    console.log("you rerolled");
    socket.getDiceBack();
  });

    // var onLoser = function(){
    //   $('.bluff').on('click', function(event){
    //   console.log("you clicked the bluff button");
    //   socket.send("/topic/loser", {}, JSON.stringify({id: playerId}));
    //   });
    // };

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

  },


}

function Socket() {
  var playerId;
  var _this = this;
  var socketInternal;

  _this.connectSocket = function(name,roomCode) {
    var ws = new SockJS("/liarsDice")
    socketInternal = Stomp.over(ws)
    socketInternal.connect({name:name, roomCode: roomCode}, function() {
      _this.onSocketConnected()
    })
  };

  _this.onSocketConnected = function() {
    var url = socketInternal.ws._transport.url.split("/");
    playerId = url[url.length-2];
    console.log('I NEED THISConnected to socket server', playerId);
    isConnected = true


      socketInternal.subscribe("/topic/lobby/" + playerId, getDiceBack)
      // socketInternal.subscribe("/topic/playerList", playerList);
      socketInternal.subscribe("/topic/loser", getDiceBack)

      //
      // function test1(data) {
      //   console.log("SUBSCRIBE TOPIC LOBBY", data);
      //
      // }
      // function test2(data) {
      //   console.log("SUBSCRIBE TOPIC playerlist",data);
      //
      // }  function test3(data) {
      //     console.log("SUBSCRIBE TOPIC LOSER",data);
      // }


      socketInternal.send("/app/lobby/" + playerId, {}, "");
      socketInternal.send("/app/lobby/JoinGame",{} ,playerId);


      // socketInternal.send("/app/lobby/resetGame", {}, playerId);

      // var millisecondsToWait = 200;
      //       setTimeout(function() {
      //           socketInternal.send("/app/lobby/rollDice", {}, playerId);
      // }, millisecondsToWait);
      // var millisecondsToWait = 200;
      // setTimeout(function() {
      //     socketInternal.send("/app/lobby/" + playerId, {}, "");
      // }, millisecondsToWait);
      // socketInternal.send("/app/lobby/resetGame", {}, playerId);
      // _this.sendFirstConnection(playerId);

    };
    _this.getPlayerList = function() {
      console.log("this is the player list", playerId);
      socketInternal.send("/app/lobby/JoinGame",{} ,playerId);
    }



    _this.playRollDie = function() {
      console.log("THIS IS A PLAYER ID IN ROLLDIE", playerId);
      socketInternal.send("/app/lobby/rollDice", {}, playerId);
    }

    _this.callBullShit = function() {
      console.log("BULL SHIT", playerId);
      socketInternal.send("/app/lobby/callBluff", {}, playerId);
    }

    _this.raiseStake = function(){
      var quantity = parseInt($('input[name="quantity"]').val());
      var quality = parseInt($('input[name="quality"]').val());
      var stake = {
           "playerId": playerId,
           "newStake": [quantity, quality]
         }
         $('input[name="quantity"]').val("");
         $('input[name="quality"]').val("");
     socketInternal.send("/app/lobby/setStake",{}, JSON.stringify(stake));
    }

//   start next round and reroll dice
  // _this.rollForNextRound = function(){
  //
  // }


  // connectSocket();
    _this.sendFirstConnection = function(thingId) {
      socketInternal.send("/app/lobby/" + thingId, {}, "");
    }

    function getDiceBack(data) {
      window.preStuff = data;
      console.log("WE GOT STUFF BACK", data);
      data = JSON.parse(data.body);
      window.glob = data;
      // console.log("SHOW DATA DICE", data.dice);
      if(data.dice) {
        var diceRol = {
            queryParam: data.dice[0],
            queryParam2: data.dice[1],
            queryParam3: data.dice[2],
            queryParam4: data.dice[3],
            queryParam5: data.dice[4],
          };
          console.log("HEEERERERERE", diceRol);
          window.diceToDisplay = diceRol
          return diceRol
      }
    }

    // function playerList(data) {
    //   var parsed = JSON.parse(data.body);
    //   var players = parsed.playerList.playerDtos
    //   console.log("PLAYERLIST", players);
    //   _.each(data, function onPlayerList(data) {
    //     window.preStuff = data;
    //     console.log(data);
    //     data = JSON.parse(data.body.playerList.PlayerDtos.name);
    //     window.glob = data;
    //     $('.nameContent').html("");
    //   });
    // }
}



function rollDicePage(diceObject) {
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
          return faceOne = diceObject.queryParam;
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
          return faceTwo = diceObject.queryParam2;
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
          return faceThree = diceObject.queryParam3;
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
          return faceFour = diceObject.queryParam4;
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
          return faceFive = diceObject.queryParam5;
        }
        currentSpinCount5++;
      };
      var timer5 = setInterval(showFive, 500);
}

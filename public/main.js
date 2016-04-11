// query params= the number the dice lands on
// spinCount=how many times the dice spins before it lands on the number


// dice one
var faceOne=1;
var queryParam = 6;
var spinCount = 0;
var currentSpinCount = 0;
var showOne = function() {
 $('#cube').attr('class', 'showOne'+faceOne);
 if(faceOne==6) {
   faceOne=1;
 } else {
   faceOne++;
 }
 if (currentSpinCount == spinCount){
   return faceOne = queryParam;
 }
 currentSpinCount++;
};
var timer1=setInterval(showOne  , 500);

// dice two

var faceTwo=1;
var queryParam2 = 4;
var spinCount2 = 2;
var currentSpinCount2 = 0;
var showTwo = function() {
  $('#cube2').attr('class', 'showTwo'+faceTwo);
  if(faceTwo==6) {
    faceTwo=1;
  } else {
    faceTwo++;
  }
  if (currentSpinCount2 == spinCount2){
    return faceTwo = queryParam2;
  }
  currentSpinCount2++;
 };

var timer2=setInterval("showTwo()", 500);

// third dice

var faceThree=1;
var queryParam3 = 6;
var spinCount3 = 2;
var currentSpinCount3 = 0;

var showThree = function() {
  $('#cube3').attr('class', 'showThree'+faceThree);
  if(faceThree==6) {
    faceThree=1;
  } else {
    faceThree++;
  }
  if (currentSpinCount3 == spinCount3){
    return faceThree = queryParam3;
  }
  currentSpinCount3++;
 };

var timer3=setInterval("showThree()", 500);

// fourth dice

var faceFour=1;
var queryParam4 = 3;
var spinCount4 = 1;
var currentSpinCount4 = 0;

var showFour = function() {
  $('#cube4').attr('class', 'showFour'+faceFour);
  if(faceFour==6) {
    faceFour=1;
  } else {
    faceFour++;
  }
  if (currentSpinCount4 == spinCount4){
  return faceFour = queryParam4;
  }
  currentSpinCount4++;
 };

var timer4=setInterval("showFour()", 500);

// fifth dice

var faceFive=1;
var queryParam5 = 2;
var spinCount5 = 2;
var currentSpinCount5 = 0;

var showFive = function() {
  $('#cube5').attr('class', 'showFive'+faceFive);
  if(faceFive==6) {
    faceFive=1;
  } else {
    faceFive++;
  }
  if (currentSpinCount5 == spinCount5){
    return faceFive = queryParam5;
  }
  currentSpinCount5++;
 };

var timer5 = setInterval(showFive, 500);



// press space bar to view dice
$(window).keydown(function(e) {
    if (e.which === 32) {
      $('.cup').animate({opacity:.2}, 10);
    }
});
// returns to main view when space bar is released
$(window).keyup(function(e) {
    if (e.which === 32) {
      $('.cup').animate({opacity:1}, 10);
    }
});


// cup that lifts and disapears
$('.cup2').stop(true, true).delay(2100).animate({
     marginTop: -1000,
   }, 1400)






  //  lobby


 

UpdateCanvas = function (func){
window.requestAnimationFrame = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.msRequestAnimationFrame || window.amozRequestAnimationFrame || (function (func){setTimeout(func, 16.666);});
  window.requestAnimationFrame(func);
}

var width = 0;
Animate = function ()
{
  var ca = document.getElementById("canvas");
  var co = ca.getContext('2d');
  
  // Create gradient
  ca.width = ca.width;
  
  co.save();

  // Before drawing the line the canvas should be clipped
  co.beginPath;
  co.rect(0,0,width,250)
  co.clip();
  
  co.beginPath();
  co.lineWidth = 3;
  co.lineCap = 'round';
  co.strokeStyle ="#00FF00";
  var coords = [
    {x: 10, y:200},{x: 50, y:200},
    {x: 60, y:125},{x: 75, y:225},
    {x: 85, y:175},{x: 95, y:205},
    {x: 105, y:150},{x: 115, y:210},
    {x: 125, y:190},{x: 135, y:208},
    {x: 145, y:125},{x: 155, y:215},
    {x: 165, y:175},{x: 175, y:205},
    {x: 185, y:115},{x: 195, y:220},
    {x: 205, y:150},{x: 215, y:215},
    {x: 225, y:190},{x: 235, y:210},
    {x: 245, y:115},{x: 255, y:212},
    {x: 265, y:200},{x: 400, y:200},
    {x: 405, y:190},{x: 415, y:210},
    {x: 425, y:150},{x: 435, y:210},
    {x: 445, y:115},{x: 455, y:225},
    {x: 465, y:200},{x: 540, y:200}]
                             
  for (var i=0; i<coords.length; i++) {
    //co.moveTo(coords[i].x, coords[i].y);
    co.lineTo(coords[i].x, coords[i].y);
  }
                
  co.stroke();
  co.restore();
  co.fillStyle="#FF0000";
  if (width < 600) {
    width += 1;
    co.fillStyle="#FF0000";
    UpdateCanvas(Animate);
    
    // Reset the width to zero to allow the button to be pressed again
  } else {
    width = 0;
  }
}
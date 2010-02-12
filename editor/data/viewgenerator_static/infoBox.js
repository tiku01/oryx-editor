$(function () {
  $('.origin').each(function () {
	  
    var distance = -90;
    var time = 50;
    var hideDelay = 100;

    var hideDelayTimer = null;

    var beingShown = false;
    var shown = false;
    
    var originName = $('.originName', this);
    var infoBox = $('.infoBox', this).css('opacity', 0);

    // set the mouseover and mouseout on both element
    $([originName.get(0), infoBox.get(0)]).mouseover(function () {
      // stop hide event
      if (hideDelayTimer) clearTimeout(hideDelayTimer);

      // not animate again if we're being shown, or already visible
      if (beingShown || shown) {
        return;
      } else {
        beingShown = true;

        // reset position of infoBox
        infoBox.css({
          top: -110,
          left: +40,
          display: 'block' 
        })

        //starting animation
        .animate({
          top: '-=' + distance + 'px',
          opacity: 1
        }, time, 'swing', function() {
          beingShown = false;
          shown = true;
        });
      }
    }).mouseout(function () {
      // reset the timer
      if (hideDelayTimer) clearTimeout(hideDelayTimer);
      
      // store the timer 
      hideDelayTimer = setTimeout(function () {
        hideDelayTimer = null;
        infoBox.animate({
          top: '-=' + distance + 'px',
          opacity: 0
        }, time, 'swing', function () {
          shown = false;
          // hide the infoBox
          infoBox.css('display', 'none');
        });
      }, hideDelay);
    });
  });
});

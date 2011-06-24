/*
	Menu ver. 1.0 (Wrzesień 2010)
	Zalecana wersja jQuery: 1.4 +
	-----------------------------------
	www.devplugin.pl
	-----------------------------------
	Wszystkie modyfikacje poniższego
	skryptu prosimy zgłaszać na:
	INFO@DEVPLUGIN.PL 
*/

(function($){
$.fn.menu = function(ustawienia){ 
//Dekleracja zmiennych globalnych 
var el, rozwLinkAkt;
ustawienia = jQuery.extend({
	  szybkosc	: 220,
	  autostart	: 0,
      off		: "includes/images/strzN.png",
	  on		: "includes/images/strzA.png"
   },ustawienia);  
  el = $(this);
  el.children("ul").parent("li").children("a").addClass("aktywzakladka");
  el.children("ul").addClass("aktywpodzakladka");
  rozwLinkAkt = el.children("a.aktywzakladka");
   
  rozwLinkAkt.css({
  	"background":	"url("+ustawienia.off+") top right no-repeat"
  });
  if(ustawienia.autostart) {
  	$(".aktywpodzakladka").first().slideDown(ustawienia.szybkosc*1.4, function () {
			$(this).removeAttr("class");
			$(this).attr("class", "naktywpodzakladka");	
			rozwLinkAkt.first().css({
			  	"background":	"url("+ustawienia.on+") top right no-repeat"
		    });	
		});
  }
  //Klik w zakładkę
  rozwLinkAkt.click(function () {
		var klik = $(this); 
		//Zwijanie pozostałych
		rozwLinkAkt.parent("li").children("ul").slideUp(ustawienia.szybkosc/1.2, function(){
			$(this).removeAttr("class");
			$(this).attr("class", "aktywpodzakladka");
			rozwLinkAkt.css({
			  	"background":	"url("+ustawienia.off+") top right no-repeat"
		    });	
		}); 
		klik.removeAttr("class");
		klik.attr("class", "naktywzakladka");
		klik.parent("li").children("ul.aktywpodzakladka").slideDown(ustawienia.szybkosc, function () {
			$(this).removeAttr("class");
			$(this).attr("class", "naktywpodzakladka");	
				klik.css({
				  	"background":	"url("+ustawienia.on+") top right no-repeat"
			    });	
		});
		
	return false;
  }); 
}
})(jQuery); //koniec pluginu 
(function($) {
  $.fn.postJSON = function(args) {
    args["type"] = "POST";
    args["processData"] = false;
    args["contentType"] = "application/json; charset=utf-8",
    args["data"] = JSON.stringify(args["data"]);
    return $.ajax(args);
  };
})(jQuery);

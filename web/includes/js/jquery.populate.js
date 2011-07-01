(function($) {
  $.fn.populate = function(data) {
    var form = this;
    $.each(data, function (name,value) {
      $(form).find("input[name='"+name+"'],select[name='"+name+"']").each(function() {
        switch (this.nodeName.toLowerCase()) {
            case "input":
                switch (this.type) {
                    case "radio":
                    case "checkbox":
                        if (this.value==value) { $(this).click(); }
                        break;
                    default:
                        $(this).val(value);
                        break;
                }
                break;
            case "select":
                jQuery("option", this).each(function() {
                    if (this.value==value) { this.selected=true; }
                });
                break;
        }
      });
    });
  }
})(jQuery);

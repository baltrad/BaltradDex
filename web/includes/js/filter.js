"use strict";

jQuery.template("filter-template-common",
  '<input type="hidden" name="id" value="${$item.data.id}"/>' +
  '<input type="hidden" name="type" value="${$item.data.type}"/>'
);

jQuery.template("select-option-template",
  '<option value="${$data}" {{if $data == $item.current}} selected="selected"{{/if}}>${$data}</option>'
);

jQuery.template("filter-template-attr",
  '<div><form>' +
  '  {{tmpl(null, $item) "filter-template-common"}}' +
  '  <div>' +
  '    <label for="attribute">attribute</label>' +
  '    <input type="text" name="attribute"/>' +
  '  </div>' +
  '  <div>' +
  '    <label for="operator">operator</label>' +
  '    <select name="operator">' +
  '      {{tmpl($item.operators, {current: $item.data.operator}) "select-option-template"}}' +
  '    </select>' +
  '  </div>' +
  '  <div>' +
  '    <label for="valueType">value type</label>' +
  '    <select name="valueType">' +
  '      {{tmpl($item.valueTypes, {current: $item.data.valueType}) "select-option-template"}}' +
  '    </select>' +
  '  </div>' +
  '  <div>' +
  '    <label for="value">value</label>' +
  '    <input type="text" name="value" />' +
  '  </div>' +
  '  <div>' +
  '    <label for="negated">negated</label>' +
  '    <input type="checkbox" name="negated" value="true"/>' +
  '  </div>' +
  '</form></div>'
);

jQuery.template("filter-template-combined",
  '<div>' +
  '  <form>' +
  '    {{tmpl(null, $item) "filter-template-common"}}' +
  '    <div>' +
  '      <label for="matchType">match</label>' +
  '      <select name="matchType">' +
  '        {{tmpl($item.matchTypes, {current: $item.data.matchType}) "select-option-template"}}' +
  '      </select>' +
  '    </div>' +
  '  </form>' +
  '  <div id="childFilters">' +
  '  </div>' +
  '  {{tmpl "filter-template-create"}}' +
  '</div>'
);

jQuery.template("filter-template-create",
  '<div>' +
  '  <select id="filter-type" name="type">' +
  '    <option value="combined" selected="selected">CombinedFilter</option>' +
  '    <option value="attr">AttributeFilter</option>' +
  '  </select>' +
  '  <input id="filter-create" type="button" value="Create">' +
  '</div>'
);

jQuery.template("filter-template-edit",
  '<div>' +
  '  <div>${$item.getExpressionString()}</div>' +
  '  <div>' +
  '    <input id="filter-edit", type="button", value="Edit">' +
  '    <input id="filter-remove", type="button", value="Remove">' +
  '  </div>' +
  '</div>'
);

function _createFilterDialog(flt) {
  if (typeof _createFilterDialog.counter === 'undefined') {
    _createFilterDialog.counter = 0;
  }
  _createFilterDialog.counter++;
  var name = "beast-db-filter-" + _createFilterDialog.counter;
  var dlg = $.tmpl("filter-template-" + flt.data.type, null, flt);
  dlg.attr("id", name)
  dlg.appendTo("body");
  dlg.dialog({
    autoOpen: false,
    closeOnEscape: false,
    modal: true,
    title: "Edit Filter: " + name,
    buttons: [{
        text: "Cancel",
        click: function() {
	  flt.reset();
          flt.close();
        }
      }, {
        text: "Save",
        click: function() {
          if (!flt.isValid()) {
            alert("invalid filter");
            return;
          }
          if (typeof flt.onSave === "function") {
            if (!flt.onSave())
              return;
          }
	  flt.updateData();
          flt.close();
        }
    }]
  });

  return dlg;
}

/**
 * Base filter construction
 */
function BaseFilter(data) {
  
  this.data = data;
  
  // create the actual dialog
  var dlg = _createFilterDialog(this);
  
  /**
   * display the dialog
   */
  this.display = function() {
    dlg.dialog("open");
  };
  
  /**
   * close the dialog
   */
  this.close = function() {
    dlg.dialog("close");
  };
  
  /**
   * reset the dialog form from this.data
   */
  this.reset = function() {
    dlg.find("form").populate(this.data);
  }

  this.reset();
  
  /**
   * update this.data from dialog form
   */
  this.updateData = function() {
    this.data = this.toJSON();
  }
  
  /**
   * access the dialog form
   */
  this.getDialog = function() {
    return dlg;
  }
  
  /**
   * get expression string formed by this filter
   *
   * fetches synchronously from `filter_expressionString.json`
   */
  this.getExpressionString = function() {
    var r;
    $.fn.postJSON({
      url: "filter_expressionString.json",
      async: false,
      data: this.toJSON(),
      success: function(result) {
        r = result;
      }
    });
    return r;
  };
  
  /**
   * test if the filter is valid
   *
   * test synchronously using `filter_isValid.json`
   *
   * @return true if the filter is valid
   */
  this.isValid = function() {
    var r;
    $.fn.postJSON({
      url: "filter_isValid.json",
      async: false,
      data: this.toJSON(),
      success: function(result) {
        r = result;
      }
    });
    return r;
  };
  
  /**
   * json representation of this filter
   */
  this.toJSON = function() {
    return dlg.find("form").serializeJSON();
  };
}

/**
 * Create CombinedFilter dialog
 *
 * @constructor
 */
function CombinedFilter(data) {
  var default_data = {
    "type": "combined",
    "childFilters": []
  }

  if (data) {
    $.extend(default_data, data);
  }

  var childFilters = default_data["childFilters"];
  delete default_data["childFilters"];

  // create base filter instance to enhance
  var that = new BaseFilter(default_data);
  var childDiv = that.getDialog().find("div#childFilters");

  /**
   * add a child filter
   * @param {child} the child filter instance
   */
  this.addChildFilter = function(child) {
    var childDom = $.tmpl("filter-template-edit", null, child);
    childDom.data("filter", child);
    childDom.find("#filter-edit").click(function() {
      child.display();
    });
    childDom.find("#filter-remove").click(function() {
      childDom.remove();
    });
    childDiv.append(childDom);
  }

  for (var i = 0; i < childFilters.length; i++) {
    var child = makeFilterDialog(childFilters[i]);
    this.addChildFilter(child);
  }
  
  var thisRef = this;

  that.getDialog().find("#filter-create").click(function() {
    var dialog = makeFilterDialog({
      "type": that.getDialog().find("#filter-type option:selected").val()
    });
    dialog.onSave = function() {
      thisRef.addChildFilter(dialog);
      return true;
    }
    dialog.display();
  });

  that.toJSON = function() {
    var json = that.getDialog().find("form").serializeJSON();
    var childFilters = [];
    childDiv.children().each(function(index) {
      childFilters.push($(this).data("filter").toJSON());
    });
    json['childFilters'] = childFilters;
    return json;
  }

  return that;
}

/**
 * Create AttributeFilter dialog
 *
 * @constructor
 */
function AttributeFilter(data) {
  var defaults = {
    "type": "attr"
  }
  if (data)
    $.extend(defaults, data);

  var that = new BaseFilter(defaults);

  return that;
}

/**
 * load enums
 */
function load(flt) {
  $.ajax({
    url: "filter_comboMatchTypes.json",
    async: false,
    success: function(result) {
      flt.matchTypes = result;
    }
  });

  $.ajax({
    url: "filter_attrOperators.json",
    async: false,
    success: function(result) {
      flt.operators = result;
    }
  });

  $.ajax({
    url: "filter_attrValueTypes.json",
    async: false,
    success: function(result) {
      flt.valueTypes = result;
    }
  });
}
// load and attach to the prototype
load(BaseFilter.prototype)


/**
 * filter dialog factory method
 *
 * @constructor
 * @param {data} json data of the filter. data.type is used to
 *               look up concrete object to create.
 */
function makeFilterDialog(data) {
  switch (data.type) {
    case "attr":
      return new AttributeFilter(data);
    case "combined":
      return new CombinedFilter(data);
    default:
      throw "unknown filter type: " + data.type;
  }
}

/**
 * create top level filter management controls
 *
 * @constructor
 * @param {elm} the DOM element whose content to replace with
 *              top level filter controls
 * @param {data} Filter data. If present, will create controls
 *               to edit a filter, otherwise, will create controls
 *               to create a new filter.
 *
 * the intended setup is along these lines:
 *
 * <script type="text/javascript">
 *   $(document).ready(function() {
 *     createTopLevelFilter($.find("#filter"));
 *   }
 * </script>
 * <body>
 *   <div id="filter">
 * </body>
 *
 * after user has created/modified to filter, it's data can be accessed:
 *
 * $.find("#filter").data("filter").toJSON();
 *
 */
function createTopLevelFilter(elm, data) {
  if (data) {
    var dialog = makeFilterDialog(data);
    var div = $.tmpl("filter-template-edit", null, dialog);
    div.find("#filter-edit").click(function() {
      dialog.display();
      dialog.onSave = function() {
      	createTopLevelFilter(elm, dialog.toJSON());
	return true;
      }
    });
    div.find("#filter-remove").click(function() {
      createTopLevelFilter(elm);
    });
    $(elm).empty().append(div);
  } else {
    var div = $.tmpl("filter-template-create");
    div.find("#filter-create").click(function() {
      var dialog = makeFilterDialog({
        "type": div.find("#filter-type option:selected").val()
      });
      dialog.onSave = function() {
        createTopLevelFilter(elm, dialog.toJSON());
        return true;
      };
      dialog.display();
    });
    $(elm).empty().append(div);
  }
}

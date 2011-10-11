jQuery.template("bdb_filter_select_option",
  '<option value="${value}" {{if value == $item.current}} selected="selected"{{/if}}>${name}</option>'
);

jQuery.template("bdb_filter_common",
  '<input type="hidden" name="id" value="${$data.id}"/>' +
  '<input type="hidden" name="type" value="${$data.type}"/>'
);


jQuery.template("bdb_filter_attr",
  '<span class="bdbAttributeFilter">' +
  '  {{tmpl($data) "bdb_filter_common"}}' +
  '  <label for="negated">NOT</label>' +
  '  <input type="checkbox" name="negated" value="true" {{if $data.negated}} checked="checked" {{/if}}/>' +
  '  <input type="text" name="attribute" value="${$data.attribute}"/>' +
  '  <select name="operator">' +
  '    {{tmpl($item.operators, {current: $data.operator}) "bdb_filter_select_option"}}' +
  '  </select>' +
  '  <select name="valueType">' +
  '    {{tmpl($item.valueTypes, {current: $data.valueType}) "bdb_filter_select_option"}}' +
  '  </select>' +
  '  <input type="text" name="value" value="${$data.value}"/>' +
  '</span>'
);

jQuery.template("bdb_filter_combined",
  '<div class="bdbCombinedFilter"> ' +
  '  {{tmpl($data) "bdb_filter_common"}} ' +
  '  <select name="matchType">' +
  '    {{tmpl($item.matchTypes, {current: $data.matchType}) "bdb_filter_select_option"}}' +
  '  </select>' +
  '  <ul></ul>' +
  '</div>'
);

jQuery.template("bdb_filter_add_child",
  '<input class="bdbFilterAdd" type="image" src="includes/images/icons/circle-plus.png">' +
  '<select name="filterType">' +
  '  {{tmpl($item.filterTypes) "bdb_filter_select_option"}}' +
  '</select>'
);

jQuery.template("bdb_filter_always",
  '<span>' +
  '  {{tmpl($data) "bdb_filter_common"}}' +
  '  ALWAYS MATCH' +
  '</span>'
);

function BdbCombinedFilter(data) {
  var default_data = {
    type: "combined",
    matchType: "ANY",
    childFilters: []
  };
  
  this.data = $.extend(default_data, data);
  this.dom = null;
  this.children = [];

  this.wrapChildFilterDom = function (childFilter) {
    var dom = childFilter.dom.wrapAll("<li/>").parent();
    dom.prepend("<input type='image' class='bdbFilterRemove' src='includes/images/icons/circle-delete.png'/>");
    createChildRemovalCallback(dom, this, childFilter);
    return dom;
  };

  this.updateDomFromData = function() {
    this.dom = $.tmpl(
      "bdb_filter_combined",
      this.data,
      {matchTypes: this.MATCH_TYPES}
    );

    var childFilters = this.dom.children().filter("ul");
    childFilters.empty();
    for (var i = 0; i < this.data.childFilters.length; i++) {
      var filter = createBdbFilter(this.data.childFilters[i]);
      this.children.push(filter);
      this.wrapChildFilterDom(filter).appendTo(childFilters);
    }

    var addChildDom = $.tmpl(
      "bdb_filter_add_child",
      null,
      {filterTypes : this.FILTER_TYPES}
    );
    addChildDom.wrapAll("<li>");
    addChildDom = addChildDom.parent();
    createChildAdditionCallback(addChildDom, this);
    addChildDom.appendTo(childFilters);
  };
  this.updateDomFromData();

  this.updateDataFromDom = function() {
    var vals = {};
    $.map(this.dom.children().serializeArray(), function(item, i) {
      vals[item['name']] = item['value'];
    });
    vals.childFilters = [];
    for (var i=0; i < this.children.length; i++) {
      var child = this.children[i];
      child.updateDataFromDom();
      vals.childFilters.push(child.data);
    }
    this.data = vals;
  }

  return this;
};

function createChildAdditionCallback(addChildDom, filter) {
  var addChildFilterButton = addChildDom.find("input.bdbFilterAdd");

  addChildFilterButton.click(function(evt) {
    evt.preventDefault();
    var filterType = addChildDom.find("[name='filterType']").val();
    var childFilter = createBdbFilter({type: filterType});
    filter.children.push(childFilter);
    var childDom = filter.wrapChildFilterDom(childFilter);
    addChildDom.first().before(childDom);
  });
}

function createChildRemovalCallback(childDom, filter, childFilter) {
  childDom.children().first().click(function(evt) {
    evt.preventDefault();
    childDom.remove();
    for (var i=0; i < filter.children.length; i++) {
      if (filter.children[i] == childFilter)
        filter.children.splice(i, 1);
    }
  });
}

BdbCombinedFilter.prototype.MATCH_TYPES = [
  {name: "match ANY", value: "ANY"},
  {name: "match ALL", value: "ALL"},
];

BdbCombinedFilter.prototype.FILTER_TYPES = [
  {name: "Always match", value: "always"},
  {name: "Attribute filter", value: "attr"},
  {name: "Combined filter", value: "combined"},
]


function BdbAttributeFilter(data) {
  var default_data = {
    type: "attr",
    negated: false,
    attribute: "",
    operator: "EQ",
    valueType: "STRING",
    value: "",
  };

  this.data = $.extend(default_data, data);
  this.dom = null;

  this.updateDomFromData = function() {
    this.dom = $.tmpl(
      "bdb_filter_attr",
      this.data,
      {
        operators: this.OPERATORS,
        valueTypes : this.VALUE_TYPES,
      }
    );
    return this.dom;
  };
  this.updateDomFromData();

  this.updateDataFromDom = function() {
    var vals = {};
    $.map(this.dom.children().serializeArray(), function(item, i) {
      vals[item['name']] = item['value'];
    });
    this.data = vals;
  };

  return this;
};

BdbAttributeFilter.prototype.OPERATORS = [
  {name: "=", value: "EQ"},
  {name: "<", value: "LT"},
  {name: "<=", value: "LE"},
  {name: ">", value: "GT"},
  {name: ">=", value: "GE"},
  {name: "in", value: "IN"},
];
  
BdbAttributeFilter.prototype.VALUE_TYPES = [
  {name: "string", value: "STRING"},
  {name: "long", value: "LONG"},
  {name: "double", value: "DOUBLE"},
  {name: "bool", value: "BOOL"},
];

function BdbAlwaysMatchFilter(data) {
  this.data = data;
  
  this.dom = $.tmpl("bdb_filter_always", this.data);
  this.updateDataFromDom = function() { }
  this.updateDomFromData = function() { }

  return this;
};

function createBdbFilter(data) {
  switch (data.type) {
    case "attr":
      return new BdbAttributeFilter(data);
    case "combined":
      return new BdbCombinedFilter(data);
    case "always":
      return new BdbAlwaysMatchFilter(data);
  }
};

/**
 * test if the filter is valid
 *
 * test synchronously using `filter_isValid.json`
 *
 * @return true if the filter is valid
 */
function isValidBdbFilter(filter) {
  var r;
  $.fn.postJSON({
    url: "filter_isValid.json",
    async: false,
    data: filter,
    success: function(result) {
      r = result;
    }
  });
  return r;
};


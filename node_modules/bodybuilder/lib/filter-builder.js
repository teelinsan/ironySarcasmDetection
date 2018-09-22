'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = filterBuilder;

var _utils = require('./utils');

function filterBuilder(options) {
  var filters = {
    and: [],
    or: [],
    not: []
  };

  var makeFilter = _utils.pushQuery.bind(Object.assign({ isInFilterContext: true }, options), filters);

  function addMinimumShouldMatch(str) {
    filters.minimum_should_match = str;
  }

  return {
    /**
     * Add a filter clause to the query body.
     *
     * @param  {string}        type    Filter type.
     * @param  {string|Object} field   Field to filter or complete filter
     *                                 clause.
     * @param  {string|Object} value   Filter term or inner clause.
     * @param  {Object}        options (optional) Additional options for the
     *                                 filter clause.
     * @param  {Function}      [nest]  (optional) A function used to define
     *                                 sub-filters as children. This _must_ be
     *                                 the last argument.
     *
     * @return {bodybuilder} Builder.
     *
     * @example
     * bodybuilder()
     *   .filter('term', 'user', 'kimchy')
     *   .build()
     */
    filter: function filter() {
      for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
        args[_key] = arguments[_key];
      }

      makeFilter.apply(undefined, ['and'].concat(args));
      return this;
    },


    /**
     * Alias for `filter`.
     *
     * @return {bodybuilder} Builder.
     */
    andFilter: function andFilter() {
      return this.filter.apply(this, arguments);
    },


    /**
     * Alias for `filter`.
     *
     * @return {bodybuilder} Builder.
     */
    addFilter: function addFilter() {
      return this.filter.apply(this, arguments);
    },


    /**
     * Add a "should" filter to the query body.
     *
     * Same arguments as `filter`.
     *
     * @return {bodybuilder} Builder.
     */
    orFilter: function orFilter() {
      for (var _len2 = arguments.length, args = Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
        args[_key2] = arguments[_key2];
      }

      makeFilter.apply(undefined, ['or'].concat(args));
      return this;
    },


    /**
     * Add a "must_not" filter to the query body.
     *
     * Same arguments as `filter`.
     *
     * @return {bodybuilder} Builder.
     */
    notFilter: function notFilter() {
      for (var _len3 = arguments.length, args = Array(_len3), _key3 = 0; _key3 < _len3; _key3++) {
        args[_key3] = arguments[_key3];
      }

      makeFilter.apply(undefined, ['not'].concat(args));
      return this;
    },


    /**
     * Set the `minimum_should_match` property on a bool filter with more than
     * one `should` clause.
     *
     * @param  {any} param  minimum_should_match parameter. For possible values
     *                      see https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html
     * @return {bodybuilder} Builder.
     */
    filterMinimumShouldMatch: function filterMinimumShouldMatch(param) {
      addMinimumShouldMatch(param);
      return this;
    },
    getFilter: function getFilter() {
      return this.hasFilter() ? (0, _utils.toBool)(filters) : {};
    },
    hasFilter: function hasFilter() {
      return !!(filters.and.length || filters.or.length || filters.not.length);
    }
  };
}
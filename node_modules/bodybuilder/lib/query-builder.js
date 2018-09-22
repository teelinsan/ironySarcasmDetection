'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = queryBuilder;

var _utils = require('./utils');

function queryBuilder(options) {
  var query = {
    and: [],
    or: [],
    not: []
  };

  var makeQuery = _utils.pushQuery.bind(options || {}, query);

  function addMinimumShouldMatch(str) {
    query.minimum_should_match = str;
  }

  return {
    /**
     * Add a query clause to the query body.
     *
     * @param  {string}        type    Query type.
     * @param  {string|Object} field   Field to query or complete query clause.
     * @param  {string|Object} value   Query term or inner clause.
     * @param  {Object}        options (optional) Additional options for the
     *                                 query clause.
     * @param  {Function}      [nest]  (optional) A function used to define
     *                                 sub-filters as children. This _must_ be
     *                                 the last argument.
     *
     * @return {bodybuilder} Builder.
     *
     * @example
     * bodybuilder()
     *   .query('match_all')
     *   .build()
     *
     * bodybuilder()
     *   .query('match_all', { boost: 1.2 })
     *   .build()
     *
     * bodybuilder()
     *   .query('match', 'message', 'this is a test')
     *   .build()
     *
     * bodybuilder()
     *   .query('terms', 'user', ['kimchy', 'elastic'])
     *   .build()
     *
     * bodybuilder()
     *   .query('nested', { path: 'obj1', score_mode: 'avg' }, (q) => {
     *     return q
     *       .query('match', 'obj1.name', 'blue')
     *       .query('range', 'obj1.count', {gt: 5})
     *   })
     *   .build()
     */
    query: function query() {
      for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
        args[_key] = arguments[_key];
      }

      makeQuery.apply(undefined, ['and'].concat(args));
      return this;
    },


    /**
     * Alias for `query`.
     *
     * @return {bodybuilder} Builder.
     */
    andQuery: function andQuery() {
      return this.query.apply(this, arguments);
    },


    /**
     * Alias for `query`.
     *
     * @return {bodybuilder} Builder.
     */
    addQuery: function addQuery() {
      return this.query.apply(this, arguments);
    },


    /**
     * Add a "should" query to the query body.
     *
     * Same arguments as `query`.
     *
     * @return {bodybuilder} Builder.
     */
    orQuery: function orQuery() {
      for (var _len2 = arguments.length, args = Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
        args[_key2] = arguments[_key2];
      }

      makeQuery.apply(undefined, ['or'].concat(args));
      return this;
    },


    /**
     * Add a "must_not" query to the query body.
     *
     * Same arguments as `query`.
     *
     * @return {bodybuilder} Builder.
     */
    notQuery: function notQuery() {
      for (var _len3 = arguments.length, args = Array(_len3), _key3 = 0; _key3 < _len3; _key3++) {
        args[_key3] = arguments[_key3];
      }

      makeQuery.apply(undefined, ['not'].concat(args));
      return this;
    },


    /**
     * Set the `minimum_should_match` property on a bool query with more than
     * one `should` clause.
     *
     * @param  {any} param  minimum_should_match parameter. For possible values
     *                      see https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html
     * @return {bodybuilder} Builder.
     */
    queryMinimumShouldMatch: function queryMinimumShouldMatch(param) {
      addMinimumShouldMatch(param);
      return this;
    },
    getQuery: function getQuery() {
      return this.hasQuery() ? (0, _utils.toBool)(query) : {};
    },
    hasQuery: function hasQuery() {
      return !!(query.and.length || query.or.length || query.not.length);
    }
  };
}
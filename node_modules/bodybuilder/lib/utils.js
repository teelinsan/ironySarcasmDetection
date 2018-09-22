'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _has2 = require('lodash/has');

var _has3 = _interopRequireDefault(_has2);

var _includes2 = require('lodash/includes');

var _includes3 = _interopRequireDefault(_includes2);

var _isFunction2 = require('lodash/isFunction');

var _isFunction3 = _interopRequireDefault(_isFunction2);

var _last2 = require('lodash/last');

var _last3 = _interopRequireDefault(_last2);

var _isObject2 = require('lodash/isObject');

var _isObject3 = _interopRequireDefault(_isObject2);

var _isNil2 = require('lodash/isNil');

var _isNil3 = _interopRequireDefault(_isNil2);

var _extend2 = require('lodash/extend');

var _extend3 = _interopRequireDefault(_extend2);

var _findIndex2 = require('lodash/findIndex');

var _findIndex3 = _interopRequireDefault(_findIndex2);

var _assign2 = require('lodash/assign');

var _assign3 = _interopRequireDefault(_assign2);

var _isPlainObject2 = require('lodash/isPlainObject');

var _isPlainObject3 = _interopRequireDefault(_isPlainObject2);

exports.sortMerge = sortMerge;
exports.buildClause = buildClause;
exports.toBool = toBool;
exports.pushQuery = pushQuery;

var _queryBuilder = require('./query-builder');

var _queryBuilder2 = _interopRequireDefault(_queryBuilder);

var _filterBuilder = require('./filter-builder');

var _filterBuilder2 = _interopRequireDefault(_filterBuilder);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

/**
 * Compound sort function into the list of sorts
 *
 * @private
 *
 * @param  {Array} current Array of Elasticsearch sorts.
 * @param  {String} field Field to sort.
 * @param  {String|Object} value A valid direction ('asc', 'desc') or object with sort options
 * @returns {Array} Array of Elasticsearch sorts.
 */
function sortMerge(current, field, value) {
  var payload = void 0;

  if ((0, _isPlainObject3.default)(value)) {
    payload = _defineProperty({}, field, (0, _assign3.default)({}, value));
  } else {
    payload = _defineProperty({}, field, { order: value });
  }

  var idx = (0, _findIndex3.default)(current, function (o) {
    return o[field] != undefined;
  });

  if (field === '_geo_distance' || idx === -1) {
    current.push(payload);
  } else {
    (0, _extend3.default)(current[idx], payload);
  }

  return current;
}

/**
 * Generic builder for query, filter, or aggregation clauses.
 *
 * @private
 *
 * @param  {string|Object} field Field name or complete clause.
 * @param  {string|Object} value Field value or inner clause.
 * @param  {Object}        opts  Additional key-value pairs.
 *
 * @return {Object} Clause
 */
function buildClause(field, value, opts) {
  var hasField = !(0, _isNil3.default)(field);
  var hasValue = !(0, _isNil3.default)(value);
  var mainClause = {};

  if (hasValue) {
    mainClause = _defineProperty({}, field, value);
  } else if ((0, _isObject3.default)(field)) {
    mainClause = field;
  } else if (hasField) {
    mainClause = { field: field };
  }

  return Object.assign({}, mainClause, opts);
}

function toBool(filters) {
  var unwrapped = {
    must: unwrap(filters.and),
    should: unwrap(filters.or),
    must_not: unwrap(filters.not),
    minimum_should_match: filters.minimum_should_match
  };

  if (filters.and.length === 1 && !unwrapped.should && !unwrapped.must_not) {
    return unwrapped.must;
  }

  var cleaned = {};

  if (unwrapped.must) {
    cleaned.must = unwrapped.must;
  }
  if (unwrapped.should) {
    cleaned.should = filters.or;
  }
  if (unwrapped.must_not) {
    cleaned.must_not = filters.not;
  }
  if (unwrapped.minimum_should_match && filters.or.length > 1) {
    cleaned.minimum_should_match = unwrapped.minimum_should_match;
  }

  return {
    bool: cleaned
  };
}

function unwrap(arr) {
  return arr.length > 1 ? arr : (0, _last3.default)(arr);
}

function pushQuery(existing, boolKey, type) {
  var nested = {};

  for (var _len = arguments.length, args = Array(_len > 3 ? _len - 3 : 0), _key = 3; _key < _len; _key++) {
    args[_key - 3] = arguments[_key];
  }

  if ((0, _isFunction3.default)((0, _last3.default)(args))) {
    var nestedCallback = args.pop();
    var nestedResult = nestedCallback(Object.assign({}, (0, _filterBuilder2.default)({ isInFilterContext: this.isInFilterContext }), this.isInFilterContext ? {} : (0, _queryBuilder2.default)({ isInFilterContext: this.isInFilterContext })));
    if (!this.isInFilterContext && nestedResult.hasQuery()) {
      nested.query = nestedResult.getQuery();
    }
    if (nestedResult.hasFilter()) {
      nested.filter = nestedResult.getFilter();
    }
  }

  if ((0, _includes3.default)(['bool', 'constant_score'], type) && this.isInFilterContext && (0, _has3.default)(nested, 'filter.bool')) {
    // nesting filters: We've introduced an unnecessary `filter.bool`
    existing[boolKey].push(_defineProperty({}, type, Object.assign(buildClause.apply(undefined, args), nested.filter.bool)));
  } else if (type === 'bool' && (0, _has3.default)(nested, 'query.bool')) {
    existing[boolKey].push(_defineProperty({}, type, Object.assign(buildClause.apply(undefined, args), nested.query.bool)));
  } else {
    // Usual case
    existing[boolKey].push(_defineProperty({}, type, Object.assign(buildClause.apply(undefined, args), nested)));
  }
}
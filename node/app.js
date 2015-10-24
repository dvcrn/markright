// Copyright 2013 The Closure Library Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @fileoverview A nodejs script for dynamically requiring Closure within
 * nodejs.
 *
 * Example of usage:
 * <code>
 * require('./bootstrap/nodejs')
 * goog.require('goog.ui.Component')
 * </code>
 *
 * This loads goog.ui.Component in the global scope.
 *
 * If you want to load custom libraries, you can require the custom deps file
 * directly. If your custom libraries introduce new globals, you may
 * need to run goog.nodeGlobalRequire to get them to load correctly.
 *
 * <code>
 * require('./path/to/my/deps.js')
 * goog.bootstrap.nodeJs.nodeGlobalRequire('./path/to/my/base.js')
 * goog.require('my.Class')
 * </code>
 *
 * @author nick@medium.com (Nick Santos)
 *
 * @nocompile
 */


// this is goog/boostrap/nodejs.js with slight modifications to fit shadow-build


var fs = require('fs');
var path = require('path');
var vm = require('vm');


/**
 * The goog namespace in the global scope.
 */
global.goog = {};


/**
 * Imports a script using Node's require() API.
 *
 * @param {string} src The script source.
 * @return {boolean} True if the script was imported, false otherwise.
 */
global.CLOSURE_IMPORT_SCRIPT = function(src) {
  // @thheller: goog.require files are in src directory
  require(path.resolve(__dirname, 'src') + '/' + src);
  return true;
};


// Declared here so it can be used to require base.js
function nodeGlobalRequire(file) {
  var _module = global.module, _exports = global.exports;
  global.module = undefined;
  global.exports = undefined;
  vm.runInThisContext(fs.readFileSync(file), file);
  global.exports = _exports;
  global.module = _module;
}


// Load Closure's base.js into memory.
nodeGlobalRequire(path.resolve(__dirname, 'src', 'goog', 'base.js'));


/**
 * Bootstraps a file into the global scope.
 *
 * This is strictly for cases where normal require() won't work,
 * because the file declares global symbols with 'var' that need to
 * be added to the global scope.
 * @suppress {missingProvide}
 *
 * @param {string} file The path to the file.
 */
goog.nodeGlobalRequire = nodeGlobalRequire;



goog.require('goog.dom.NodeType');
goog.require('goog.string.Unicode');
goog.require('goog.string');
goog.require('goog.debug.Error');
goog.require('goog.asserts.AssertionError');
goog.require('goog.asserts');
goog.require('goog.array');
goog.require('goog.array.ArrayLike');
goog.require('goog.string.StringBuffer');
goog.require('goog.object');
goog.require('cljs.core');
goog.require('runtime_setup');
goog.require('cljs.nodejs');
goog.require('cljs.reader');
goog.require('cljs.core.async.impl.protocols');
goog.require('cljs.core.async.impl.buffers');
goog.require('goog.functions');
goog.require('goog.dom.TagName');
goog.require('goog.debug.EntryPointMonitor');
goog.require('goog.debug.entryPointRegistry');
goog.require('goog.labs.userAgent.util');
goog.require('goog.labs.userAgent.browser');
goog.require('goog.labs.userAgent.engine');
goog.require('goog.async.nextTick');
goog.require('goog.async.throwException');
goog.require('cljs.core.async.impl.dispatch');
goog.require('cljs.core.async.impl.channels');
goog.require('cljs.core.async.impl.ioc_helpers');
goog.require('cljs.core.async.impl.timers');
goog.require('cljs.core.async');
goog.require('electron.ipc');
goog.require('markright.main');

(function() {
var proc = require('process');
cljs.core.apply.cljs$core$IFn$_invoke$arity$2(
markright.main.main
,
process.argv.slice(2)
);
})();
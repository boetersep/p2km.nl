'use strict';

String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1).toLowerCase();
}

/* App Module */

var p2000App = angular.module('p2000App', [
  'ngRoute',
  'p2000Controllers',
  'openlayers-directive'
]);

p2000App.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
    when('/message/:capcode/:timestamp', {
        templateUrl: 'detail.html',
        controller: 'MessageDetailCtrl'
      })
  }]);
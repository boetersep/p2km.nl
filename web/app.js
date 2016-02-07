'use strict';

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
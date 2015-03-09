'use strict';

/* App Module */

var p2000App = angular.module('p2000App', [
  'ngRoute',
  'p2000Controllers'
]);

p2000App.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
    when('/message/:messageId', {
        templateUrl: 'detail.html',
        controller: 'MessageDetailCtrl'
      })
  }]);
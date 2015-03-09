'use strict';

/* Controllers */

var p2000Controllers = angular.module('p2000Controllers', []);

p2000Controllers.controller('MessageListCtrl', ['$scope', '$http',
  function($scope, $http) {
	$scope.messages = [];
	
	var ws = new WebSocket("ws://" + window.location.host + window.location.pathname + "messages/live");

	ws.onmessage = function(messages) {
		var messages = JSON.parse(messages.data);
		angular.forEach(messages, function(item) {
			$scope.messages.unshift(item);
			if ($scope.messages.length > 100) {
				$scope.messages.pop();
			}
		});
		$scope.$apply();
	};
  }]);

p2000Controllers.controller('MessageDetailCtrl', ['$scope', '$routeParams',
  function($scope, $routeParams) {
    $scope.messageId = $routeParams.messageId;
  }]);

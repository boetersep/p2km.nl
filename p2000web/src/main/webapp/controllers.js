'use strict';

/* Controllers */

var p2000App = angular.module('p2000App', []);

p2000App.controller('MessageListCtrl', function($scope) {
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
});

p2000App.controller('MessageDetailCtrl', function($scope) {
	 
	
});

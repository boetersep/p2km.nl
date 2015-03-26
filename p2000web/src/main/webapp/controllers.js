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

p2000Controllers.controller('MessageDetailCtrl', ['$scope', '$routeParams', '$http',
  function($scope, $routeParams, $http) {
    $scope.hash = $routeParams.hash;
   
    $http.get('messages/detail/' + $routeParams.hash).
    success(function(data) {
        $scope.message = data;
        
        if (data.metadata && data.metadata.lat && data.metadata.lon) {
        
	        $scope.messageLocation = {
	        	lat: data.metadata.lat,
	        	lon: data.metadata.lon,
	        	zoom: 16
	        };

	        $scope.markers = [{
		       	lat: data.metadata.lat,
		       	lon: data.metadata.lon,
		       	label: {
                    message: data.message,
                    show: false,
                    showOnMouseOver: true
                }
		    }];
	        
        }
    });
    
  }]);

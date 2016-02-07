'use strict';

/* Controllers */

var p2000Controllers = angular.module('p2000Controllers', []);

p2000Controllers.controller('MessageListCtrl', ['$scope', '$http',
  function($scope, $http) {
	$scope.messages = [];
	
	var ws = new WebSocket("ws://" + window.location.host + window.location.pathname + "messages/live");

	ws.onmessage = function(message) {
		var message = JSON.parse(message.data);
		$scope.messages.unshift(message);
		if ($scope.messages.length > 100) {
			$scope.messages.pop();
		}
		$scope.$apply();
	};
  }]);

p2000Controllers.controller('MessageDetailCtrl', ['$scope', '$routeParams', '$http',
  function($scope, $routeParams, $http) {
	 $scope.capcode = $routeParams.capcode;
	 $scope.timestamp = $routeParams.timestamp;
	   
    $http.get('messages/' + $routeParams.capcode + '/' + $routeParams.timestamp).
    success(function(data) {
        $scope.message = data;
        
        if (data.geodata && data.geodata.location) {
        
	        $scope.messageLocation = {
	        	lat: data.geodata.location.lat,
	        	lon: data.geodata.location.lon,
	        	zoom: 16
	        };

	        $scope.markers = [{
		       	lat: data.geodata.location.lat,
		       	lon: data.geodata.location.lon,
		       	label: {
                    message: data.message,
                    show: false,
                    showOnMouseOver: true
                }
		    }];
	       
	        
        }
    });
    
  }]);

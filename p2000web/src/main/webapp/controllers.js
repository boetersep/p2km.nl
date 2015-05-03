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
        
        if (data.metadata && data.metadata.geodata && data.metadata.geodata.location) {
        
	        $scope.messageLocation = {
	        	lat: data.metadata.geodata.location.lat,
	        	lon: data.metadata.geodata.location.lon,
	        	zoom: 16
	        };

	        $scope.markers = [{
		       	lat: data.metadata.geodata.location.lat,
		       	lon: data.metadata.geodata.location.lon,
		       	label: {
                    message: data.message,
                    show: false,
                    showOnMouseOver: true
                }
		    }];
	        
	        $scope.regions =  [ {
                name: 'Regio\'s',
                source: {
                    type: 'GeoJSON',
                    url: 'inc/regions.json'
                },
                style: {
                    fill: {
                        color: 'rgba(0, 255, 0, 0.6)'
                    },
                    stroke: {
                        color: 'white',
                        width: 3
                    }
                }
            }];
	        
        }
    });
    
  }]);

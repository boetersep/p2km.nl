'use strict';

/* Controllers */

var service = {
	'AMBULANCE': 'ambulance',
	'FIREDEPARTMENT': 'brandweer',
	'KNRM': 'kustwacht',
	'LIFELINER': 'traumaheli',
	'POLICE': 'politie',
	'TEST': '',
	'OTHER': ''
};


var urgency = {
	'HIGH': 'spoed',
	'LOW': '',
	'MEDIUM': 'gepaste spoed'
};

var p2000Controllers = angular.module('p2000Controllers', []);

p2000Controllers.filter('emergencyService', function() {
	return function(input) {
		input = input || [];
		var out = '';
		for (var i = 0; i < input.length; i++) {
			out += service[input[i]];
			out += input.length == i ? '' : ', ';
		}
		return out;
	};
}).filter('emergencyUrgency', function() {
	return function(input) {
		input = input || '';
		var out = urgency[input];
		return out ? 'met ' + out : '';
	};
})


p2000Controllers.controller('MessageListCtrl', ['$scope', '$http',
  function($scope, $http) {
	$scope.messages = [];
	
	var ws = new WebSocket("ws://" + window.location.host + window.location.pathname + "messages/live");

	ws.onmessage = function(message) {
		var message = JSON.parse(message.data);
		if (message.update) {
			angular.forEach($scope.messages, function(candidateMsg) {
				if (message.capcode == candidateMsg.capcode && message.timestamp == candidateMsg.timestamp.$numberLong) {
					angular.extend(candidateMsg, message.data);
					return;
				}
			});
		} else {
			$scope.messages.unshift(message);
			if ($scope.messages.length > 100) {
				$scope.messages.pop();
			}			
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

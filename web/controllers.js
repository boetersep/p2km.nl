'use strict';

/* Controllers */

var serviceValues = {
	'AMBULANCE': 'ambulance',
	'FIREDEPARTMENT': 'brandweer',
	'KNRM': 'kustwacht',
	'LIFELINER': 'traumaheli',
	'POLICE': 'politie'
};

var urgencyValues = {
	'HIGH': 'spoed',
	'LOW': '',
	'MEDIUM': 'gepaste spoed'
};

var p2000Controllers = angular.module('p2000Controllers', []);

p2000Controllers.directive('message', function() {
	return {
		restrict : 'E',
		templateUrl : 'message.html',
		scope : {
			message : '=data',
			detail : '=detail',
		},
		transclude: true,
		controller: ['$scope', function($scope) {
			var watchEmergency = $scope.$watch('message.emergency', function(emergency) {
				if (emergency) {
					var services = emergency.service;
					if (services.indexOf('TEST') > -1) {
						services.splice(services.indexOf('TEST'), 1);						
					}
					if (services.indexOf('OTHER') > -1) {
						services.splice(services.indexOf('OTHER'), 1);
					}
					
					var emergencyText = '';
					for (var i = 0; i < services.length; i++) {
						emergencyText += i == 0 ? serviceValues[services[i]].capitalize() : serviceValues[services[i]];
						emergencyText += (i == services.length - 2 ? ' en ' : (i == services.length - 1 ? '' : ', ' ))  
					}
					var urgency = urgencyValues[emergency.urgency];
					$scope.emergencyText = emergencyText + (urgency ? ' met ' + urgency : '');
				}
			});	
			
			var watchGeodata = $scope.$watch('message.geodata', function(geodata) {
				if (geodata && geodata.source && geodata.source.length) {
					var geodataText = ' naar ';
					var source = geodata.source[0];
					if (source.hectometrering) {
						geodataText += 'de ' + source.weg + ' ter hoogte van hectometerpaal ';
						geodataText += (source.hectometrering / 10.0);
						if (source.street && source.city) {
							geodataText += ' nabij ' + source.street.capitalize() + ' te ' + source.municipality;
						}
					} else {
						geodataText += source.street.capitalize() + (source.housenumber ? ' ' + source.housenumber : '');
						geodataText += ', ' + source.postcode + ' te ' + source.city;
					}
					$scope.geodataText = geodataText;
				}
			});
		}]
	};
});

p2000Controllers.controller('MessageListCtrl', ['$scope', '$http',
  function($scope, $http) {
	$scope.messages = [];
	
	var ws = new WebSocket("ws://" + window.location.host + window.location.pathname + "messages/live");

	ws.onmessage = function(message) {
		var message = JSON.parse(message.data);
		if (message.update) {
			angular.forEach($scope.messages, function(candidateMsg) {
				if (message.capcode == candidateMsg.capcode && message.timestamp == candidateMsg.timestamp) {
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

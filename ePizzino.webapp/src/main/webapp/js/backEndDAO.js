(function() {
	var app = angular.module("SegnalazioniPagamentiApp");

//	var URI = "http://10.144.206.242:8181/cxf/reactive/generic";
//	var URI = "http://localhost:8181/cxf/reactive/generic";

	var backEndDAO = function(cfg, u, $http, $location) {

//		var URI = "http://" + $location.host() + ":" + $location.port() + "/cxf/reactive/generic";
		var URI = "/cxf/reactive/generic";

		var reactivePost = function(ente, user, service, method, body, cbOK, cbKO) {
			var h = {
				"msgUid" : new Date().getTime()+"."+Math.floor(Math.random() * 0x1000), //TODO: real UUID
				"timestamp" : new Date().getTime(),
				"metadata" : {},
				"codApplication" : "ePizzino",
				"invocationContext" : new Date().getTime()+"."+Math.floor(Math.random() * 0x1000), //TODO: real UUID
				"caller" : "ePizzinoWebClient", //TODO: client ip
				"user" : user,
				"codEnte" : ente,
				"service" : service, 
				"method" : method
			};
			var req = {
				"header" : h,
				"body" : body
			};
			
			$http.post(URI, req)
			.then(function(data){
				data = data.data;
				if(cbOK)
					cbOK(data);
				else
					alert(JSON.stringify(data));
//					alert("Operazione terminata " + (data.header.success ? "con successo" : "in modo errato"));
			},
			function(e){
				if(cbKO)
					cbKO(e)
				else
					alert("Operazione terminata con errore: " + e.statusText);
			});
		};
		
		this.save = function($scope, document, send, cbOK, cbKO){			
			var body = {
					"@dto":"JaxbBody",
					"content": document
			};
			var method = send ? "send" : "save";			
			reactivePost($scope.ente, $scope.operatore.account, "ePizzino_segnalazioni", method, body, cbOK, cbKO);
		};


		this.list= function($scope, ente, operatore, richiedente, from, to, stati, cbOK, cbKO){
			var body = {
					"@dto":"JaxbBody",
					"content": {"from" : from, "to" : to,
								"ente": ente, "operatore": operatore, "richiedente": richiedente, 
								"stati" : stati}
			};
			if($scope.operatore!=null)
				reactivePost($scope.ente, $scope.operatore.account, "ePizzino_segnalazioni", "list", body, cbOK, cbKO);
		};

		this.read= function($scope, id, cbOK, cbKO){
			var body = {
					"@dto":"JaxbBody",
					"content": id
			};
			reactivePost($scope.ente, $scope.operatore.account, "ePizzino_segnalazioni", "read", body, cbOK, cbKO);
		};

		this.inviaEMailSuggerimento = function($scope, id, cbOK, cbKO){
			var body = {
					"@dto":"JaxbBody",
					"content": {
						id_segnalazione: $scope.dati._id,
						id_suggerimento: id
					}
			};
			reactivePost($scope.ente, $scope.operatore.account, "ePizzino_segnalazioni", "emailSuggerimento", body, cbOK, cbKO);
		};

		this.pagamentoM1byIdRicevuta = function($scope, id, cbOK, cbKO){
			$http.get('/cxf/simple/command/exec/ePizzino_fedfis/pagamentoM1byIdRicevuta/' + id )
			.then(function (data) {
				cbOK(data.data);
			},
			function (e) {
				if(cbKO)
					cbKO(e);
				else
					alert(e.statusText);
			});
		};

	};

	backEndDAO.$inject = [ "cfg", "util", "$http", "$location" ];

	app.service('backEndDAO', backEndDAO);

})();

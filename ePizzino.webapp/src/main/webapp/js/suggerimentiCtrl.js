(function(){
var app = angular.module("SegnalazioniPagamentiApp");

	app.controller("suggerimentiCtrl", ["$scope", "cfg", "backEndDAO",
	                                    function($scope, cfg, dao) {

		$scope.addSegnalazione = function(s, esito, mezzo){
			if(mezzo!="email" && !confirm("Confermi esito '"+ esito + "' per il Suggerimento?"))
				return;

			var prev = $scope.dati.esitiSuggerimenti[s.id];
			var e = {
				"id": s.id,
				"data": new Date(),
				"cod_operatore": (prev && prev.operatore) ? prev.cod_operatore : $scope.operatore.account,
				"nome_operatore": (prev && prev.operatore) ? prev.nome_operatore : $scope.operatore.nome,
				"mezzo": mezzo ? mezzo : (prev && prev.mezzo) ? prev.mezzo : "voce",
				"esito": esito
			};
			$scope.dati.esitiSuggerimenti[s.id] = e;
		}
		
		$scope.inviaEMailSuggerimento = function(s){
			if(!confirm("EMail a:" +$scope.dati.richiedente.email+"\nSuggerimento: "+s.testo))
				return;
			
			dao.inviaEMailSuggerimento($scope, s.id, 
					function(data){
						$scope.addSegnalazione(s, "email", "email")
					},
					function(e){
						alert("Invio eMail fallita: " + e.statusText);
					});
		}
			
		 $scope.isEMailInviata = function(s){
			 var es = $scope.dati.esitiSuggerimenti[s.id];
			 if(es){
				 if(es.esito=='email')
					 return true;
			 }
			 return false;
		 }

		 $scope.getSuggerimento = function(id){
			 var es = $scope.suggerimenti[id];
			 if(!es)
				 return "???";
			 
			 return es.testo;
		 }
	} ]);
	
})();	
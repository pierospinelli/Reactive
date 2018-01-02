(function(){
	var app = angular.module("SegnalazioniPagamentiApp");

	app.controller("segnalazioneCtrl", ["$scope", "bke", "util", 
	                                    function($scope, bke, u) {
		bke.addChecked($scope);
		$scope.isComplete = function() {
			try {
				var c = $scope.dati.contatto;
				var r = $scope.dati.richiedente;
				var p = $scope.dati.pagamento;
				var s = $scope.dati.segnalazione;

				if(!c.canale)
					return false;
				if(!s.tipo)
					return false;

				if((c.canale == 'M' || c.canale == '0')
					&& !u.checkFilled(c.testo))
					return false;
				
				
				if(!u.checkFilled(r.denominazione))
					return false;

					
				if((s.tipo == 'NA' || s.canale == 'NA')
					&& !u.checkFilled(s.note))
					return false;
					
				if(s.canale == 'PW'){
					if(!u.checkFilled(s.autenticazione))
						return false;
					if(!s.applicazione)
						return false;
					if(s.applicazione == 'NA'
						&& !u.checkFilled(s.applicazioneSpec))
					return false;

				}
					
				if(!(u.checkFilled(p.IUV)
				  || u.checkFilled(p.ricevuta)
				  || u.checkFilled(p.idSA))){

					if(!(u.checkFilled(p.data) && u.checkFilled($scope.richiedente.idFiscale)))
						return false;

				}

				return true;
			} catch (e) {
				return false;
			}
		};
		
		
		$scope.openEmail = false;
		$scope.toggleEmail = function(){
			$scope.openEmail = ! $scope.openEmail;
		};
		
		$scope.showInfoButton = function(){
			return true; //TODO
		};

		$scope.findInfoPagamento = function(){
			bke.findInfoPagamento();
//			alert("Finestra con le informazioni dei pagamenti per utente e/o contribuente e/o codici inseriti"); //TODO
		};

		$scope.findInfoPosizione = function(){
			alert("Finestra con le informazioni dei pagamenti per utente e/o contribuente e/o codici inseriti"); //TODO
		};
		
		$scope.onChangeProblema = function(oldVal, newVal){
			if($scope.dati.segnalazione.tipo=="CA"){
				$scope.dati.segnalazione.canale="PW";
				$scope.dati.segnalazione.autenticazione="PF";
				$scope.dati.segnalazione.applicazione="SA";
				$scope.dati.pagamento.credito="8";
			}
		}
		
		
	} ]);

})();
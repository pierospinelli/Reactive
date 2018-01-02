(function(){
	var app = angular.module("SegnalazioniPagamentiApp");

	var app = angular.module("SegnalazioniPagamentiApp");

	app.controller("mainCtrl", ["$scope", "cfg", "util", "bke", 
	                            function($scope, cfg, u, bke) {
		bke._setMainCtrl($scope);
		
		_skipDirty = false;
		$scope.enti = cfg.enti;
		$scope.stati = cfg.stati;
		$scope.contatti = cfg.contatti;
		$scope.crediti = cfg.crediti;
		$scope.sistemi = cfg.sistemi;
		$scope.problemi = cfg.problemi;		
		$scope.canaliPagamento = cfg.canaliPagamento;
		$scope.suggerimenti = cfg.suggerimenti;

		
		$scope.decodeContatto = function(code){
			return _decode(this.contatti, code);
		}
		$scope.decodeCredito = function(code){
			return _decode(this.crediti, code);
		}
		$scope.decodeSistema = function(code){
			return _decode(this.sistemi, code);
		}
		$scope.decodeProblema = function(code){
			return _decode(this.problemi, code);
		}
		$scope.decodeEnte = function(code){
			var e = this.enti[codice];
			if(!e)
				return null;
			return e.nome;
		}

		$scope.logout = function(){
			$scope.$root.operatore = undefined;
		};
		
		$scope.clearEnte = function(){
			$scope.ente = "Tutti";
			$scope.segnalazioni = bke.listSegnalazioni();
		}
		
		$scope.setEnte = function(e){
			$scope.ente = e;
			$scope.segnalazioni = bke.listSegnalazioni();
		}

		$scope.setAccesso = function(a){
			$scope.accesso = a;
			$scope.segnalazioni = bke.listSegnalazioni();
		}

		$scope.isBackOffice = function(){
			return ($scope.operatore && $scope.operatore.ruolo=="backoffice");
		}
		
		$scope.isEnteManager = function(){
			return ($scope.operatore && $scope.operatore.ruolo=="entemanager");
		}
		
		$scope.nuovo = function(){
			bke.nuovo(false);
		}
		
		$scope.isComplete = function(){
			return bke.isComplete();
		}

		$scope.save = function(){
			bke.save();
		}
		
		$scope.read = function(id){
			bke.read(id);
		}
		
		$scope.send = function(forced){
			bke.send(forced);
		}

		$scope.changeStatus = function(s){
			bke.changeStatus(s);
		}

		$scope.dropDraft = function(){
			var m = u.compare($scope.dati.original, $scope.dati, 
					['_id', 'history', 'original', 'dataUltimaSpedizione', 'modificato', 'esitiSuggerimenti'])
			if(confirm("Confermando perderai le seguenti modifiche:\n"
					+ JSON.stringify(m)+"\n\nConfermi?")){
				bke.dropDraft();
			}
		}
		
		$scope.segnalazioni = [];		
		$scope.showList = function(){
			if($scope.dirty){
				if(!confirm("Segnalazione modificata. Salva ed esci?"))
					return;
				$scope.save();
			}

			$scope.segnalazioni = bke.listSegnalazioni();
			$scope.lista=true;
		}

		
		$scope.setFiltroElenco = function(filtro){
			bke.setFiltroElenco(filtro);
		}
		$scope.getFiltroElenco = function(){
			return bke.nomeFiltroElenco;
		}


		$scope.decodeContatto = function(code){
			return cfg.decodeContatto(code);
		}
		$scope.decodeCredito = function(code){
			return cfg.decodeCredito(code);
		}
		$scope.decodeProblema = function(code){
			return cfg.decodeProblema(code);
		}
		
		$scope.showModifiche = function(event){
			alert(JSON.stringify(event.modifiche));
		}
		
		$scope.setFiltroElenco("BOZZE E ATTIVE");

		$scope.lista = true;
		

	} ]);
	
})();

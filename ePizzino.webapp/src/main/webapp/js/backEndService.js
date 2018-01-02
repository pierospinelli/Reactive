(function(){
var app = angular.module("SegnalazioniPagamentiApp");

var bkeService = function(cfg, u, $filter, backEndDAO) {

	this.$scope = undefined;

	function calcSuggerimenti(scope){
		scope.hasSuggerimenti=false;
		scope.currentSuggerimenti=[];
		
		for(var s in scope.suggerimenti){
			 var es = scope.dati.esitiSuggerimenti[s];
			 if(es){
				 if(es.esito!='email')
					 continue;
			 }
			 var sug = scope.suggerimenti[s];
			 try{
				 scope.dati.show = sug.show
				 if(scope.dati.show()){
					 scope.currentSuggerimenti.push(sug);
					 scope.hasSuggerimenti=true;
				 }
			 }catch(e){
				 //TODO: tracciare
			 }
		}
		 scope.dati.show = undefined;
	}

	this._setMainCtrl = function(scope){
		this.$scope = scope;
		
		var _wDirty = function( newValue, oldValue ){
			if(newValue){
				calcSuggerimenti(scope);
				if(_skipDirty){
					_skipDirty = false;
					return;
				}
				scope.dirty = true;
				scope.dati.modificato = true;
			}
		};
		scope.$watch('dati', _wDirty, true);
	}
	
	var _skipDirty=false;
	this.nuovo = function(skipConfirm){
		var $scope = this.$scope;
		if(!skipConfirm)
			if(!confirm("Nuova Segnalazione"))
				return;
		
		$scope.dati = {};
		$scope.dati._id = u.newUUID($scope.operatore.account);
		$scope.dati.contatto = {};
		$scope.dati.richiedente = {};
		$scope.dati.segnalazione = {};
		$scope.dati.pagamento = {};
		$scope.dati.pagamento.data =  new Date(2018, 0, 01, 0, 0)
		$scope.dati.history = [];
		$scope.dati.esitiSuggerimenti = {};

		var _now = new Date();
		$scope.dati.contatto.data = $scope.dati.dataCreazione = 
			new Date(_now.getFullYear(), _now.getMonth(), _now.getDate(), _now.getHours(), _now.getMinutes())
		$scope.dati.dataSpedizione = undefined;
		$scope.dati.mittente = $scope.operatore;
		$scope.dati.ente = $scope.ente && $scope.ente!='Tutti' ? $scope.ente : $scope.operatore.ente;
		$scope.dati.stato = "BOZZA"
			

		_skipDirty=true;
		$scope.dirty = false;
		$scope.lista = false;

	}
	
	this.checkeds = [];
	this.addChecked = function(scope){
		this.checkeds.push(scope);
	}
	this.isComplete = function() {
		if(!this.$scope.operatore)
			return false;

		if(!this.$scope.dati.mittente)
			return false;

		var index;
		for (index = 0; index < this.checkeds.length; ++index) {
		   if(!this.checkeds[index].isComplete())
		   	return false;
		}
		return true;
	}
	
	
	this.save = function(){
		
		var $scope = this.$scope;
		
		backEndDAO.save($scope, $scope.dati, false, 
			function(data){
				if(data.header.success){
					$scope.dirty = false;
				}else
					alert("Salvataggio fallito: "+data.header.message);
			},
			function(e){
				alert("Salvataggio fallito");
			});
	}

	
	
	
	this.read = function(id){
		var $scope = this.$scope;
		
		backEndDAO.read($scope, id, 
			function(data){
				if(data.header.success){
					var letto = data.body.content;
					if(letto.original){
						$scope.dati = letto;
					} else {
						$scope.dati = angular.copy(letto);
						$scope.dati.original = letto;
					}
					
					$scope.lista=false;
		
					_skipDirty=true;
					$scope.dirty = false;
				}else
					alert("Apertura segnalazione fallita");
			}, 
			function(e){
				alert("Apertura segnalazione fallita");
			});
	}

	
	this.changeStatus = function(s){
		this.$scope.dati.stato=s;
		this.send();
	}

	this.dropDraft = function(){
		_skipDirty=true;
		if(this.$scope.dati.original)
			this.$scope.dati = this.$scope.dati.original;
		this.$scope.dati.modificato = false;
		this.save();
		this.$scope.dati.original = angular.copy(this.$scope.dati);
	}

	
	
	this.send = function(forced){
		_skipDirty=true;

		var $scope = this.$scope;
		if(forced)
			$scope.dati.forced="FORZATURA";
		else
			$scope.dati.forced=undefined;
		
		//TODO: rendere il commento non obbligatorio con una modal dedicata
		var commento = prompt("Commenti e Confermi la spedizione"+(forced ? " forzata?" : "?"))
		if(commento==null || commento=="")
			return; 
		
		var ID = $scope.dati._id;
		

		var olds = {
				"stato" : $scope.dati.stato,
				"dataSpedizione" : $scope.dati.dataSpedizione,
				"dataUltimaSpedizione" : $scope.dati.dataUltimaSpedizione,
				"modificato": $scope.dati.modificato,
				"original": $scope.dati.original
		};
		
		$scope.dati.dataUltimaSpedizione = new Date();
		if($scope.dati.stato=="BOZZA"){
			$scope.dati.stato = "APERTA";	
			$scope.dati.dataSpedizione = new Date();
		}
		$scope.dati.modificato = false;
		$scope.dati.original = undefined;

		var event = {
				"data": new Date(), 
				"cod_mittente": $scope.operatore.account, 
				"nome_mittente": $scope.operatore.nome, 
				"stato_apertura": olds.stato, 
				"stato_chiusura": $scope.dati.stato, 
				"commento": commento,
				"modifiche": u.compare(olds.original, $scope.dati, 
						['_id', 'history', 'original', 'dataUltimaSpedizione', 'modificato', 'esitiSuggerimenti'])
		};
		$scope.dati.history.push(event);

		
		backEndDAO.save($scope, $scope.dati, true, 
			function(data){
				_skipDirty = true;
				$scope.dirty = false;
				$scope.dati.original = angular.copy($scope.dati);
				$scope.dati.modificato = false;
			},
			function(e){
				$scope.dati.history.pop();
				$scope.dati.original = olds.original;
				$scope.dati.modificato = olds.modificato;
				$scope.dati.dataUltimaSpedizione = olds.dataUltimaSpedizione;
				$scope.dati.stato = olds.stato;	
				$scope.dati.dataSpedizione = olds.dataSpedizione;
				alert("Spedizione fallita: ...");
			});

		
	}
	
	
	
	this.filtriElenco=[];
	this.nomeFiltroElenco=undefined;
	this.setFiltroElenco = function(filtro){
		this.nomeFiltroElenco=filtro;
		if(!filtro){
			this.filtriElenco=[];
		}else if(filtro=='BOZZE E ATTIVE'){
			this.filtriElenco=['BOZZA', 'APERTA', 'IN_CARICO', 'IN_LAVORAZIONE', 'ATTESA_INFO'];
		}else if(filtro=='BOZZE'){
			this.filtriElenco=['BOZZA'];
		}else if(filtro=='ATTIVE'){
			this.filtriElenco=['APERTA', 'IN_CARICO', 'IN_LAVORAZIONE', 'ATTESA_INFO'];
		}else if(filtro=='CHIUSE'){
			this.filtriElenco=['RISOLTA', 'CHIUSA'];
		}else if(filtro=='SOSPESE'){
			this.filtriElenco=['SOSPESA'];
		}
		this.$scope.segnalazioni = this.listSegnalazioni();
	};

	this.listSegnalazioni = function(){
		var $scope = this.$scope;
		$scope.segnalazioni = {};
		
		if(!$scope.operatore)
			return;
		
		var segn_ente = null;
		var segn_operatore = null;
		
		if($scope.operatore.ruolo=='operatore'
			 || ($scope.operatore.ruolo=="entemanager" && $scope.accesso!="ente")){
			 segn_operatore = $scope.operatore.account;
			 segn_ente = $scope.operatore.ente;
		 } else {
			  if($scope.operatore.ruolo=='backoffice')
				  segn_ente = $scope.ente=="Tutti" ? null : $scope.ente;
			  else
				  segn_ente = $scope.operatore.ente;
		 }
		 
		
		backEndDAO.list($scope, segn_ente, segn_operatore, null, $scope.dallaData, $scope.allaData, this.filtriElenco,  
				function(data){
					$scope.segnalazioni = {};
					if(data.header.success){
						var ss = data.body.content;
						for(var sid in ss)
							$scope.segnalazioni[sid] = ss[sid];
					}else
						alert("Caricamento elenco fallita: " + data.header.message);
				},
				function(e){
					$scope.segnalazioni = {};
					alert("Caricamento elenco fallita: " + e.statusText);
				});
	};

	
	this.findInfoPagamento = function(){
		var $scope = this.$scope;
		if(!$scope.dati.pagamento.ricevuta || $scope.dati.pagamento.ricevuta.trim().length==0){
			alert("Inserire Il numero di ricevuta");
			return;
		}
		backEndDAO.pagamentoM1byIdRicevuta($scope, $scope.dati.pagamento.ricevuta,
				function(data){
					if(!data.header.success){
						alert("Richiesta fallita: "+data.header.message);
						return;
					}
					alert(JSON.stringify(data.body.content));
				});
				
	};

};

bkeService.$inject = ["cfg", "util", "$filter", "backEndDAO"];

app.service('bke', bkeService);

})();

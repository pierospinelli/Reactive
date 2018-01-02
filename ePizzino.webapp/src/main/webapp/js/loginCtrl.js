(function(){
var app = angular.module("SegnalazioniPagamentiApp");

	app.controller("loginCtrl", ["$scope", "cfg", "bke", "$location",
	                     function($scope, cfg, bke, $location) {
		$scope.account=null;
		$scope.pswd=null;
		$scope.error=null;

		$scope.login = function(){
			$scope.error=null;
			var u = cfg.operatori[$scope.account];
			if(u==null){
				$scope.error="Account sconosciuto";
				return;
			}
			if(u.pswd!=$scope.pswd){
				$scope.error="Password errata";
				return;
			}
			
			$scope.$root.operatore = angular.copy(u);
			$scope.$root.operatore.account = $scope.account;
			$scope.$root.operatore.pswd = undefined;
			$scope.pswd = undefined;
			
			$scope.ente = $scope.$root.operatore.ruolo=="backoffice" ? "Tutti" : $scope.$root.operatore.ente;
//			bke.nuovo(true);			
			
			bke.setFiltroElenco("BOZZE E ATTIVI");
			$scope.$root.segnalazioni = bke.listSegnalazioni();
			$scope.$root.accesso="ente";
			
			var segId = $location.search().segId;
			if(segId){
				$scope.$root.lista = false;
				$scope.$root.dirty = false; //??
				bke.read(segId);
			} else {
				$scope.$root.dirty = true; //??
				$scope.$root.lista = true;
			}

	
		};

	} ]);
	
})();	
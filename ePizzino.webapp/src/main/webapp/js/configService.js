(function(){
var app = angular.module("SegnalazioniPagamentiApp");


var cfgService = function($http) {
	
	var getConfig = function (col, dest, key, multi){
		$http.get('/cxf/simple/command/exec/ePizzino_config/' + col )
		.then(function (data) {
			data = data.data;
			if(!data.success){
				alert(data.message);
				return;
			}
			var res = data.collection;
			if(!key){
				for(var i=0;i<res.length;i++)
					dest.push(res[i]);
			} else {
				for(var i=0;i<res.length;i++){
					if(multi){
						if(!dest[res[i][key]])
							dest[res[i][key]] = [];
						dest[res[i][key]].push(res[i])
					} else {
						dest[res[i][key]] = res[i];
					}
				}
			}
		},
		function (e) {
			alert(e.statusText);
		});
	};
	
	var loadSuggerimenti = function (col, key){
		$http.get('/cxf/simple/command/exec/ePizzino_config/suggerimenti')
		.then(function (data) {
			data = data.data;
			if(!data.success){
				alert(data.message);
				return;
			}
			var res = data.collection;

			for(var i=0;i<res.length;i++){
				var sug = res[i];
				var js = sug.show;
				try{
					eval("sug.show = " + js)
					col[sug[key]] = sug;
				}catch(e){
					window.status = e.statusText;
					sug.show = function(){return false;}
				}
			}

		},
		function (e) {
			alert(e);
		});
	};

	this.enti={}; getConfig("enti", this.enti, "codice");
	this.operatori={}; getConfig("operatori", this.operatori, "account");
	this.stati=[]; getConfig("stati", this.stati);
	this.contatti=[]; getConfig("contatti", this.contatti);
	this.crediti=[]; getConfig("crediti", this.crediti);
	this.sistemi=[]; getConfig("sistemi", this.sistemi);
	this.problemi=[]; getConfig("problemi", this.problemi);
	this.canaliPagamento={}; getConfig("canaliPagamento", this.canaliPagamento, "type", true);
	this.suggerimenti={}; loadSuggerimenti(this.suggerimenti, "id");

	function _decode(conf, code){
		for(var i=0; i<conf.length; i++){
			var c = conf[i];
			if(c.value==code)
				return c.label;
		}
		return null;
	}

	this.decodeModoPagamento = function(code){
		for(k in this.modiPagamento){
			for(var i=0; i<this.modiPagamento[k].length; i++){
				var c = this.modiPagamento[k][i];
				if(c.value==code)
					return c.label;
			}
		}
		return null;
	}


};
	
cfgService.$inject = ["$http"];

app.service('cfg', cfgService);
})();
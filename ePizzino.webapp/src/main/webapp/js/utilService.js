(function(){
var app = angular.module("SegnalazioniPagamentiApp");

app.service('util', function() {

	this.checkFilled = function(f){
		try{
			if(f==null)
				return false;
			if(f==undefined)
				return false;
			if(f.trim().length==0)
				return false;
			
			return true;
			
		}catch(e){
			return false;
		}
	}
		
	this.newUUID = function(prex) {
		if(prex)
			return prex + new Date().getTime();
	    return _p8() + _p8(true) + _p8(true) + _p8();
	}
	
	function _p8(s) {
	    var p = (Math.random().toString(16)+"000000000").substr(2,8);
	    return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p ;
	}
	
	this.compare = function (original, draft, unmatched) {

		  var result = {
		    modificati: {},
		    aggiunti: {},
		    eliminati: {}
		  };
		  
		 function _match1(original, draft, path){
				if(!(original && draft))
					return;

				for (var key in original) {
					if(unmatched.indexOf(key) > -1)
						continue;
					
					var origVal = original[key];
					if( typeof(origVal) == "string" && origVal.trim()=="")
						continue;
					
					var newVal = draft.hasOwnProperty(key) ? draft[key] : null;
					if(newVal!=null && typeof(newVal) == "string" && newVal.trim()=="")
						newVal = null;
					
					if (newVal) {
						if (angular.equals(origVal, newVal))
							continue;

						if (typeof (origVal) != typeof ({})
								|| typeof (newVal) != typeof ({})) {
							result.modificati[path+key] = newVal;
						} else {
							_match1(origVal, newVal, path+key+"_");
						}

					} else {
						result.eliminati[path+key] = origVal;
					}
				}
		 }
				
		 function _match2(original, draft, path){
				if(!(original && draft))
					return;

				for (var key in draft) {
					if(unmatched.indexOf(key) > -1)
						continue;
					
					var newVal = draft[key];
					if( typeof(newVal) == "string" && newVal.trim()=="")
						continue;
					
					var origVal = original.hasOwnProperty(key) ? original[key] : null;
					if(origVal!=null && typeof(origVal) == "string" && origVal.trim()=="")
						origVal = null;
					
					if (origVal) {
						if (angular.equals(origVal, newVal))
							continue;

						if (typeof (newVal) == typeof ({})
								&& typeof (origVal) == typeof ({})) {
							_match1(origVal, newVal, path+key+"_");
						}

					} else {
						result.eliminati[path+key] = newVal;
					}
				}
		 }

	
	
		 
		 	_match1(original, draft, "");
		 	_match2(original, draft, "");
		 	return result;

	};

});

})();

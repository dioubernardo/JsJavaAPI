
var JsJavaAPI = (function(){
	
	var callbacks = [];
	var ws;
	
	return {
	
		init: function(){
			if (!window.WebSocket && window.MozWebSocket)
			    window.WebSocket = window.MozWebSocket;
			return (!window.WebSocket) ? false : true;
		},
		
		connect: function(){
			var deferred = Q.defer();
			if (ws != null){
				deferred.resolve();
				return deferred.promise;
			}
			
			ws = new WebSocket("ws://localhost:1206/jja");
			ws.onopen = function(){
				deferred.resolve();
			};
			ws.onerror = function(error){
				deferred.reject(error);
			};
			ws.onclose = function(closeEvent){
				ws = null;
	        };
	        ws.onmessage = function(msg){
				try{
					var data = JSON.parse(msg.data);
					callbacks[data.id].call(null, data);
				}catch(ex){
				}
			};
	        return deferred.promise;
		},
		
		exec: function(method, params){
			var deferred = Q.defer();
			
			if (!ws){
				callback.reject();
			}else{
				var id = setTimeout(function(){
					deferred.reject(null);
				}, 3000);
				callbacks[id] = function(data){
					
					clearTimeout(id);
					
					switch(data.action){
					case 1:
						deferred.resolve(data.data);
						callbacks[id] = null;
						break;
					case 2:
						deferred.notify(data.data);
						break;
					case 3:
						deferred.reject(data.data);
						callbacks[id] = null;
					}
				};
				ws.send(JSON.stringify({
					id: id,
					method: method,
					params: params||{}
				}));
			}
			
			return deferred.promise;			
		}
	};
	
})();

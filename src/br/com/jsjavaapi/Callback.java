package br.com.jsjavaapi;

import org.eclipse.jetty.websocket.api.Session;
import org.json2.JSONObject;

public abstract class Callback implements Runnable, Cloneable {

	protected Session sess;
	protected Object id;
	protected JSONObject params;

	public void setContext(Session s, Object i, JSONObject p) {
		sess = s;
		id = i;
		params = p;
	}

	public void clearTimeout(){
		send(null, 0);
	}
	
	public void resolve(JSONObject params){
		send(params, 1);
	}

	public void notify(JSONObject params){
		send(params, 2);
	}
	
	public void reject(JSONObject params){
		send(params, 3);
	}
	
	public void error(String msg){
		JSONObject err = new JSONObject();
		err.put("error", msg);
		reject(err);
	}
	
	private void send(JSONObject data, int action){
		JSONObject msgOut = new JSONObject();
		msgOut.put("id", id);
		msgOut.put("action", action);
		msgOut.put("data", data);
		sess.getRemote().sendString(msgOut.toString(), null);
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}

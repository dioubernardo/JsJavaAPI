package br.com.jsjavaapi;

import java.awt.EventQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.json2.JSONObject;

public class Listener implements WebSocketListener {

	private Session sess;

	public void onWebSocketClose(int statusCode, String reason) {
		sess = null;
	}

	public void onWebSocketConnect(Session session) {
		sess = session;
	}

	public void onWebSocketError(Throwable cause) {
	}

	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
	}

	public void onWebSocketText(String message) {
		if ((sess != null) && (sess.isOpen())) {

			JSONObject in = new JSONObject(message);
			NullCallback nullcb = new NullCallback();

			try {
				String method = in.getString("method");
				if (!JsJavaAPI.calbacks.containsKey(method))
					throw new Exception("Método inválido");
				Callback call = JsJavaAPI.calbacks.get(method);
				Callback callRun = (Callback) call.clone();
				callRun.setContext(sess, in.get("id"), in.getJSONObject("params"));
				EventQueue.invokeLater(callRun);
			} catch (Exception e) {
				nullcb.setContext(sess, in.get("id"), null);
				nullcb.error(e.getMessage());
			}
		}
	}

}

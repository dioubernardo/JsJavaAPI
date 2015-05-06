package br.com.jsjavaapi;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class JsJavaAPI {
	
	public static HashMap<String, Callback> calbacks;
	private Server server;
	
	public JsJavaAPI(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			Image image = new ImageIcon(this.getClass().getResource("/icon.png")).getImage();
			
			final SystemTray tray = SystemTray.getSystemTray();
			final TrayIcon trayIcon = new TrayIcon(image);
			trayIcon.setImageAutoSize(true);
			
			PopupMenu popup = new PopupMenu();
			MenuItem exitItem = new MenuItem("Fechar");
			exitItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                tray.remove(trayIcon);
	                System.exit(0);
	            }
	        });			
			
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);
			tray.add(trayIcon);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		calbacks = new HashMap<String, Callback>();
		
		server = new Server(1206);
		ServletContextHandler cntx = new ServletContextHandler(ServletContextHandler.SESSIONS);
		cntx.setContextPath("/");
		server.setHandler(cntx);

		ServletHolder holder = new ServletHolder("jja", new WebSocketServlet(){
			private static final long serialVersionUID = -9072135903790589347L;
			public void configure(WebSocketServletFactory factory){
				factory.register(Listener.class);
			}
		});
		cntx.addServlet(holder, "/jja");
	}

	public void start(){
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(String method, Callback callback){
		calbacks.put(method, callback);
	}
	
}

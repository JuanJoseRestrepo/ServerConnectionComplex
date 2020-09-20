package control;

import comm.Receptor.OnMessageListener;
import comm.Session;
import javafx.application.Platform;
import model.DirectMessage;
import model.Generic;
import model.Message;
import model.UserMessage;


import com.google.gson.Gson;

import comm.TCPConnection;
import view.ChadWindow;
import comm.TCPConnection.OnConnectionListener;

public class ChadController implements OnMessageListener, OnConnectionListener {

	
	private ChadWindow view;
	private TCPConnection connection;
	
	public ChadController(ChadWindow view) {
		this.view = view;
		init();
	}
	
	public void init() {
		connection = TCPConnection.getInstance();
		connection.setPuerto(5000);
		connection.start();
		connection.setConnectionListener(this);
		connection.setMessageListener(this);
	}

	@Override
	public void onMessage(Session s,String message) {
		//
		Gson gson = new Gson();
		Generic type = gson.fromJson(message,Generic.class);
		
		switch(type.getType()) {
		case "Message":
			connection.sendBroadCast(message);
			break;
		case "DirectMessage":
			DirectMessage m = gson.fromJson(message,DirectMessage.class);
			Message normal = new Message(m.getId(),m.getBody(),m.getDate());
			String json = gson.toJson(normal);
			connection.sendDirectMessage(m.getClientId(),json);
			break;
			
		case "UserMessage":
			
			UserMessage userM = gson.fromJson(message, UserMessage.class);
			System.out.println(userM);
			s.setUserName(userM.getId());
			connection.addUser(s, userM.getId());
			break;
			
		}
		
		//UI
	/*	Platform.runLater(
				
				()->{
					
					Message msjObj = gson.fromJson(message, Message.class);
					
					view.getMessageArea().appendText("<<<"+ msjObj.getDate() + " : "+ msjObj.getBody() + "\n");
				}
				
				
				);
	}
	*/
	}

	@Override
	public void onConnection(String id) {
	Platform.runLater(
				
				()->{
					
					view.getMessageArea().appendText("<<< Nuevo cliente conectado!:" + id + ">>>\n");
				}
				
				
				);
	}

	@Override
	public void onDisconection(Session s) {
		
		connection.getSalaDeEspera().remove(s);

	}

	@Override
	public void OnRepeatConnection(Session s,String reason) {
		
	Platform.runLater(
				
				()->{
					
					view.getMessageArea().appendText("<<< Alto ahi:" + reason + ">>>\n");
				}
				
				
				);
	}
	
	
	
	
	}

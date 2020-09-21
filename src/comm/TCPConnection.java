package comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.Gson;

import comm.Receptor.OnMessageListener;
import model.Message;
import model.NewConnection;


public class TCPConnection extends Thread {


	private static TCPConnection instance = null;
	
	
	//GLOBAL
	private int puerto;
	private ServerSocket server;
	private OnConnectionListener connectionListener;
	private OnMessageListener messageListener;
	private ArrayList<Session> conectados;
	private ArrayList<Session> salaDeEspera;
	
	private  TCPConnection() {
		// TODO Auto-generated constructor stub
		conectados = new ArrayList<Session>();	
		salaDeEspera = new ArrayList<Session>();		
	}

	
	
	public ArrayList<Session> getSalaDeEspera() {
		return salaDeEspera;
	}



	public void setSalaDeEspera(ArrayList<Session> salaDeEspera) {
		this.salaDeEspera = salaDeEspera;
	}



	public static synchronized TCPConnection getInstance() {
		
		if(instance == null) {
			instance = new TCPConnection();
		}
		return instance;
	}
	
	
	@Override
	public void run() {

		try {
			
			server = new ServerSocket(puerto);
			
			while(true) {
				System.out.println("Esperando cliente...");
				Socket socket = server.accept();
				System.out.println("Nuevo cliente Conectado =)");
				System.out.println("Espere la respuesta del usuario...");
				Session session = new Session(socket);
				
				salaDeEspera.add(session);
				
				setAllSessionsInProgram(messageListener);
			}
		
				
		}catch(IOException e) {
			
		}
			
	}

	public void setConnectionListener(OnConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
	}

	public void setAllSessionsInProgram(OnMessageListener listener) {
		for(int i = 0; i < salaDeEspera.size();i++) {
		Session s = salaDeEspera.get(i);
		s.getReceptor().setList(listener);
		}
	}

	public void setPuerto(int puerto) {
	
		this.puerto = puerto;
		
	}
	
	public interface OnConnectionListener{
		public void onConnection(String id);
		public void OnRepeatConnection(Session s,String reason);
	}

	public OnMessageListener getMessageListener() {
		return messageListener;
	}


	public void setMessageListener(OnMessageListener messageListener) {
		this.messageListener = messageListener;
	}
	
	public void actualize() {
		Gson json = new Gson();
        for(int i = 0; i < conectados.size();i++) {
        	Session s = conectados.get(i);
            System.out.println(conectados.size());
            for(int j = 0; j < conectados.size();j++) {
            	if(!(s.getUserName().equalsIgnoreCase(conectados.get(j).getUserName()))) {
            		System.out.println(conectados.get(j).getUserName() + "  " + j);
                    NewConnection m = new NewConnection(conectados.get(j).getUserName(),"","");
                    String objetoNuevo = json.toJson(m);
                    s.getEmisor().setMessage(objetoNuevo);	
            	}
            }

        }

    }
	
	public ArrayList<Session> sendActualize(ArrayList<Session> sesions,String id) {

        ArrayList<Session> aux = new ArrayList<>();
        for(int i = 0; i < sesions.size();i++) {

            if(!(sesions.get(i).getUserName().equalsIgnoreCase(id))) {

                aux.add(sesions.get(i));

            }

        }

        return aux;
    }
	
	public void sendBroadCast(String msg) {
		
		for(int i = 0; i < conectados.size();i++) {
			Session s = conectados.get(i);
			s.getEmisor().setMessage(msg);
		}
		
	}

	public Session getSession(String id) {
		Session aux = null;
		boolean t = false;
		for(int i = 0; i < conectados.size() && !t;i++) {
			if(conectados.get(i).getUserName().equals(id)) {
				
				aux = conectados.get(i);
				t = true;
				
			}
		}
		
		return aux;
		
	}
	
	public void addUser(Session s,String msg) {
        int index = salaDeEspera.indexOf(s);
        Gson json = new Gson();

        if(estaRepetido(msg)) {

            String msj = "Ese usuario ya existe";
            Message m = new Message(s.getUserName(),"","");
            String msj1 = json.toJson(m);
            connectionListener.OnRepeatConnection(s,msj);
            //s.getEmisor().setMessage(msj1);
        }else {

            salaDeEspera.remove(index);
            conectados.add(s);
            NewConnection m = new NewConnection(s.getUserName(),"","");
            String msj = json.toJson(m);
            connectionListener.onConnection(s.getUserName());
            s.getEmisor().setMessage(msj);
            
            actualize();

        }
	}

	public void sendDirectMessage(String id,String json) {
		boolean t = false;
		for(int i = 0; i < conectados.size() && !t;i++) {
			
			if(conectados.get(i).getUserName().equalsIgnoreCase(id)) {
				conectados.get(i).getEmisor().setMessage(json);
				t = true;
			}
		}
	}
	
	public Boolean estaRepetido(String nombreDeUsuario) {
		Boolean t = false;
		for(int i = 0; i < conectados.size() && !t;i++) {
			if(conectados.get(i).getUserName().equals(nombreDeUsuario)) {
			
				t = true;
				
			}
		}
		
		return t;
	}

}

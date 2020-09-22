package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Receptor extends Thread {

	private InputStream is;
	private Session s;
	public OnMessageListener listener;
	
	public Receptor(Session s,InputStream is){
		this.is = is;
		this.s = s;
	}
	
	@Override
	public void run() {
		
		BufferedReader breader = new BufferedReader(new InputStreamReader(this.is));
		
		while(true) {
			
			try {
				String msj = breader.readLine();
				if(msj == null) {
					s.closeSocket();
					listener.onDisconection(s);
				}else {
				
				//System.out.println("Recibido:" + msj);
				listener.onMessage(s,msj);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void setList(OnMessageListener listener) {
		this.listener = listener;
	}
	
	
	public interface OnMessageListener{

		public void onMessage(Session s,String message);
		public void onDisconection(Session s);
	}

	
	
}

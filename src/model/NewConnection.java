package model;

public class NewConnection {

	public String type = "NewConnection";
	private String id;
	private String body;
	private String date;
	
	public NewConnection() {
		
	}
	
	public NewConnection(String id,String body,String date) {
		this.id = id;
		this.body = body;
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getType() {
		return type;
	}
	
}
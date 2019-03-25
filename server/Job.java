import java.net.Socket;

public class Job {
	private Socket socket;
	private int clientNum;

	public Job (Socket socket, int clientNum) {
		this.socket = socket;
		this.clientNum = clientNum;
	}
	
	public Socket getSocket () {
		return this.socket;
	}
	
	public int getClient () {
		return this.clientNum;
	}
}

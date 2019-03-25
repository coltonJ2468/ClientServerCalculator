import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class MyMonitor extends Thread {
	private LinkedList<Job> queue;
	private final static int V = 1;
	private final static int CAPACITY = 50;
	private ThreadPool pool;
	private boolean running;
	
	public MyMonitor () {
		this.queue = new LinkedList<Job> ();
		this.pool = new ThreadPool(this);
		this.pool.startPool();
		this.running = true;
	}

	public void run () {
		while (running) {
			pool.increaseThreadsInPool (queue.size());
			pool.decreaseThreadsInPool (queue.size());
			try {
				sleep (V * 1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void enqueue (Socket socket, int clientNum) {
		if (!running) {
			try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Server shutting down");
			}
			catch (IOException e){
				e.printStackTrace();
			}
			finally {
	            try {
	                socket.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		}
		if (queue.size() < CAPACITY) {
			this.queue.add(new Job (socket, clientNum));
			notify();
			System.out.println("Queued: " + queue.size());
		}
	}
	
	public synchronized Job dequeue () {
		if (queue.size() == 0) {
			return null;
		}
		System.out.println("Dequeueing");
		return queue.remove();
	}
	
	public boolean isEmpty () {
		if (queue.size() == 0) {
			return true;
		}
		return false;
	}
	
	public void kill () {
		this.running = false;
	}
	
	public synchronized boolean isRunning () {
		return this.running;
	}
}
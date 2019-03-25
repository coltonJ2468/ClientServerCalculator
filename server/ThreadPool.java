import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.print.attribute.standard.DateTimeAtCompleted;

class ThreadPool {
	private int maxThreads;
	private int numThreads;
	WorkerThread[] holders;
	private boolean stopped;

	MyMonitor queue;

	public final static int T1 = 10;
	public final static int T2 = 20;

	public ThreadPool (MyMonitor queue) {
		this.maxThreads = 5;
		this.numThreads = 0;
		this.queue = queue;
		this.holders = new WorkerThread[5];
	}

	public void startPool () {
		for (int ix = 0; ix < holders.length; ix ++) {
			this.holders[ix] = new WorkerThread ();
			this.holders[ix].start();
		}
	}

	public void increaseThreadsInPool (int numJobs) {
		if ((T1 < numJobs && numJobs <= T2 && maxThreads != 10) ||
			(numJobs > T2 && maxThreads != 40)) {
			System.out.println("Increasing");
			maxThreads = holders.length * 2;
			WorkerThread[] newArray = new WorkerThread[maxThreads];
			for (int ix = 0; ix < holders.length; ix ++) {
				newArray[ix] = holders[ix];
			}
			for (int ix = holders.length; ix < newArray.length; ix ++) {
				newArray[ix] = new WorkerThread ();
				newArray[ix].start();
			}
			holders = newArray;
         System.out.println("Thread manager doubled number of threads in the pool");
		}
	}

	public void decreaseThreadsInPool (int numJobs) {
		if (maxThreads != 5 && maxThreads / 2 > numJobs) {
			maxThreads = holders.length / 2;
			WorkerThread[] newArray = new WorkerThread[holders.length];
			for (int ix = 0; ix < newArray.length; ix ++) {
				newArray[ix] = holders[ix];
			}
			holders = newArray;
         System.out.println("Thread manager halved the number of threads in the pool");
		}
	}

	public synchronized void stopPool () {
		stopped = true;
		queue.kill();
	}

	public int numberThreadsRunning () {
		return numThreads;
	}

	public int maxCapacity () {
		return maxThreads;
	}

	private class WorkerThread extends Thread {
		public void run () {
			while (!stopped || !queue.isEmpty()) {
				synchronized (queue) {
					while (queue.isEmpty()) {
						try {
							queue.wait();
						} 
                        catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				numThreads ++;
				Job job = queue.dequeue();
				Socket socket = job.getSocket();
				int clientNumber = job.getClient();
				try {
	                BufferedReader in = new BufferedReader(
	                        new InputStreamReader(socket.getInputStream()));
	                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

	                out.println("Hello, you are client #" + clientNumber + ".");
	                out.println("Enter a line with only a period to quit\n");

	                while (true) {
	                    String input = in.readLine();
	                    if (input == null || input.equals(".")) {
	                        break;
	                    }
	                    if (input.equals("KILL")) {
	                    	out.println("Shutting down server");
	                    	stopPool();
	                    	break;
	                    }
	                    else {
		                    String[] params = new String[3];
		                    params = input.split(",");
		                    String command = params[0];
		                    int num1 = Integer.parseInt(params[1]);
		                    int num2 = Integer.parseInt(params[2]);
		                    if (command.equals("ADD")) {
		                    	out.println(num1 + " + " + num2 + " = " + (num1 + num2));
		                    }
		                    else if (command.equals("SUB")) {
		                    	out.println(num1 + " - " + num2 + " = " + (num1 - num2));
		                    }
		                    else if (command.equals("MUL")) {
		                    	out.println(num1 + " * " + num2 + " = " + (num1 * num2));
		                    }
		                    else if (command.equals("DIV")) {
		                    	out.println(num1 + " / " + num2 + " = " + (num1 / num2));
		                    }
	                    	System.out.println("Worker thread id = " + clientNumber + " processed service request " + input);
	                    }
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    numThreads --;
	                    socket.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
			}
		}
	}
	
	
}

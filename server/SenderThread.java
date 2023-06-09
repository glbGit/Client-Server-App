import java.io.PrintWriter;

public class SenderThread implements Runnable {

	private PrintWriter pw;
	private boolean flag;
	private String sent = "";
	
	public SenderThread(PrintWriter pw) {
		flag = false;
		this.pw = pw;
	}

	// Sender thread 
	public void run() {
		flag = true;
		while (flag) {
			String toSend = "";
			double check = Math.random();
			if (check > 0.5) {
				toSend = "1";
			} else {
				toSend = "0";
			}
			pw.println(toSend);
			pw.flush();
			sent += toSend;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	// Caller thread
	public void stop() {
		// Closing pw handled by caller
		flag = false;
		pw.println("0");
		pw.flush();
		pw.println("1");
		pw.flush();
		pw.println("0");
		pw.flush();
		pw.println("*");
		pw.flush();
			
		sent+="010";
		
		String md5 = Integer.toString(sent.hashCode());
		System.out.println(md5);
		pw.println(md5);
		pw.flush();
		
		pw.println("stop");
		pw.flush();
	}
}

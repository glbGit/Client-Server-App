
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientListener implements ActionListener {

	public static final String START = "start", STOP = "stop", CONNECT = "connect", DISCONNECT = "disconnect";

	private JTextField ipAddressField;
	private JTextField portaField;
	private JTextField matricolaField;

	private boolean connected = false, transmitting = false;
	private Downloader downloader = null;

	private PrintWriter netPw;
	private Scanner scan;
	private Socket sock;
	private BinaryDownloaderFrame frame;

	public ClientListener(BinaryDownloaderFrame frame, JTextField ipAddr, JTextField porta, JTextField matricola) {
		this.frame = frame;
		this.ipAddressField = ipAddr;
		this.portaField = porta;
		this.matricolaField = matricola;
	}

	private void setupConnection() throws UnknownHostException, IOException {
		sock = new Socket(ipAddressField.getText(), Integer.parseInt(portaField.getText()));
		OutputStream os = sock.getOutputStream();
		netPw = new PrintWriter(new OutputStreamWriter(os));
		scan = new Scanner(sock.getInputStream());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(ClientListener.CONNECT)) {
			try {
				setupConnection();
				connected = true;
				System.out.println("Connected");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Cannot connect to server: \n" + e1.getMessage());
				e1.printStackTrace();
				return;
			}

			JOptionPane.showMessageDialog(null, "Connected to server. ");
		} else if (cmd.equals(ClientListener.START)) {
			try {
				downloader = new Downloader(matricolaField.getText(), scan);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Failed to create file: \n" + e1.getMessage());
				e1.printStackTrace();
			}
			transmitting = true;
			netPw.println(cmd);
			netPw.flush();

			Thread t = new Thread(downloader);
			t.start();
			JOptionPane.showMessageDialog(null, "Downloading... ");
		} else if (cmd.equals(ClientListener.STOP)) {
			netPw.println(cmd);
			netPw.flush();
			transmitting = false;
			JOptionPane.showMessageDialog(null, "Download paused. ");
		} else if (cmd.equals(ClientListener.DISCONNECT)) {
			netPw.println(ClientListener.DISCONNECT);
			netPw.flush();
			netPw.close();
			scan.close();
			connected = false;
			try {
				sock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Disconnected. ");
		}
		frame.setButtons(connected, transmitting);
	}
}

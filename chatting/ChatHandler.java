package chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatHandler extends Thread{

	Socket socket; //���� ��ü

	BufferedReader input; //�Է� ��Ʈ��

	PrintWriter output; //��� ��Ʈ��

	ChatServer server; //���� ��ü

	ChatClient chatClient; //ä�� ��ü


	/** ������, ���ϰ�ü�κ��� ����� ��Ʈ���� �� */
	public ChatHandler(ChatServer server, Socket socket) throws IOException{
		this.server = server;
		this.socket = socket;

		input = new BufferedReader(
				new InputStreamReader(socket.getInputStream())
		);
		output = new PrintWriter(
				new OutputStreamWriter(socket.getOutputStream())
		);
	}

	/** Ŭ���̾�Ʈ�� ���� �޼����� �д� �޼ҵ� */
	public void run() {
		String name = "";
		try {
			/* Ŭ���̾�Ʈ�� ���� �޼����� ���� */
			name = input.readLine();
			broadcastMessage(name + "�� ����.");

			/* ������ Ŭ���̾�Ʈ�� ���� �޼����� ���� �� �ֵ��� ���ѷ����� ó�� */
			while (true) {
				/* Ŭ���̾�Ʈ�� ���� �޼��� ���� */
				String message = input.readLine();
				broadcastMessage(name + " > " + message);
			}
		} catch (Exception ex) {
			System.out.println(name + "�� ����, " + "  IP: " + socket.getInetAddress());
		} finally { //ä��â�� �ݱ���ָ� Ŭ���ϸ� ����
			server.handlers.removeElement(this);
			broadcastMessage(name + " �� ����");

			try {
				input.close();
				output.close();
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/** ���� ������ ��� Ŭ���̾�Ʈ���� �޼����� ���� */
	protected void broadcastMessage(String message) {
		//��� ����ڵ鿡�� �޼����� �߰��ϴ� ����
		//���Ϳ��� Ŭ���̾�Ʈ�� �߰��� ���Ű� �ȵ�

		synchronized (server.handlers) {
			// ���� ���;ȿ� �ִ� Ŭ���̾�Ʈ�� ���� ��
			int onlineCount = server.handlers.size();

			// ������ ��� ����ڿ��� �޼����� ������ ���� ������� ����ŭ �ݺ�
			for (int i = 0; i < onlineCount; i ++) {
				ChatHandler handler = server.handlers.elementAt(i); // Ŭ���̾�Ʈ �ϳ��� ��
				try {
					synchronized (handler.output) {
						handler.output.println(message); // Ŭ���̾�Ʈ���� �޼����� ����
					}
					handler.output.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}

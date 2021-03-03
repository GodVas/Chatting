package chatting;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer{

	Vector<ChatHandler> handlers;


	public ChatServer(int port) {
		try {
			ServerSocket socket = new ServerSocket(port); //���� ���� ��ü����, ����ڴ� �ִ� 50���� ���� �� ����
			ChatClient client = new ChatClient();
			handlers = new Vector<ChatHandler>(); 		  //Ŭ���̾�Ʈ�� �����ϴ� ���׻���
			System.out.println("ä�� ���� �غ� �Ϸ�");

			while (true) { // ������ Ŭ���̾�Ʈ�� ���� �� �ֵ��� ���ѷ��� ó��
				Socket accept = socket.accept(); //���ϰ�ü ��
				System.out.println("������ Ŭ���̾�Ʈ IP: " + accept.getInetAddress());
				ChatHandler handler = new ChatHandler(this, accept); // Ŭ���̾�Ʈ�� 1���� ChatHandler ��ü ����
				handlers.addElement(handler); //Ŭ���̾�Ʈ ���� ���Ϳ� ������ Ŭ���̾�Ʈ �߰�
				handler.start(); //ChatHandler Ŭ������ run()�޼ҵ尡 ȣ���
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ChatServer(ChatClient.PROXY_PORT);
	}
}
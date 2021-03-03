package chatting;

import java.awt.*;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 1L;

	BufferedReader input;   //�Է� ��Ʈ��

	PrintWriter output;     //��� ��Ʈ��

	Thread handler;         //ChatHandler �� �޼����� �ְ� �ޱ� ���� ������

	Container container;    //Container ���̾ƿ� ���� ����

	JTextArea display;      //ä��â���� ��ȭ�� ǥ��

	JTextField id;          //����� id

	JTextField inData;      //����ڰ� �޼����� �Է��ϴ� �ʵ�

	JLabel displayId;       //ä��â�� id�� ǥ���ϴ� ���̺�

	JButton send;           //������ ���� ��ư

	CardLayout window;


	public static final String PROXY_NETWORK = "127.0.0.1";

	public static final int PROXY_PORT = 5000;


	/** �����ڷ� ä��â�� UI�� ������ */
	public ChatClient() {
		super("ä�� Ŭ���̾�Ʈ");

		container = getContentPane();
		window = new CardLayout();
		container.setLayout(window);

		/* �α��� â�� ���� */
		JPanel login = new JPanel(new BorderLayout());
		JPanel button = new JPanel();
		JLabel idLabel = new JLabel("�α���");

		/* ���̵� �Է��ʵ��� ������ ������ ��� */
		id = new JTextField(15);
		id.addActionListener(this);

		/* �α���â�� ������Ʈ ��ġ */
		button.add(idLabel);
		button.add(id);
		login.add("South", button);
		login.setBackground(Color.CYAN);

		/* ä��â�� ���� */
		JPanel chat = new JPanel(new BorderLayout());

		/* ä��â�� ��ȭ ǥ�� �ؽ�Ʈ������ ���� �� ��ũ�ѹ� �߰�, ��ġ */
		display = new JTextArea(10, 30);
		JScrollPane scrollPane = new JScrollPane(display);
		display.setBackground(Color.PINK);
		chat.add("Center", scrollPane); //�г� �߰�
		display.setEditable(false); //��ȭǥ�� ȭ�鿡 ���Ƿ� �Է� ����

		/* ä��â�� �޼����Է°� ������ ��ư ���� �� ��ġ */
		JPanel panel = new JPanel();
		panel.add(new JLabel("�޼���"));

		/* �޼��� �Է��ʵ��� ������ ������ ���, ��ġ */
		inData = new JTextField(20);
		panel.add(inData);

		/* ������ ��ư�� ������ ������ ���, ��ġ */
		send = new JButton("������");
		panel.add(send);
		send.addActionListener(this);

		/* ä��â�� ������Ʈ ��ġ */
		chat.add("South", panel);
		displayId = new JLabel();
		chat.add("North", displayId);
		chat.add("chat", chat);
		window.show(chat, "login");

		setSize(400, 400);
		setVisible(true);
	}

	/** ChatHandler �� �޼����� �ְ�޴� ���� �ϴ� �����带 ���� �� �����Ŵ */
	public void onRunClientThread() {
		handler = new Thread(this);
		handler.start();
	}

	/** ������ �ڵ鷯�� ChatHandler �� ���� �޼����� �޾Ƽ� ��ȭȭ�鿡 ǥ�� */
	public void execute() {
		try{
			while (true) { //������ �޼����� ����
				/* ���� �޼����� ��ȭȭ�鿡 ǥ�� */
				String line = input.readLine();
				display.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/* �޼��� �޴� ���� �ߴܵɶ� ���� */
			stop();
		}
	}

	public void stop() {
		if (handler != null) {
			try {
				if (output != null) {
					input.close();
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/* ����� ������ ���� */
		handler = null;
	}

	public static void main(String[] args) {
		ChatClient chatClient = new ChatClient();
		chatClient.setDefaultCloseOperation(EXIT_ON_CLOSE);
		chatClient.onRunClientThread();
	}

	/** ������� ���̵� ó���� ������ �ڵ鷯�� �޼����� ������ ���� ó�� */
	@Override
	public void actionPerformed(ActionEvent event) {
		/* �̺�Ʈ�� �߻��� ������Ʈ �� */
		Component component = (Component) event.getSource();

		/* �̺�Ʈ�� �߻��� ������Ʈ�� ���̵� �Է��ʵ��̸� ���� */
		if (component == id) {
			/* ���̵� �� ä��â�� �������� ǥ�� */
			String name = id.getText().trim();
			displayId.setText(name);

			/* ���̵� �Է����� ������ �����ߴ�. */
			if (name.length() == 0) {
				return;
			}

			/* ������ �ڵ鷯�� ChatHandler �� �޼��� ���� */
			output.println(name);
			output.flush();

			/* ä��â�� ǥ�õǵ����� */
			window.show(container, "chat");
			inData.requestFocus();
		} else if (component == inData || component == send) { //�̺�Ʈ�� �߻��� �޼��� �̓� �ʵ峪 ������ ��ư�̸� ����
			/* ������ �ڵ鷯�� ChatHandler �� �޼��� ���� */
			output.println(inData.getText());
			output.flush();
		}
	}

	/** �����带 ����� �ڵ����� ���� */
	@Override
	public void run() {
		try {
			/* ���ϰ�ü ���� */
			Socket socket = new Socket(PROXY_NETWORK, PROXY_PORT);

			/* ����½�Ʈ�� �� */
			input = new BufferedReader(
					new InputStreamReader(socket.getInputStream())
			);
			output = new PrintWriter(
					new OutputStreamWriter(socket.getOutputStream())
			);

			/* ������ �ڵ鷯�� ���� �޼����� �޴� execute() �޼ҵ� */
			execute();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
}
package chatting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * 
 * @author ������ ���� : �������� ���� ��ư �߰�
 *
 */

public class MyClientSocket extends JFrame {

	// ������ ������ ���� ����
	private JPanel top;
	private JPanel center;
	private JPanel bottom;
	private JTextField textBox;
	private JButton send;
	private String msg;
	private JPanel msgBox;
	private JScrollPane textBoxScroll;

	// �������� ���� ���� ����
	private JButton all;
	private JButton chat;
	private boolean isAll = true;
	private boolean isChat = false;

	// ������ ���� ����
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private boolean isLogin = true;
	private String userName;

	// ���̵� �Է� Ȯ�� ����
	private boolean isId = false;

	public MyClientSocket() {
		initObject();
		initSetting();
		addObject();
		btn();
		initListener();
		connect();
		setVisible(true);
	}

	private void initObject() {

		top = new JPanel();
		center = new JPanel();
		bottom = new JPanel();
		textBox = new JTextField(30);
		send = new JButton("����");
		msgBox = new JPanel();
		all = new JButton("ALL");
		chat = new JButton("CHAT");
		textBoxScroll = new JScrollPane(textBox);

	}

	private void initSetting() {
		setTitle("ä�����α׷�");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		top.setBackground(Color.yellow);
		top.setLayout(new FlowLayout());
		top.setPreferredSize(new Dimension(400, 40));

		center.setBackground(Color.pink);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		textBoxScroll.setBackground(Color.orange);
		textBoxScroll.setPreferredSize(new Dimension(400, 40));

	}

	private void addObject() {

		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(textBoxScroll, BorderLayout.SOUTH);

		top.add(all);
		top.add(chat);
		top.add(send);

	}

	private void initListener() {
		textBox.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msg = textBox.getText();
					textBox.setText("");
					protocol();

				}
			}
		});

	}

	private void connect() {
		try {

			// ���� ����
			socket = new Socket("localhost", 2000);

			// ���� �ޱ�
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			read();

		} catch (Exception e) {
			System.out.println("������� : " + e.getMessage());
		}
	}

	// �б� �޼���
	private void read() {
		// �б� ���� ���ο� ������ ����
		new Thread(() -> {
			try {
				center.add(msgBox.add(new JLabel("���̵� �Է��ϼ���.")));
				while (isLogin) {
					String inputData = reader.readLine();
//					System.out.println(inputData);
					center.add(msgBox.add(new JLabel(inputData)));
					center.revalidate();
					center.repaint();
				}
			} catch (Exception e) {
				try {
					System.out.println("���������� : " + e.getMessage());
					isLogin = false;
					writer.close();
					reader.close();
					socket.close();
				} catch (Exception e2) {
					System.out.println("�������� ���� : " + e2.getMessage());
				}

			}
		}).start();

	}

	private void writeAll() {
		try {
			if (!isId) {
				userName = msg;
				writer.write(userName + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel("ID�� ���۵Ǿ����ϴ�.")));
				center.add(msgBox.add(new JLabel("ID : " + userName)));
				center.revalidate();
				center.repaint();
				isId = true;
			} else if (isId) {

				writer.write("ALL:" + msg + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel(msg)));
				center.revalidate();
				center.repaint();
			}
		} catch (Exception e) {
			System.out.println("������ �����ϴ�.");
		}
	}

	private void writeChat() {
		try {
			if (!isId) {
				userName = msg;
				writer.write(userName + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel("ID�� ���۵Ǿ����ϴ�.")));
				center.add(msgBox.add(new JLabel("ID : " + userName)));
				center.revalidate();
				center.repaint();
				isId = true;
			} else if (isId) {

				writer.write("CHAT:" + msg + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel(msg.substring(5))));
				center.revalidate();
				center.repaint();
			}
		} catch (Exception e) {
			System.out.println("������ �����ϴ�.");
		}
	}

	// ��ư Ŭ���� �̺�Ʈ ����
	private void btn() {

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == send) {
					msg = textBox.getText();
					textBox.setText("");
					protocol();
				}
			}
		});

		all.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == all) {
					isAll = true;
					isChat = false;

				}
			}
		});

		chat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == chat) {
					msg = textBox.getText();
					isAll = false;
					isChat = true;
				}
			}
		});
	}

	private void protocol() {
		if (isAll) {
			writeAll();
		} else if (isChat) {
			writeChat();
		}
	}

	public static void main(String[] args) {
		MyClientSocket clientSocket = new MyClientSocket();

	}
}

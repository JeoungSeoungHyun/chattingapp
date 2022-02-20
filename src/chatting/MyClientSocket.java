package chatting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * @author ������ ���� : ���� ����
 *
 */

public class MyClientSocket extends JFrame {

	// ������ ������ ���� ����
	private String msg;
	private JPanel top;
	private JPanel msgBox;
	private JPanel bottom;
	private JTextArea center;
	private JButton send;
	private JButton connect;
	private JTextField textBox;
	private JTextField ip;
	private JTextField port;
	private ScrollPane scroll;
	private JPanel test;

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
	private String myName;

	// ���̵� �Է� Ȯ�� ����
	private boolean isId = false;

	// ������ Ȯ�� ���� ����
	private JPanel west;
	private JButton userList;
	private Set<String> users;
	private ScrollPane scroll2;
	private JTextArea userBox;

	// �ӼӸ� ���� ����
	private String receiver;
	private JTextField chatBox;

	public MyClientSocket() {
		initObject();
		initSetting();
		addObject();
		btn();
		initListener();
		setVisible(true);
	}

	// ������Ʈ ���� �޼���
	private void initObject() {

		top = new JPanel();
		center = new JTextArea();
		textBox = new JTextField(30);
		send = new JButton("����");
		msgBox = new JPanel();
		all = new JButton("       ALL      ");
		chat = new JButton("     CHAT     ");
		bottom = new JPanel();
		west = new JPanel();
		userList = new JButton("  UserList  ");
		users = new HashSet<>();
		scroll = new ScrollPane();
		scroll2 = new ScrollPane();
		userBox = new JTextArea();
		ip = new JTextField(15);
		port = new JTextField(10);
		connect = new JButton("connect");
		chatBox = new JTextField();
		test = new JPanel();
	}

	// ���� �޼���
	private void initSetting() {
		setTitle("ä�����α׷�");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		top.setBackground(Color.yellow);
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.setPreferredSize(new Dimension(400, 40));

		center.setBackground(Color.pink);
		center.setEditable(false);

		bottom.setPreferredSize(new Dimension(400, 40));
		bottom.setBackground(Color.orange);

		west.setPreferredSize(new Dimension(100, 100));
		west.setBackground(Color.lightGray);
		west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));

		userList.setAlignmentX(CENTER_ALIGNMENT);
		all.setAlignmentX(CENTER_ALIGNMENT);
		chat.setAlignmentX(CENTER_ALIGNMENT);

		userBox.setEditable(false);

		ip.setText("127.0.0.1");
		port.setText("2000");

		test.setLayout(new BorderLayout());

		all.setPreferredSize(new Dimension(40, 30));

	}

	// ������Ʈ �߰� �޼���
	private void addObject() {

		add(top, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		add(test, BorderLayout.WEST);

		test.add(west, BorderLayout.CENTER);
		test.add(chatBox, BorderLayout.SOUTH);

		top.add(ip);
		top.add(port);
		top.add(connect);

		west.add(userList);
		west.add(scroll2);
		west.add(all);
		west.add(chat);
//		west.add(chatBoxPanel);
//		chatBoxPanel.add(chatBox);

		scroll.add(center);
		scroll2.add(userBox);

		bottom.add(textBox);
		bottom.add(send);
	}

	// Ű���� �Է� �ν� ������ -> Enter�Է½� �޼��� ����
	private void initListener() {
		textBox.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msg = textBox.getText();
					msg = msg + "";
					textBox.setText("");
					protocol();
				}
			}
		});

	}

	// ���� ����� ���۴ޱ�
	private void connect() {
		try {

			// ���� ����
			socket = new Socket(ip.getText(), Integer.parseInt(port.getText()));

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
				center.append("���̵� �Է��ϼ���.\n");
				while (isLogin) {
					String inputData = reader.readLine();
					if (inputData.startsWith("USER:")) {
						user(inputData);
					} else {
						center.append(inputData + "\n");
					}
				}
			} catch (Exception e) {
				try {
					center.append("������ �����Ǿ����ϴ�\n");
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

	// ��ü ä�� �޼���
	private void writeAll() {
		try {
			if (!isId) {
				myName = msg;
				writer.write(myName + "\n");
				writer.flush();
				center.append("ID�� ���۵Ǿ����ϴ�.\n");
				center.append("ID : " + myName + "\n");
				isId = true;
			} else if (isId) {

				writer.write("ALL:" + msg + "\n");
				writer.flush();
				center.append(myName + ": " + msg + "\n");
			}
		} catch (Exception e) {
			System.out.println("������ �����ϴ�.");
		}
	}

	// �ӼӸ� �޼���
	private void writeChat() {
		try {
			if (isId) {
				try {
					writer.write("CHAT:" + receiver + ":" + msg + "\n");
					writer.flush();
					center.append(receiver + "����: " + msg + "\n");

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.out.println("������ �����ϴ�.");
		}
	}

	// ��ư Ŭ���� �̺�Ʈ ����
	private void btn() {

		// ���� ��ư
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == connect) {
					connect();
				}
			}
		});

		// ���� ��ư
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

		// ��ü ä�� ��ư
		all.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == all) {
					isAll = true;
					isChat = false;
				}
			}
		});

		// �ӼӸ� ��ư
		chat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == chat) {
					isAll = false;
					isChat = true;
					receiver = chatBox.getText();
				}
			}
		});

		// ������ ����Ʈ Ȯ�� ��ư
		userList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == userList) {
					try {
						writer.write("USER:\n");
						writer.flush();
					} catch (Exception e3) {
						e3.getMessage();
					}
				}
			}
		});
	}

	// ��ư�� ���� �������� �ڵ�ó�� ���ִ� �޼���
	private void protocol() {
		if (isAll) {
			writeAll();
		} else if (isChat) {
			writeChat();
		}

	}

	// ������ Ȯ�� �޼���
	private void user(String inputData) {
		users.clear();
		userBox.setText("");
		String[] token = inputData.split(":");
		for (int i = 1; i < token.length; i++) {
			users.add(token[i]);
		}
		userBox.append("������ �� : " + users.size() + "��\n");
		for (String s : users) {
			userBox.append(s + "\n");
		}
	}

	public static void main(String[] args) {
		MyClientSocket clientSocket = new MyClientSocket();

	}
}

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
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * 
 * @author ������ ���� : ������ ��ư���� �ӼӸ��ϱ�
 *
 */

public class MyClientSocket extends JFrame {

	// ������ ������ ���� ����
	private JPanel top;
	private JPanel center;
	private JTextField textBox;
	private JButton send;
	private String msg;
	private JPanel msgBox;
	private JScrollPane bottom;

	// �������� ���� ���� ����
	private JButton all;
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
//	private String userName;
	private boolean isUser = false;
	private JButton userList;
	private Set<JButton> users;

	// �ӼӸ� ���� ����
	private String receiver;

	public MyClientSocket() {
		initObject();
		initSetting();
		addObject();
		btn();
		initListener();
		connect();
		setVisible(true);
	}

	// ������Ʈ ���� �޼���
	private void initObject() {

		top = new JPanel();
		center = new JPanel();
		textBox = new JTextField(30);
		send = new JButton("����");
		msgBox = new JPanel();
		all = new JButton("ALL");
		bottom = new JScrollPane(textBox);
		west = new JPanel();
		userList = new JButton("UserList");
		users = new HashSet<>();

	}

	// ���� �޼���
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

		bottom.setPreferredSize(new Dimension(400, 40));

		west.setPreferredSize(new Dimension(100, 100));
		west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));

	}

	// ������Ʈ �߰� �޼���
	private void addObject() {

		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		add(west, BorderLayout.WEST);

		top.add(userList);
		top.add(all);
		top.add(send);

	}

	// Ű���� �Է� �ν� ������ -> Enter�Է½� �޼��� ����
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

	// ���� ����� ���۴ޱ�
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
					if (inputData.startsWith("USER:")) {
						user(inputData);
					} else {
						center.add(msgBox.add(new JLabel(inputData)));
						center.revalidate();
						center.repaint();
					}
				}
			} catch (Exception e) {
				try {
					center.add(msgBox.add(new JLabel("������ �����Ǿ����ϴ�")));
					center.revalidate();
					center.repaint();
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
				center.add(msgBox.add(new JLabel("ID�� ���۵Ǿ����ϴ�.")));
				center.add(msgBox.add(new JLabel("ID : " + myName)));
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

	// �ӼӸ� �޼���
	private void writeChat() {
		try {
			if (isId) {
				try {
					writer.write("CHAT:" + receiver + ":" + msg + "\n");
					writer.flush();
					center.add(msgBox.add(new JLabel(receiver + "����: " + msg)));
					center.revalidate();
					center.repaint();

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
					isUser = false;
				}
			}
		});

		// ������ ����Ʈ Ȯ�� ��ư
		userList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == userList) {
					isAll = false;
					isChat = false;
					isUser = true;
					protocol();
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
		} else if (isUser) {
			try {
				writer.write("USER:\n");
				writer.flush();

			} catch (Exception e) {
				e.getMessage();
			}
		}
	}

	// ������ Ȯ�� �޼���
	private void user(String inputData) {
		users.clear();
		west.removeAll();
		String[] token = inputData.split(":");
		for (int i = 1; i < token.length; i++) {
			users.add(new JButton(token[i]));
		}
		west.add(new JLabel("������ �� : " + users.size() + "��"));
		for (JButton s : users) {
			chat(s);
			west.add(s);
			west.revalidate();
			west.repaint();
		}
	}

	private void chat(JButton userName) {
		userName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == userName) {
					isAll = false;
					isChat = true;
					receiver = userName.getText();
				}
			}
		});
	}

	public static void main(String[] args) {
		MyClientSocket clientSocket = new MyClientSocket();

	}
}

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
 * @author 정성현 목적 : 접속자 목록 보여주기
 *
 */

public class MyClientSocket extends JFrame {

	// 프레임 구성을 위한 변수
	private JPanel top;
	private JPanel center;
	private JTextField textBox;
	private JButton send;
	private String msg;
	private JPanel msgBox;
	private JScrollPane bottom;

	// 프로토콜 구분 위한 변수
	private JButton all;
	private JButton chat;
	private boolean isAll = true;
	private boolean isChat = false;

	// 소켓을 위한 변수
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private boolean isLogin = true;
	private String myName;

	// 아이디 입력 확인 변수
	private boolean isId = false;

	// 접속자 확인 위한 변수
	private JPanel west;
	private String userName;
	private boolean isUser = false;
	private JButton userList;

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
		textBox = new JTextField(30);
		send = new JButton("전송");
		msgBox = new JPanel();
		all = new JButton("ALL");
		chat = new JButton("CHAT");
		bottom = new JScrollPane(textBox);
		west = new JPanel();
		userList = new JButton("UserList");

	}

	private void initSetting() {
		setTitle("채팅프로그램");
		setSize(600, 500);
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

	private void addObject() {

		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		add(west, BorderLayout.WEST);

		top.add(userList);
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

			// 소켓 연결
			socket = new Socket("localhost", 2000);

			// 버퍼 달기
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			read();

		} catch (Exception e) {
			System.out.println("연결실패 : " + e.getMessage());
		}
	}

	// 읽기 메서드
	private void read() {
		// 읽기 위한 새로운 스레드 생성
		new Thread(() -> {
			try {
				center.add(msgBox.add(new JLabel("아이디를 입력하세요.")));
				while (isLogin) {
					String inputData = reader.readLine();
//					System.out.println(inputData);
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
//					System.out.println("연결해제됨 : " + e.getMessage());
					center.add(msgBox.add(new JLabel("연결 해제됨")));
					center.revalidate();
					center.repaint();
					isLogin = false;
					writer.close();
					reader.close();
					socket.close();
				} catch (Exception e2) {
					System.out.println("연결해제 실패 : " + e2.getMessage());
				}

			}
		}).start();

	}

	private void writeAll() {
		try {
			if (!isId) {
				myName = msg;
				writer.write(myName + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel("ID가 전송되었습니다.")));
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
			System.out.println("연결이 없습니다.");
		}
	}

	private void writeChat() {
		try {
			if (!isId) {
				myName = msg;
				writer.write(myName + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel("ID가 전송되었습니다.")));
				center.add(msgBox.add(new JLabel("ID : " + myName)));
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
			System.out.println("연결이 없습니다.");
		}
	}

	// 버튼 클릭시 이벤트 설정
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
					isUser = false;
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
					isUser = false;
				}
			}
		});

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

	private void user(String inputData) {
		String[] token = inputData.split(":");
		west.add(new JLabel(token[1]));
		west.revalidate();
		west.repaint();
	}

	public static void main(String[] args) {
		MyClientSocket clientSocket = new MyClientSocket();

	}
}

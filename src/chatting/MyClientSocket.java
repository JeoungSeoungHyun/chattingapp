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
 * @author 정성현 목적 : 구성 변경
 *
 */

public class MyClientSocket extends JFrame {

	// 프레임 구성을 위한 변수
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
	private JButton userList;
	private Set<String> users;
	private ScrollPane scroll2;
	private JTextArea userBox;

	// 귓속말 위한 변수
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

	// 오브젝트 생성 메서드
	private void initObject() {

		top = new JPanel();
		center = new JTextArea();
		textBox = new JTextField(30);
		send = new JButton("전송");
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

	// 설정 메서드
	private void initSetting() {
		setTitle("채팅프로그램");
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

	// 오브젝트 추가 메서드
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

	// 키보드 입력 인식 리스너 -> Enter입력시 메세지 전송
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

	// 소켓 연결과 버퍼달기
	private void connect() {
		try {

			// 소켓 연결
			socket = new Socket(ip.getText(), Integer.parseInt(port.getText()));

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
				center.append("아이디를 입력하세요.\n");
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
					center.append("연결이 해제되었습니다\n");
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

	// 전체 채팅 메서드
	private void writeAll() {
		try {
			if (!isId) {
				myName = msg;
				writer.write(myName + "\n");
				writer.flush();
				center.append("ID가 전송되었습니다.\n");
				center.append("ID : " + myName + "\n");
				isId = true;
			} else if (isId) {

				writer.write("ALL:" + msg + "\n");
				writer.flush();
				center.append(myName + ": " + msg + "\n");
			}
		} catch (Exception e) {
			System.out.println("연결이 없습니다.");
		}
	}

	// 귓속말 메서드
	private void writeChat() {
		try {
			if (isId) {
				try {
					writer.write("CHAT:" + receiver + ":" + msg + "\n");
					writer.flush();
					center.append(receiver + "에게: " + msg + "\n");

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.out.println("연결이 없습니다.");
		}
	}

	// 버튼 클릭시 이벤트 설정
	private void btn() {

		// 연결 버튼
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == connect) {
					connect();
				}
			}
		});

		// 전송 버튼
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

		// 전체 채팅 버튼
		all.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == all) {
					isAll = true;
					isChat = false;
				}
			}
		});

		// 귓속말 버튼
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

		// 접속자 리스트 확인 버튼
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

	// 버튼을 통해 프로토콜 자동처리 해주는 메서드
	private void protocol() {
		if (isAll) {
			writeAll();
		} else if (isChat) {
			writeChat();
		}

	}

	// 접속자 확인 메서드
	private void user(String inputData) {
		users.clear();
		userBox.setText("");
		String[] token = inputData.split(":");
		for (int i = 1; i < token.length; i++) {
			users.add(token[i]);
		}
		userBox.append("접속자 수 : " + users.size() + "명\n");
		for (String s : users) {
			userBox.append(s + "\n");
		}
	}

	public static void main(String[] args) {
		MyClientSocket clientSocket = new MyClientSocket();

	}
}

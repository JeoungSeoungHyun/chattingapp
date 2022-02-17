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
 * @author 정성현 목적 : 접속자 목록 출력하기
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
	private Set<String> users;

	public MyClientSocket() {
		initObject();
		initSetting();
		addObject();
		btn();
		initListener();
		connect();
		setVisible(true);
	}

	// 오브젝트 생성 메서드
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
		users = new HashSet<>();

	}

	// 설정 메서드
	private void initSetting() {
		setTitle("채팅프로그램");
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

	// 오브젝트 추가 메서드
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

	// 키보드 입력 인식 리스너 -> Enter입력시 메세지 전송
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

	// 소켓 연결과 버퍼달기
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
					center.add(msgBox.add(new JLabel("연결이 해제되었습니다")));
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

	// 전체 채팅 메서드
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

	// 귓속말 메서드
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
					isUser = false;
				}
			}
		});

		// 귓속말 버튼
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

		// 접속자 리스트 확인 버튼
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

	// 버튼을 통해 프로토콜 자동처리 해주는 메서드
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

	// 접속자 확인 메서드
	private void user(String inputData) {
		users.clear();
		west.removeAll();
		String[] token = inputData.split(":");
		for (int i = 1; i < token.length; i++) {
			users.add(token[i]);
		}
		west.add(new JLabel("접속자 수 : " + users.size() + "명"));
		for (String s : users) {
			west.add(new JButton(s));
			west.revalidate();
			west.repaint();
		}
	}

	public static void main(String[] args) {
		MyClientSocket clientSocket = new MyClientSocket();

	}
}

package chatting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JTextField;

/**
 * 
 * @author 정성현 목적 : 프레임 생성
 *
 */

public class MyClientSocket extends JFrame {

	// 프레임 구성을 위한 변수
	private JPanel top;
	private JPanel center;
	private JPanel bottom;
	private JTextField textBox;
	private JButton send;
	private String msg;
	private JPanel msgBox;

	// 소켓을 위한 변수
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private boolean isLogin = true;
	private String userName;

	// 아이디 입력 확인 변수
	private boolean isId = false;

	public MyClientSocket() {
		initObject();
		initSetting();
		addObject();
		initListener();
		connect();
		setVisible(true);
	}

	private void initObject() {

		top = new JPanel();
		center = new JPanel();
		bottom = new JPanel();
		textBox = new JTextField(30);
		send = new JButton("전송");
		msgBox = new JPanel();

	}

	private void initSetting() {
		setTitle("채팅프로그램");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		top.setBackground(Color.yellow);
		top.setPreferredSize(new Dimension(400, 30));

		center.setBackground(Color.pink);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		bottom.setBackground(Color.orange);
		bottom.setLayout(new FlowLayout());
		bottom.setPreferredSize(new Dimension(400, 40));

	}

	private void addObject() {

		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		bottom.add(textBox);
		bottom.add(send);

	}

	private void initListener() {
		textBox.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msg = textBox.getText();
					textBox.setText("");
					write();

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

			if (socket != null) {
				read();

			}
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
					center.add(msgBox.add(new JLabel(inputData)));
					center.revalidate();
					center.repaint();
				}
			} catch (Exception e) {
				try {
					System.out.println("연결해제됨 : " + e.getMessage());
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

	private void write() {
		try {
			if (!isId) {
				userName = msg;
				writer.write(userName + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel("ID가 전송되었습니다.")));
				center.add(msgBox.add(new JLabel("ID : " + userName)));
				center.revalidate();
				center.repaint();
				isId = true;
			} else if (isId) {

				writer.write(msg + "\n");
				writer.flush();
				center.add(msgBox.add(new JLabel(msg)));
				center.revalidate();
				center.repaint();
			}
		} catch (

		Exception e) {
			System.out.println("연결이 없습니다.");
		}
	}

	public static void main(String[] args) {
		MyClientSocket clientSocket = new MyClientSocket();

	}
}

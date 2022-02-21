package chatting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

// jwp = 재원프로토콜
// 1. 최초 메세지는 username으로 체킹
// 2.구분자 :
// 3.ALL:메시지
// 4.CHAT:아이디:메시지

public class MyServerSocket {

	// 리스너(연결받기) - 메인스레드
	ServerSocket serverSocket;

	// 유저와 관련된 정보 저장하기 위한 리스트
	List<고객전담스레드> 고객리스트;

	// 메시지 받아서 보내기 (클라이언트 수마다 만들기)

	public MyServerSocket() {
		try {
			// 2000번 포트로 서버소켓 생성
			serverSocket = new ServerSocket(2001);

			// 리스트에 동시접근 불가능한 동기화가 처리된 ArrayList인 Vector사용
			고객리스트 = new Vector<>();

			while (true) {
				// 포트 연결시 소켓 생성
				Socket socket = serverSocket.accept();
				System.out.println("유저 연결됨");

				// 소켓 생성 후 리스트에 담기
				고객전담스레드 t = new 고객전담스레드(socket);
				고객리스트.add(t);
				System.out.println("들어온 유저 수 : " + 고객리스트.size());

				// 새로운 소켓을 위한스레드
				new Thread(t).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class 고객전담스레드 implements Runnable {

		// 익명클래스로 만들면 관리가 불가능해서 내부클래스로 생성->상태의 공유 가능
		Socket socket;
		BufferedReader reader;
		BufferedWriter writer;

		// 세션연결 및 while문을 위한 변수
		boolean isLogin = true;

		// 유저구분 위한 ID 변수
		String userName;

		public 고객전담스레드(Socket socket) {
			this.socket = socket;

			// 버퍼 만들기
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 전체채팅 메서드 프로토콜 (All:메시지)
		public void chatPublic(String msg) {
			try {
				for (고객전담스레드 t : 고객리스트) {
					if (t != this) {
						t.writer.write(userName + ":" + msg + "\n");
						t.writer.flush();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 귓속말 메서드 프로토콜 (CHAT:ID:메시지)
		public void chatPrivate(String receiver, String msg) {
			try {
				for (고객전담스레드 t : 고객리스트) {
					if (t.userName.equals(receiver)) {
						t.writer.write("[귓속말]" + userName + ":" + msg + "\n");
						t.writer.flush();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void sendUserList() {
			try {
				String userList = "USER";
				for (고객전담스레드 t : 고객리스트) {
					userList = userList + ":" + t.userName;
				}
				this.writer.write(userList + "\n");
				this.writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 재원프로토콜 검사기
		// ALL:안녕
		// CHAT:ID:안녕
		public void jwp(String inputData) {
			// 1. 프로토콜 분리
			String[] token = inputData.split(":");
			String protocol = token[0];
			if (protocol.equals("ALL")) {
				String msg = token[1];
				chatPublic(msg);

			} else if (protocol.equals("CHAT")) {
				String receiver = token[1];
				String msg = token[2];
				chatPrivate(receiver, msg);

			} else if (protocol.equals("USER")) {
				sendUserList();
			} else { // 프로토콜 통과 못함
				System.out.println("프로토콜 없음");
			}
		}

		@Override
		public void run() {
			// 최초 메시지는 userName이다.
			try {
				userName = reader.readLine();
				for (고객전담스레드 t : 고객리스트) {
					t.writer.write(userName + "님이 입장하였습니다.\n");
					t.writer.flush();
				}
			} catch (Exception e1) {
				// e1.printStackTrace();
				isLogin = false; // ID없으면 세션 형성 안됨
				System.out.println("ID를 받지 못했습니다.");
			}

			while (isLogin) {
				try {
					String inputData = reader.readLine();
					// System.out.println("from 클라이언트 : " + inputData);

					// 프로토콜 검사기
					if (inputData != null) {
						System.out.println("데이터" + inputData);
						jwp(inputData);
					}
				} catch (Exception e) {
					// e.printStackTrace();
					System.out.println("오류내용 : " + e.getMessage());
					e.printStackTrace();
					try {
						System.out.println("클라이언트 연결 해제 중");
						isLogin = false; // while문 종료
						고객리스트.remove(this); // 리스트에 담겨있는 소켓을 날려줘야한다.
						writer.close(); // GarbageCollection이 일어나려면
						reader.close();// 시간이 걸리는데 통신(IO가 생기기 때문)의 부하가
						socket.close();// GarbageCollection의 부하가 더 크기 때문에 Garbage Collection을 직접 해준다.
					} catch (Exception f) {
						// f.printStackTrace();
						System.out.println("연결 해제 실패 : " + f.getMessage());
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		new MyServerSocket();
	}
}
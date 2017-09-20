import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

//클라이언트와 서버간 소통을 위한 기능
public class CommunicatorUTF implements Communicator {
	/**********************************
	 * socket_ - 클라이언트 소켓
	**********************************/
	private Socket socket_ = null;

	/**********************************
	 * CommunicatorUTF(Socket socket) - 각 변수들을 초기화
	 * socket - 클라이언트 소켓 정보
	**********************************/
	public CommunicatorUTF(Socket socket) {
		this.socket_ = socket;
	}

	/**********************************
	 * receiveRequest() - 클라이언트로 부터 데이터를 받음
	**********************************/
	public String receiveRequest() throws Exception{
		ServerLog.getInstance().log(this.getClass().getName(), "client로부터 데이타를 읽습니다.");
		String msg = new DataInputStream(this.socket_.getInputStream()).readUTF();
		ServerLog.getInstance().log(this.getClass().getName(), "데이타: " + msg);
		return msg;
	}

	/**********************************
	 * sendResponse(String msg) - 클라이언트로 데이터를 보냄
	 * msg - 보낼 데이터
	**********************************/
	public void sendResponse(String msg) throws Exception {
		ServerLog.getInstance().log(this.getClass().getName(), "client 로 데이타를 전송합니다.");		
		new DataOutputStream(this.socket_.getOutputStream()).writeUTF(msg);
	}

	/**********************************
	 * stop() - 클라이언트와 연결 종료
	**********************************/
	public void stop() {
		ServerLog.getInstance().log(this.getClass().getName(),"stop 명령을 수행합니다.");		
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"만약에 소켓이 열려 있다면 닫습니다.");			
			if(this.socket_ != null) this.socket_.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			ServerLog.getInstance().log(this.getClass().getName(),"소켓 Closed");
		}
	}

	/**********************************
	 * getSocket() - 클라이언트 소켓을 반환
	**********************************/
	public Socket getSocket() {
		return socket_;
	}
}

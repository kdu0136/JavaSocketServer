import java.net.Socket;

//클라이언트와 서버간 소통을 위한 기능
public interface Communicator {
	//클라이언트로 부터 데이터를 받음
	public String receiveRequest() throws Exception;
	//클라이언트로 데이터를 보냄
	public void sendResponse(String msg) throws Exception;
	//클라이언트와 연결 종료
	public void stop();
	
	public Socket getSocket();
}

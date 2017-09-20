import java.util.HashMap;

//기본 채팅 명령 실행자 - 상대방이 잘 받는지 채크할 필요 있음
public class CommandExecutorPingPong implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 채팅 내용
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			//ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			//ServerLog.getInstance().log(this.getClass().getName(),"받은 응답: " + request);
			
			clientHandler.pingPongResult = true;			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}
	/**********************************
	 * decoding(String msg) - json형식 명령 디테일을 풀어주는 기능
	 * msg - json형식의 명령 디테일
	**********************************/
	public HashMap<String, Object> decoding(String request) throws Exception {
		return null;
	}
}

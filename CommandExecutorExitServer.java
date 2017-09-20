import java.util.HashMap;

//서버 접속 종료 명령 샐행자
public class CommandExecutorExitServer implements CommandExecutor {
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(), clientHandler.account_.id_ + " 명령을 수행합니다.");
		
			clientHandler.isAlive = false;

	        String[] keys = {"result"}; 
	        Object[] values = {"success"};//서버 접속 종료 결과
	        String jsonStr = JsonEncode.getInstance().encodeCommandJson("exitServerResult", keys, values); 
	        clientHandler.sendResponse(jsonStr); //방 생성을 요청한 클라이언트에게 결과를 보냄
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}

	@Override
	public HashMap<String, Object> decoding(String request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

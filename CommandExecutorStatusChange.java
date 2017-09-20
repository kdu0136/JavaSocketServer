import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//기본 채팅 명령 실행자 - 상대방이 잘 받는지 채크할 필요 있음
public class CommandExecutorStatusChange implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 채팅 내용
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			 			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String status = (String)map.get("status"); //계정 상태
			
			clientHandler.account_.status_ = status;			
			ServerLog.getInstance().log(this.getClass().getName(), 
					clientHandler.account_.id_ + "의 소켓 연결 방식은 [" + clientHandler.account_.status_ + "]입니다.");
			
			if(!status.equals("service")){ //클라이언트 소캣 연결이 서비스로 이루어 진 것이 아닐 경우
				RoomManager roomManager = RoomManager.getInstance();
				WaitingRoom waitingRoom = (WaitingRoom)roomManager.getRoom(RoomManager.waitingRoomKey); //waitingRoom 정보 가져오기 
				waitingRoom.enterClient(clientHandler); //접속한 클라이언트 대기실로 입장
			}
			
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
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObj = (JSONObject)parser.parse(request); 
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("status", jsonObj.get("status")); //채팅 내용
		
		return map;
	}
}

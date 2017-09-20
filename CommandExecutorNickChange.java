import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//기본 채팅 명령 실행자 - 상대방이 잘 받는지 채크할 필요 있음
public class CommandExecutorNickChange implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 채팅 내용
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			 			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String nick = (String)map.get("nick"); //새로운 닉네임
			
			clientHandler.account_.nick_ = nick;			
			ServerLog.getInstance().log(this.getClass().getName(), 
					clientHandler.account_.id_ + "의 새로운 닉네임은 [" + clientHandler.account_.nick_ + "]입니다.");
			
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
		map.put("nick", jsonObj.get("nick")); //새로운 닉네임
		
		return map;
	}
}

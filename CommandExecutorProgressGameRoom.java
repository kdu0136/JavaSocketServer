import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//게임 진행 상황 프로그래스 바 전달
public class CommandExecutorProgressGameRoom implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try
		{
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			clientHandler.gameRoom_.gameTime_ = (long)map.get("progressBar");

//			ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //현재 입장한 방 client 목록									
//			Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
//			while(iterator.hasNext()) {
//				String id = (String)iterator.next();
//				ClientHandler handler = chm.getClientHandler(id);
//				if(handler.account_.id_.equals((String)map.get("id"))){ //프로그래스 게이지 받을 유저를 찾음
//			        String[] keys = {"progressBar"}; 
//			        Object[] values = {map.get("progressBar")};//프로그래스 게이지
//			        String jsonStr = JsonEncode.getInstance().encodeCommandJson("progressGameRoom", keys, values); 
//					ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 progressGameRoom 명령을 수행합니다.");
//					handler.sendResponse(jsonStr);		
//					break;
//				}
//			}					
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}		
	}

	/**********************************
	 * decoding(String msg) - json형식 명령 디테일을 풀어주는 기능
	 * msg - json형식의 명령 디테일
	**********************************/
	@Override
	public HashMap<String, Object> decoding(String request) throws Exception {
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObj = (JSONObject)parser.parse(request); 
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("progressBar", jsonObj.get("progressBar")); //프로그래스 바 값
		
		return map;
	}
}

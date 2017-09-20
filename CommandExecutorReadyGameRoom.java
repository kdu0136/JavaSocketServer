import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//방 입장 명령 실행자
public class CommandExecutorReadyGameRoom implements CommandExecutor {	
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
			boolean ready = (boolean)map.get("ready"); //레디 상태

			clientHandler.account_.isReady_ = ready;
			
			//방 인원 전부가 레디를 했을 경우
			if(clientHandler.gameRoom_.isAllClientReady()){
		        String[] keys = {"allReady"};
		        Object[] values = {true};
		        String jsonStr = JsonEncode.getInstance().encodeCommandJson("allReadyGameRoom", keys, values); //방에 있는 방장에게 유저 레디 상태
				ServerLog.getInstance().log(this.getClass().getName(), clientHandler.gameRoom_.hostClientHandler.account_.id_ + " 에게 allReadyGameRoom 명령을 수행합니다.");
				clientHandler.gameRoom_.hostClientHandler.sendResponse(jsonStr);						
			}else{
		        String[] keys = {"allReady"};
		        Object[] values = {false};
		        String jsonStr = JsonEncode.getInstance().encodeCommandJson("allReadyGameRoom", keys, values); //방에 있는 방장에게 유저 레디 상태
				ServerLog.getInstance().log(this.getClass().getName(), clientHandler.gameRoom_.hostClientHandler.account_.id_ + " 에게 allReadyGameRoom 명령을 수행합니다.");
				clientHandler.gameRoom_.hostClientHandler.sendResponse(jsonStr);										
			}
			
	        String[] keys = {"id", "ready"};
	        Object[] values = {clientHandler.account_.id_, ready};
	        String jsonStr = JsonEncode.getInstance().encodeCommandJson("readyGameRoom", keys, values); //방에 있는 클라이언트에게 유저 레디 상태
			
			ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //현재 입장한 방 client 목록									
			Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
			while(iterator.hasNext()) {
				String id = (String)iterator.next();
				ClientHandler handler = chm.getClientHandler(id);
				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 readyGameRoom 명령을 수행합니다.");
				handler.sendResponse(jsonStr);		
			}					
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
		map.put("ready", jsonObj.get("ready")); //ready 상태
		
		return map;
	}
}

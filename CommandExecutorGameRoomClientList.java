import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//방에 접속한 클라이언트 목록 보여주는 명령 실행자
public class CommandExecutorGameRoomClientList implements CommandExecutor {
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_;

			JSONArray jsonArr = new JSONArray(); 
			//방 리스트에 있는 유저 정보 수집
			Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
			while(iterator.hasNext()) {
				String id = (String)iterator.next();
				if(!clientHandler.account_.id_.equals(id)){ //본인은 제외
					ClientHandler client = chm.getClientHandler(id);
					JSONObject accountJson = new JSONObject(); 
					accountJson.put("id", client.account_.id_); //유저 아이디
					accountJson.put("nick", client.account_.nick_); //유저 닉네임
					accountJson.put("ready", client.account_.isReady_); //유저 레디 상태
					
					jsonArr.add(accountJson); //방 정보를 JsonArray에 추가					
				}
			}
			String jsonStr = JsonEncode.getInstance().encodeCommandJson("clientListResult", jsonArr); //클라이언트에게 넘겨줄 게임 방 정보
			
			clientHandler.sendResponse(jsonStr);
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

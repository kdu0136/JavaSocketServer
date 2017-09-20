import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//방 목록을 보여주는 명령 실행자
public class CommandExecutorGameRoomList implements CommandExecutor {
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			RoomManager roomManager = RoomManager.getInstance();

			JSONArray jsonArr = new JSONArray(); 
			//방 리스트에 있는 방 정보 수집
			Iterator<String> iterator = roomManager.roomList_.keySet().iterator();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				try{ //roomManager에서 받은 방의 유형이 GameRoom인 경우
					GameRoom gameRoom = (GameRoom)roomManager.getRoom(key);
					JSONObject roomJson = new JSONObject(); 
					roomJson.put("key", gameRoom.key_); //방 키값
					roomJson.put("name", gameRoom.name_); //방 이름
					roomJson.put("isLock", gameRoom.isLock_); //방 잠금 유무
					roomJson.put("num", gameRoom.clientHandlerManager_.clientHandlers_.size()); //방 현재 인원
					roomJson.put("maxNum", gameRoom.maxNum_); //방 최대 인원
					roomJson.put("isStart", gameRoom.isStart_); //방 상태
					
					jsonArr.add(roomJson); //방 정보를 JsonArray에 추가						
				}catch(ClassCastException e){//roomManager에서 받은 방의 유형이 ChatRoom / WatingRoom인 경우
					
				}
			}
			String jsonStr = JsonEncode.getInstance().encodeCommandJson("gameRoomListResult", jsonArr); //클라이언트에게 넘겨줄 게임 방 정보
			
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

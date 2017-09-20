import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//강제 퇴장 명령 실행자
public class CommandExecutorKickClientRoom implements CommandExecutor {
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");

			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String kickId = (String)map.get("id"); //강퇴당할 유저 아이디

			ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //방 client 목록
			ClientHandler kickClientHandler = chm.getClientHandler(kickId); //강제 퇴장 client
						
			//강제 퇴장 client 못 찾은 경우
			if(kickClientHandler == null){ 
				return;
			}	

			//방에 남아 있는 유저들에게 퇴장 메세지 전달
            String[] keys = {"id","nick"};
            Object[] values = {kickClientHandler.account_.id_, kickClientHandler.account_.nick_}; //강퇴 당한 유저 닉네임
            String jsonStr = JsonEncode.getInstance().encodeCommandJson("noticeKick", keys, values); //방에 있는 클라이언트들에게 보낼 메세지을 json으로 변환
			Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
			while(iterator.hasNext()) {
				String id = (String)iterator.next();
				ClientHandler handler = chm.getClientHandler(id);
				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 noticeKick 명령을 수행합니다.");
				handler.sendResponse(jsonStr);	
			}				

			clientHandler.gameRoom_.exitClient(kickClientHandler); //현재 방에서 강제 퇴장
			kickClientHandler.gameRoom_ = null; //게임방 비우기

			RoomManager roomManager = RoomManager.getInstance(); //RoomManager 참조
			Room waitingRoom = roomManager.getRoom(RoomManager.waitingRoomKey); //waitingRoom 정보 가져오기
			waitingRoom.enterClient(kickClientHandler); //waitingRoom으로 강퇴당한 클라이언트 입장

//			//강퇴당한 유저에게 강제 퇴장 메세지 전달
//            String[] keys2 = {"nick"};
//            Object[] values2 = {kickClientHandler.account_.nick_}; //강퇴 당한 유저 닉네임
//            String jsonStr = JsonEncode.getInstance().encodeCommandJson("noticeKick", keys2, values2); //방에 있는 클라이언트들에게 보낼 메세지을 json으로 변환
//			sendCommand = clientHandler.room_.name_ + "방에서 강제 퇴장 당하였습니다.";
//			kickClientHandler.sendResponse(sendCommand);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}

	@Override
	public HashMap<String, Object> decoding(String request) throws Exception {
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObj = (JSONObject)parser.parse(request); 
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", jsonObj.get("id")); //강퇴 당할 유저 아이디
		
		return map;
	}
}

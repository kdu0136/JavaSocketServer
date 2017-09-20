import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//방 입장 명령 실행자
public class CommandExecutorEnterGameRoom implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		String result = "success";
		try
		{
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String roomKey = (String)map.get("key"); //입장 할 방 키값
			String roomPassword = (String)map.get("password"); //입장 할 방 비밀번호

			RoomManager roomManager = RoomManager.getInstance(); //RoomManager 참조
			GameRoom room = (GameRoom)roomManager.getRoom(roomKey); //입장 할 게임 방
			
			if(room == null){ //없는 방
				result = "wrongKey";
			}else{
				if(room.maxNum_ <= room.clientHandlerManager_.clientHandlers_.size()){ //방 인원 가득 참
					result = "fullRoom";					
				}else{
					if(room.isLock_ && !room.password_.equals(roomPassword)){ //비밀방 경우 비밀번호 체크
						result = "wrongPassword";
					}else{ //방 입장
						WaitingRoom waitingRoom = (WaitingRoom)roomManager.getRoom(RoomManager.waitingRoomKey); //waitingRoom 정보 가져오기 
						waitingRoom.exitClient(clientHandler); //대기실에서 퇴장		
						
						clientHandler.gameRoom_ = room; //게임 방 변경
						clientHandler.gameRoom_.enterClient(clientHandler); //바뀐 게임 방으로 입장
						clientHandler.account_.isReady_ = false; //레디 해제 상태

				        String[] keys = {"id", "nick"};
				        Object[] values = {clientHandler.account_.id_, clientHandler.account_.nick_};
				        String jsonStr = JsonEncode.getInstance().encodeCommandJson("noticeEnter", keys, values); //방에 있는 클라이언트에게 유저 입장을 알림
						
						ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //현재 입장한 방 client 목록									
						Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
						while(iterator.hasNext()) {
							String id = (String)iterator.next();
							ClientHandler handler = chm.getClientHandler(id);
							if(handler != clientHandler){
			    				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 noticeEnter 명령을 수행합니다.");
								handler.sendResponse(jsonStr);		
							}
						}

				        String[] keys1 = {"allReady"};
				        Object[] values1 = {false};
				        String jsonStr1 = JsonEncode.getInstance().encodeCommandJson("allReadyGameRoom", keys1, values1); //방에 있는 방장에게 유저 레디 상태
						ServerLog.getInstance().log(this.getClass().getName(), clientHandler.gameRoom_.hostClientHandler.account_.id_ + " 에게 allReadyGameRoom 명령을 수행합니다.");
						clientHandler.gameRoom_.hostClientHandler.sendResponse(jsonStr1);					
					}
				}
			}						
		} catch (Exception e)
		{
			e.printStackTrace();
			result = "fail";
		}
		
		long maxNum = 0;
		String name ="";
		String key ="";
		String host ="";
		boolean isGameStart = false;
		String quizHost ="";
		long gameTime=0;
		if(clientHandler.gameRoom_ != null){
			maxNum = clientHandler.gameRoom_.maxNum_;
			name = clientHandler.gameRoom_.name_;
			key = clientHandler.gameRoom_.key_;
			host = clientHandler.gameRoom_.hostClientHandler.account_.nick_;
			isGameStart = clientHandler.gameRoom_.isStart_;
			quizHost = clientHandler.gameRoom_.quizClientHandler.account_.nick_;
			gameTime = clientHandler.gameRoom_.gameTime_;
		}
        String[] keys = {"result", "maxNum", "name", "key", "host", "isGameStart", "quizHost", "gameTime"};
        Object[] values = {result, maxNum, name, key, host, isGameStart, quizHost, gameTime};
        String jsonStr = JsonEncode.getInstance().encodeCommandJson("enterGameRoomResult", keys, values); //방 입장을 요청한 클라이언트에게 결과를 보냄
        clientHandler.sendResponse(jsonStr);
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
		map.put("key", jsonObj.get("key")); //입장 하고자 하는 방 키값
		map.put("password", jsonObj.get("password")); //입장 하고자 하는 방 비밀번호
		
		return map;
	}
}

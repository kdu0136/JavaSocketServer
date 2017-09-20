import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//대시길에서 채팅 방을 만드는 명령 실행자
public class CommandExecutorCreateGameRoom implements CommandExecutor {		
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		String result = "success";
		RoomManager roomManager = RoomManager.getInstance(); //RoomManager 참조
		GameRoom gameRoom = null;
		try
		{
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String key = (String)map.get("key"); //방 키값
			String name = (String)map.get("name"); //방 제목
			Boolean isLock = (Boolean)map.get("isLock"); //방 잠금 유무
			String password = (String)map.get("password"); //방 비밀번호
			Long maxNum = (Long)map.get("maxNum"); //방 최대 인원
						
			gameRoom = new GameRoom(key, name, isLock, password, maxNum); //방 생성
			roomManager.addRoom(gameRoom); //RoomManager에 방 추가
			
			WaitingRoom waitingRoom = (WaitingRoom)roomManager.getRoom(RoomManager.waitingRoomKey); //waitingRoom 정보 가져오기 
			waitingRoom.exitClient(clientHandler); //대기실에서 퇴장			

			clientHandler.gameRoom_ = gameRoom; //방 변경			
			clientHandler.gameRoom_.enterClient(clientHandler); //게임 방으로 입장
			clientHandler.gameRoom_.setHostClient(clientHandler); //방장으로 위임		
			clientHandler.gameRoom_.quizClientHandler = clientHandler.gameRoom_.hostClientHandler; //퀴즈 출제자 = 방장 으로 시작
		} catch (Exception e) {
			e.printStackTrace();
			result = "fail";
		} 
		
        String[] keys = {"result", "maxNum", "name", "key"}; 
        Object[] values = {result, gameRoom.maxNum_, gameRoom.name_, gameRoom.key_};//방 생성 결과
        String jsonStr = JsonEncode.getInstance().encodeCommandJson("createGameRoomResult", keys, values); 
        clientHandler.sendResponse(jsonStr); //방 생성을 요청한 클라이언트에게 결과를 보냄
        
		if(result.equals("success")){ //방 생성 성공하면 대기실  클라이언트에게 새로운 방 정보를 보냄
            String[] keys2 = {"key", "name", "isLock", "num", "maxNum", "isStart"};
            Object[] values2 = {gameRoom.key_, gameRoom.name_, gameRoom.isLock_, 1, gameRoom.maxNum_, gameRoom.isStart_}; //생성한 방 정보
            String jsonStr2 = JsonEncode.getInstance().encodeCommandJson("createNewGameRoomInfo", keys2, values2);
				          
    		ClientHandlerManager chm = roomManager.getRoom(RoomManager.waitingRoomKey).clientHandlerManager_; //대기실 클라이언트 정보
    		Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
    		while(iterator.hasNext()) { //대기실에 있는 클라이언트에게 생성 방 정보 전송
    			String id = (String)iterator.next();
    			ClientHandler handler = chm.getClientHandler(id);
    			if(handler != clientHandler){
    				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 createNewGameRoomInfo 명령을 수행합니다.");
    				handler.sendResponse(jsonStr2);		
    			}
    		}        	
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
		map.put("key", jsonObj.get("key")); //방 키값
		map.put("name", jsonObj.get("name")); //방 제목
		map.put("isLock", jsonObj.get("isLock")); //방 잠금 유무
		map.put("password", jsonObj.get("password")); //방 비밀번호
		map.put("maxNum", jsonObj.get("maxNum")); //방 최대 인원
		
		return map;
	}
}

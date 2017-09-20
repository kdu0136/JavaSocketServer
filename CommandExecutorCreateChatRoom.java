import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//대시길에서 방을 만드는 명령 실행자
public class CommandExecutorCreateChatRoom implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		String result = "success";
		ClientHandlerManager clientHandlerManager = ClientHandlerManager.getInstance();
		RoomManager roomManager = RoomManager.getInstance(); //RoomManager 참조
		ChatRoom chatRoom = null;
		try
		{
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");		
						
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String roomKey = (String)map.get("key"); //생성 할 방 키값
			String roomName = (String)map.get("name"); //생성 할 방 이름
			Long num = (Long)map.get("num"); //방에 들어와 있는 유저 수
			
			chatRoom = (ChatRoom)roomManager.getRoom(roomKey); //해당 키값을 가진 채팅방 있는지 확인
			if(chatRoom == null) { //해당 키값을 가진 방이 서버 메모리에 없으면 방 생성
				ServerLog.getInstance().log(this.getClass().getName(), roomKey + " 키값의 채팅방을 생성 합니다.");		
				chatRoom = new ChatRoom(roomKey); //방 생성
				chatRoom.enterClient(clientHandler); //채팅 방으로 입장 (본인)
				for(int i = 1; i < num; i++){
					String userId = (String)map.get("id" + i); //방에 들어와 있는 유저 아이디
					
					ClientHandler ch = clientHandlerManager.getClientHandler(userId); //전체 클라이언트 매니저에서 id를 이용하여 해당 클라이언트를 찾아내기
					if(ch == null){	 //해당 클라이언트가 서버 메모리에 없으면 임시로 클라이언트 생성해서 메모리에 올려놓음
						ch = new ClientHandler(userId);	
						clientHandlerManager.addClientHandler(ch); //전체 클라이언트 매니저에 임시 클라이언트 추가		
					}
					chatRoom.enterClient(ch); //채팅 방으로 입장 (임시 클라이언트)
				}
				roomManager.addRoom(chatRoom); //RoomManager에 채팅 방 추가		
				
				//Redis에 방 정보 저장
				JedisHandler jedisHandler = new JedisHandler();
				jedisHandler.jedisHset("ChatRoom", roomKey, roomName);
			}else{
				ServerLog.getInstance().log(this.getClass().getName(), roomKey + " 키값의 채팅방은 존재 합니다.");						
			}
			chatRoom.name_ = roomName;
		} catch (Exception e) {
			e.printStackTrace();
			result = "fail";
		} 
        String[] keys = {"result", "key", "name"}; 
        Object[] values = {result, chatRoom.key_, chatRoom.name_};//방 생성 결과, 키값, 방 이름(유저들 닉네임)
        String jsonStr = JsonEncode.getInstance().encodeCommandJson("createChatRoomResult", keys, values); 
        clientHandler.sendResponse(jsonStr); //방 생성을 요청한 클라이언트에게 결과를 보냄
	}

	/**********************************
	 * decoding(String msg) - json형식 명령 디테일을 풀어주는 기능
	 * msg - json형식의 명령 디테일
	**********************************/
	public HashMap<String, Object> decoding(String request) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		JSONParser parser = new JSONParser(); 
		JSONArray jsonArray = (JSONArray)parser.parse(request); 
		JSONObject objectKey = (JSONObject) jsonArray.get(0);		
		map.put("key", objectKey.get("key")); //생성 할 방 키값
		map.put("name", objectKey.get("name")); //생성 할 방 키 이름
		map.put("num", (long)jsonArray.size()); //방에 들어와 있는 유저 수 

		for(int i = 1; i < jsonArray.size(); i++){
			JSONObject object = (JSONObject) jsonArray.get(i);
			map.put("id" + i, object.get("id")); //방에 들어와 있는 유저 아이디
		}
		
		return map;
	}
}

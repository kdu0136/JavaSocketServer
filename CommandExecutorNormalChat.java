import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//기본 채팅 명령 실행자 - 상대방이 잘 받는지 채크할 필요 있음
public class CommandExecutorNormalChat implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 채팅 내용
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String roomKey = (String)map.get("roomKey"); //채팅 보낸 방의 키값
			String userId = (String)map.get("id"); //채팅 보낸 유저 아이디
			String userNick = (String)map.get("nick"); //채팅 보낸 유저 닉네임
			String chatMsg = (String)map.get("msg"); //채팅 내용

			//서버가 채팅을 받은 시간			
            RoomManager roomManager = RoomManager.getInstance();
			ChatRoom chatRoom = (ChatRoom)roomManager.getRoom(roomKey); //해당 키값 방 가져오기
			if(chatRoom == null){ //서버 메모리에 채팅 방이 없으면 레디스에서 방 정보 가져와서 방 생성
				JedisHandler jedisHandler = new JedisHandler();
				String roomNick = jedisHandler.jedisHget("ChatRoom", roomKey);
				ServerLog.getInstance().log(this.getClass().getName(), roomKey + " 키값의 채팅방을 생성 합니다.");		
				chatRoom = new ChatRoom(roomKey); //방 생성
				chatRoom.name_ = roomNick;

				ClientHandlerManager clientHandlerManager = ClientHandlerManager.getInstance(); //서버 전체 클라이언트를 관리하는 매니저
				String[] idArray = roomKey.split(","); // ','를 기준으로 아이디 값만 받아오기
				for(int i = 0; i < idArray.length; i++){				
					ClientHandler ch = clientHandlerManager.getClientHandler(idArray[i]); //전체 클라이언트 매니저에서 id를 이용하여 해당 클라이언트를 찾아내기
					if(ch == null){	 //해당 클라이언트가 서버 메모리에 없으면 임시로 클라이언트 생성해서 메모리에 올려놓음
						ch = new ClientHandler(idArray[i]);	
						clientHandlerManager.addClientHandler(ch); //전체 클라이언트 매니저에 임시 클라이언트 추가		
					}
					chatRoom.enterClient(ch); //채팅 방으로 입장 (임시 클라이언트)
				}
				roomManager.addRoom(chatRoom); //RoomManager에 채팅 방 추가	
			}
			ClientHandlerManager chm = chatRoom.clientHandlerManager_;

			long now = System.currentTimeMillis();

            String[] keys = {"roomKey", "id", "nick", "msg", "date", "num", "name", "type"};
            Object[] values = {roomKey, userId, userNick, chatMsg, now, (chm.clientHandlers_.size() - 1), chatRoom.name_, (String)map.get("type")}; //보낼 채팅 읽음 수 = 해당 방 인원 - 자기자신
            String jsonStr = JsonEncode.getInstance().encodeCommandJson("normalChat", keys, values); //방에 있는 클라이언트들에게 보낼 채팅을 json으로 변환
            
			Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
			while(iterator.hasNext()) {
				String id = (String)iterator.next();
				ClientHandler handler = chm.getClientHandler(id);
				if(handler.isAlive && handler.communicator_ != null){ //클라이언트가 서버와 연결이 되었을 경우
					if(!handler.communicator_.getSocket().isClosed()){
						ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 normalChat 명령을 수행합니다.");
						handler.sendResponse(jsonStr);								
					}else{//클라이언트가 서버와 연결이 되어있지 않을 경우
						ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + "는 서버와 연결이 되어 있지 않은 클라이언트 입니다.");	
						ServerLog.getInstance().log(this.getClass().getName(), "저장 명령: " + jsonStr);
						
						JedisHandler jedisHandler = new JedisHandler();
						String hashesKey = handler.account_.id_ + "MissMsg";
						long fieldNum = jedisHandler.jedisHlen(hashesKey); //해당 유저 hashes에 저장할 fieldNum 받아오기
						jedisHandler.jedisHset(hashesKey, String.valueOf(fieldNum), jsonStr);
					}
				} else{ //클라이언트가 서버와 연결이 되어있지 않을 경우			
					ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + "는 서버와 연결이 되어 있지 않은 클라이언트 입니다.");	
					ServerLog.getInstance().log(this.getClass().getName(), "저장 명령: " + jsonStr);
					
					JedisHandler jedisHandler = new JedisHandler();
					String hashesKey = handler.account_.id_ + "MissMsg";
					long fieldNum = jedisHandler.jedisHlen(hashesKey); //해당 유저 hashes에 저장할 fieldNum 받아오기
					jedisHandler.jedisHset(hashesKey, String.valueOf(fieldNum), jsonStr);
				}	
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
		map.put("roomKey", jsonObj.get("roomKey")); //채팅 보낸 방의 키값
		map.put("id", jsonObj.get("id")); //채팅 보낸 유저 아이디
		map.put("nick", jsonObj.get("nick")); //채팅 보낸 유저 닉네임
		map.put("msg", jsonObj.get("msg")); //채팅 내용
		map.put("type", jsonObj.get("type")); //타입  ex)chat / file
		
		return map;
	}

}

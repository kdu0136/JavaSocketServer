import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

//방을 관리하는 메니저
public class RoomManager {
	/**********************************
	 * roomManager_ - 룸 매니저 객체 전역 변수
	 * roomList_ - 방 목록  (키값은 방 고유 번호)
	**********************************/
	private static RoomManager roomManager_;
	public static String waitingRoomKey = "waitingRoom";
	public HashMap<String, Room> roomList_;

	/**********************************
	 * RoomManager() - 각 변수들을 초기화
	**********************************/
	private RoomManager(){
		this.roomList_ = new HashMap<String, Room>();
	}


	/**********************************
	 * getInstance() - 클라이언트 매니저 객체를 생성하고 반환하는 함수
	**********************************/
	public static RoomManager getInstance() {
		//roomManager_가 null값이면 초기화
		if(roomManager_ == null)
			roomManager_ = new RoomManager();
		return roomManager_;
	}

	/**********************************
	 * addRoom(Room room) - 룸 메니저에 방 추가
	 * room - 추가 할 방
	**********************************/
	public void addRoom(Room room){
		this.roomList_.put(room.key_, room);
		ServerLog.getInstance().log(this.getClass().getName(), room.name_ + "생성 (키값: " + room.key_ + ")");		
	}

	/**********************************
	 * removeRoom(String key) - 룸 메니저에 방 제거
	 * key - 제거 할 방 키값
	**********************************/
	public void removeRoom(String key){
		this.roomList_.remove(key);
		ServerLog.getInstance().log(this.getClass().getName(), "키값: " + key + " 방 제거");	
	}

	/**********************************
	 * getRoom(String key) - 룸 메니저에 해당 방 정보 가져오기
	 * key - 가져 올 방 키값
	**********************************/
	public Room getRoom(String key){
		Room room = this.roomList_.get(key);
		return room;
	}

	/**********************************
	 * isAliveRoom(String key) - 해당 키 방에 인원이 1명 이상인 방인지 확인
	**********************************/
	public boolean isAliveRoom(String key){
		Room room = this.roomList_.get(key);
		int numClient = room.clientHandlerManager_.clientHandlers_.size(); //방의 현재 인원
		
		if(room.key_.equals(RoomManager.roomManager_)) //대기실인 경우 방 인원이 상관없음
			return true;

		if(numClient > 0){ //방에 인원이 존재
			return true;			
		}else{ //방에 인원이 0명
			return false;
		}
	}


	/**********************************
	 * loadChatRoom() - Redis에서 채팅방 불러오기
	**********************************/
	public void loadChatRoom(){
		JedisHandler jedisHandler = new JedisHandler();
		Set<String> fieldSet = jedisHandler.jedisHkeys("ChatRoom"); //Key가 ChatRoom인 Hashes가 가지고 있는 모든 field값 가져오기
				
		//저장되어 있는 방이 있으면 진행
		if(fieldSet != null){
			ArrayList<Object> roomNameList = jedisHandler.jedisHget("ChatRoom", fieldSet.toArray()); //roomKey로 roomName가져오기
			
			//받은 정보로 채팅방 생성하기
			Iterator<String> keyIterator = fieldSet.iterator();
			int index = 0; //roomNameList를 위한 index
			ClientHandlerManager clientHandlerManager = ClientHandlerManager.getInstance(); //서버 전체 클라이언트를 관리하는 매니저
			while(keyIterator.hasNext()){
				String roomKey = keyIterator.next();
				String roomNick = (String)roomNameList.get(index++);
				ServerLog.getInstance().log(this.getClass().getName(), roomKey + " 키값의 채팅방을 생성 합니다.");		
				ChatRoom chatRoom = new ChatRoom(roomKey); //방 생성
				chatRoom.name_ = roomNick;
				
				String[] idArray = roomKey.split(","); // ','를 기준으로 아이디 값만 받아오기
				for(int i = 0; i < idArray.length; i++){				
					ClientHandler clientHandler = clientHandlerManager.getClientHandler(idArray[i]); //전체 클라이언트 매니저에서 id를 이용하여 해당 클라이언트를 찾아내기
					if(clientHandler == null){	 //해당 클라이언트가 서버 메모리에 없으면 임시로 클라이언트 생성해서 메모리에 올려놓음
						clientHandler = new ClientHandler(idArray[i]);	
						clientHandlerManager.addClientHandler(clientHandler); //전체 클라이언트 매니저에 임시 클라이언트 추가		
					}
					chatRoom.enterClient(clientHandler); //채팅 방으로 입장 (임시 클라이언트)
				}
				addRoom(chatRoom); //RoomManager에 채팅 방 추가	
			}
		}
	}
}

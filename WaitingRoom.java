//대기방 객체
public class WaitingRoom extends Room {
	/**********************************
	 * WaitingRoom(String name) - 각 변수들을 초기화
	 * name - 방 이름
	**********************************/
	public WaitingRoom(){
		super(RoomManager.waitingRoomKey,"WaitingRoom");
	}
}

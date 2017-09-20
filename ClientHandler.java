import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//클라이언트와의 연결을 유지하는 책임을 가지며
//클라이언트의 요청을 받아 그 요청을 RequestHandler에 전달할 책임을 지닌다.
//클라이언트의 정보를 가지고 있는 객채
public class ClientHandler implements Runnable {
	/**********************************
	 * requestHandler_ - 명령 핸들러
	 * communicator_ - 커뮤티케이터
	 * gameRoom_ - 클라이언트가 현재 들어가있는 게임 방
	 * chatRoom_ - 클라이언트가 접속해 있는 채팅 방
	 * account_ - 클라이언트 정보
	 * ip_ - 클라이언트 ip
	 * isAlive - 클라이언트 상태 (false 상태면 클라이언트 thread 종료)
	 * thread_ - 클라이언트 스레드
	 * pingPongResult - 핑퐁 테스트 결과 (true - 성공)
	**********************************/
	public RequestHandler requestHandler_; 
	public Communicator communicator_; 
	public GameRoom gameRoom_; 
//	public HashMap<String, ChatRoom> chatRoom_; 
	public Account account_;
	public String ip_; 
	public boolean isAlive;
	private Thread thread_;
	public boolean pingPongResult;

	/**********************************
	 * ClientHandler(Communicator communicator, Room room) - 각 변수들을 초기화
	 * communicator - 소켓 정보를 가지고 있는 커뮤니케이터
	**********************************/
	public ClientHandler(Communicator communicator) {
		this.communicator_ = communicator;
		this.gameRoom_ = null;
//		this.chatRoom_ = new HashMap<String, ChatRoom>();

		//클라이언트에게서 처음 받은 데이터는 클라이언트 정보
		try {			
			String accuntData = this.communicator_.receiveRequest();
			JSONParser parser = new JSONParser(); 
			JSONObject jsonObj = (JSONObject)parser.parse(accuntData); 
			JSONObject jsonAccount = (JSONObject)jsonObj.get("account");
									
			String id = (String)jsonAccount.get("id"); // 계정 아이디
			String email = (String)jsonAccount.get("email"); // 계정 이메일
			String nick = (String)jsonAccount.get("nick"); // 계정 닉네임
			String type = (String)jsonAccount.get("type"); // 계정 타입 ex)google, facebook, local
			String status = (String)jsonAccount.get("status"); //계정 접속 상태 ex)service, app ->서비스에서 접속을 시도 한 건지 어플을 실행시켜서 접속을 시도 한 건지
			
			account_ = new Account(id, email, nick, type, status);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.requestHandler_ = new RequestHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.ip_ = ((CommunicatorUTF)communicator).getSocket().getInetAddress().toString();
		
		this.isAlive = true;		
		this.pingPongResult = true;
		ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "ClientHandler 생성 완료");	
	}
	
	/**********************************
	 * ClientHandler(String id) - id로 비어있는 클라이언트 만들기 용
	 * id - 클라이언트 아이디
	**********************************/
	public ClientHandler(String id) {
		this.communicator_ = null;
		this.gameRoom_ = null;
//		this.chatRoom_ = new HashMap<String, ChatRoom>();

		account_ = new Account(id, null, null, null, null);		

		try {
			this.requestHandler_ = new RequestHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.ip_ = "";
		
		this.isAlive = false;		
		this.pingPongResult = true;
		
		ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "임시 ClientHandler 생성 완료");	
	}
	
	
	/**********************************
	 * keepConnect() - 클라이언트 스레드를 실행
	**********************************/
	public void keepConnect() {
		ServerLog.getInstance().log(this.getClass().getName(), this.account_.id_ + "의 소켓 연결 방식은 [" + this.account_.status_ + "]입니다.");
		ServerLog.getInstance().clientLog(this.account_.id_, "서버에 접속");
		thread_ = new Thread(this);
		thread_.start();
	}

	/**********************************
	 * sendResponse(String msg) - 해당 클라이언트로 메세지 전달
	 * msg - 전달 할 메세지
	**********************************/
	public void sendResponse(String msg) throws Exception{
		this.communicator_.sendResponse(msg);
	}

	/**********************************
	 * alterClient(ClientHandler clientHandler) - 클라이언트 정보 수정
	 * clientHandler - 새로운 정보를 가지고 있는 클라이언트
	**********************************/
	public void alterClient(ClientHandler clientHandler){
		this.requestHandler_ = clientHandler.requestHandler_; 
		this.communicator_ = clientHandler.communicator_; 
		this.gameRoom_ = clientHandler.gameRoom_; 
//		this.chatRoom_ = clientHandler.chatRoom_;
		this.account_ = clientHandler.account_; 
		this.ip_ = clientHandler.ip_; 
		this.isAlive = clientHandler.isAlive; 
		this.pingPongResult = clientHandler.pingPongResult;
							
		ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "미 접속 시 요청 들어온 명령 리스트");

		String hashesKey = this.account_.id_ + "MissMsg";
		
		JedisHandler jedisHandler = new JedisHandler();
		final long fieldNum = jedisHandler.jedisHlen(hashesKey); //해당 유저 hashes에 저장할 fieldNum 받아오기
		String[] field = new String[(int) fieldNum];
		for(int i = 0; i < field.length; i++)
			field[i] = String.valueOf(i);

		ArrayList<Object> value = jedisHandler.jedisHget(hashesKey, field);
		for(int i = 0; i < value.size(); i++){
			try {
				ServerLog.getInstance().log(this.getClass().getName(), "명령: " + (String)value.get(i));
				sendResponse((String)value.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		jedisHandler.jedisDel(hashesKey);
		
		ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "미 접속 시 요청 들어온 명령 리스트 초기화");
	}
	
	/**********************************
	 * run() - 클라이언트 스레드 구현부
	**********************************/
	public void run() {
		ServerLog.getInstance().log(this.getClass().getName(), this.account_.id_ + "스레드 시작");	
//		PingPongThread pingPongThread = new PingPongThread(5);
//		pingPongThread.start();
		
		while(isAlive) //클라이언트가 살아 있을 때 만 작동
		{
			try {
				ServerLog.getInstance().log(this.getClass().getName(), this.account_.id_ + " 로 부터 명령이 전송되기를 기다립니다.");
				String msg = communicator_.receiveRequest(); //클라이언트로부터 명령을 받음
				ServerLog.getInstance().log(this.getClass().getName(), this.account_.id_ + " 로 부터 받은 명령으로 명령을 수행합니다.");
				this.requestHandler_.processRequest(msg, this); //받은 명령을 수행
				ServerLog.getInstance().log(this.getClass().getName(), this.account_.id_ + " 로 부터 받은 명령으로 수행된 명령이 끝났습니다.");
			}catch(java.io.EOFException ee) {
				ServerLog.getInstance().log(this.getClass().getName(),this.account_.id_ + " 와의 연결이 종료됩니다.");
				break;
			}catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}	
		ServerLog.getInstance().log(this.getClass().getName(), this.account_.id_ + "스레드 종료");

		if(this.gameRoom_ != null){
			String cmd = "{\"cmd\":\"exitGameRoom\",\"request\":{\"\":\"\"}}";
			this.requestHandler_.processRequest(cmd, this); //현재 접속한 게임 방에서 퇴장 명령			
		}				

		RoomManager roomManager = RoomManager.getInstance(); //RoomManager 참조		
		Room waitingRoom = roomManager.getRoom(RoomManager.waitingRoomKey); //waitingRoom 정보 가져오기 (waitingRoom key값 0)
		waitingRoom.exitClient(this); //대기실에서 퇴장
		
		this.communicator_.stop(); //클라이언트 소켓 연결 해제
		ServerLog.getInstance().clientLog(account_.id_, "서버 접속 끊김");
		
		//클라이언트 계정 정보, 접속한 채팅 방 제외하고 모두 초기화
		this.gameRoom_ = null;
		this.ip_ = null;
		this.thread_ = null;
		this.isAlive = false;
	}

	private class PingPongThread extends Thread {
		public boolean isAlive;
		private int interrupTime;
		public PingPongThread(int interval){ //체크 주기(초)
			isAlive = true;
			interrupTime = interval * 1000;
		}
		public void run(){
			ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "  핑퐁 테스트 시작");
			while(isAlive){
				try {
					if(!communicator_.getSocket().isClosed()){
//						ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "  핑퐁 발사");
						sendResponse("pingPongTest"); //핑퐁 테스트 수행	 
						pingPongResult = false;
					}
					
					//핑봉 결과 값 기다림 (체크 주기)
					Thread.sleep(interrupTime);

					if(!communicator_.getSocket().isClosed()){
//						ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "  핑퐁 테스트 결과: " + pingPongResult);
						if(!pingPongResult){ //핑퐁 결과가 false면 소켓 닫고 테스트 스레드 종료
							communicator_.stop();
							break;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}			
			ServerLog.getInstance().log(this.getClass().getName(), account_.id_ + "  핑퐁 테스트 종료");
		}
	}
}

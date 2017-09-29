//방 상위 객체
public class Room {	
	/**********************************
	 * key_ - 방 고유 키값
	 * name_ - 방 이름
	 * clientHandlerManager_ - 방 client 관리 매니저
	 * hostClientHandler - 방장 클라이언트
	 * roomType_ - 방 유형
	**********************************/
	public String key_; 
	public String name_; 
	public ClientHandlerManager clientHandlerManager_; 
	public ClientHandler hostClientHandler; 
	//protected String roomType_; 

	/**********************************
	 * Room(String name) - 각 변수들을 초기화
	 * name - 방 이름
	**********************************/
	public Room(String key, String name){
		this.key_ = key;
		this.name_ = name;
		this.clientHandlerManager_ = new ClientHandlerManager(this.name_);
		this.hostClientHandler = null; //hostClientHandler는 roomManager에 room이 할당 되면 부여
	}

	/**********************************
	 * initRequestCommand() - 방에 따른 명령 실행자 세팅
	**********************************/
	public RequestHandler initRequestCommand(){
		ServerLog.getInstance().log(this.getClass().getName(), "initRequestCommand");
		RequestHandler requestHandler = null;
		try {
			//명령 실행자 추가
			requestHandler = new RequestHandler();
			requestHandler.addCommand("commandList",new CommandExecutorCommandList()); //등록 된 명령어 목록
			requestHandler.addCommand("clientList",new CommandExecutorGameRoomClientList()); //방 인원 보기
			requestHandler.addCommand("nickChange",new CommandExecutorNickChange()); //닉네임 변경		 
			requestHandler.addCommand("exitServer",new CommandExecutorExitServer()); //접속 종료

			requestHandler.addCommand("gameRoomList",new CommandExecutorGameRoomList()); //방 목록 보기
			requestHandler.addCommand("createGameRoom",new CommandExecutorCreateGameRoom()); //게임 방 생성
			requestHandler.addCommand("createChatRoom",new CommandExecutorCreateChatRoom()); //채팅 방 생성
			requestHandler.addCommand("enterGameRoom",new CommandExecutorEnterGameRoom()); //게임 방 입장
			requestHandler.addCommand("enterInviteGameRoom",new CommandExecutorEnterInviteGameRoom()); //초대 게임 방 입장
			
			requestHandler.addCommand("exitGameRoom",new CommandExecutorExitGameRoom()); //게임 방 나가기	
			requestHandler.addCommand("gameChat",new CommandExecutorGameChat()); //게임 방 채팅 명령
			requestHandler.addCommand("kickClient",new CommandExecutorKickClientRoom()); //게임 방 강퇴 명령
			requestHandler.addCommand("gameCanvas",new CommandExecutorGameCanvas()); //게임 방  그림 명령
			requestHandler.addCommand("readyGameRoom",new CommandExecutorReadyGameRoom()); //게임 방 레디
			requestHandler.addCommand("startGameRoom",new CommandExecutorStartGameRoom()); //게임 방 시작
			requestHandler.addCommand("timeOutGameRoom",new CommandExecutorTimeOutGameRoom()); //게임 타임 아웃  
			requestHandler.addCommand("progressGameRoom",new CommandExecutorProgressGameRoom()); //게임 타임 아웃  
			requestHandler.addCommand("loadCanvas",new CommandExecutorLoadCanvas()); //방에 저장 된 그림 정보 가져오기
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return requestHandler;
	}

	/**********************************
	 * enterClient(ClientHandler clientHandler) - 방에 클라이언트 입장
	 * clientHandler - 방에 입장 할 클라이언트
	**********************************/
	public void enterClient(ClientHandler clientHandler){
		this.clientHandlerManager_.addClientHandler(clientHandler);
		clientHandler.requestHandler_ = initRequestCommand();
		
		ServerLog.getInstance().log(this.getClass().getName(), this.name_ + " 방에  " + clientHandler.account_.id_ +" 입장");
		ServerLog.getInstance().log(this.getClass().getName(), 
				this.name_ + " 방 현재 인원: " + this.clientHandlerManager_.clientHandlers_.size() + "명");
	}

	/**********************************
	 * exitClient(ClientHandler clientHandler) - 방에서 클라이언트 퇴장
	 * clientHandler - 방에서 퇴장 할 클라이언트
	**********************************/
	public void exitClient(ClientHandler clientHandler){
		this.clientHandlerManager_.removeClientHandler(clientHandler);

		ServerLog.getInstance().log(this.getClass().getName(), this.name_ + " 방에서 " + clientHandler.account_.id_ +" 퇴장");
		ServerLog.getInstance().log(this.getClass().getName(), 
				this.name_ + " 방 현재 인원: " + this.clientHandlerManager_.clientHandlers_.size() + "명");
	}
}

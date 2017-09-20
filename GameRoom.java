import java.util.Iterator;

//게임을 진행하는 방
public class GameRoom extends Room {
	/**********************************
	 * isLock_ - 방 비밀방 인가?
	 * password_ - 비밀방이라면 방 비밀번호
	 * maxNum_ - 방 최대 인원
	 * isStart_ - 게임 시작했는가?
	 * readyNum_ - ready를 하고 있는 클라이언트 수 (게임 시작 가능 판별)
	 * quizClientHandler - 퀴즈 출제자
	 * totalQuizNum_- 해당 게임방에서 진행한 총 퀴즈 수 (redis key에 사용)
	 * maxQuiz_ - 총 문제 갯수
	 * numQuiz_ - 진행 된 문제 갯수
	 * quiz_ - 현재 진행중인 퀴즈 정답
	 * gameTime_ - 게임 진행 초
	**********************************/
	public boolean isLock_;
	public String password_;
	public Long maxNum_;	
	public boolean isStart_;
	public ClientHandler quizClientHandler;
	public int totalQuizNum_ = 0;
	public int maxQuiz_ = 3;
	public int numQuiz_ = 0;
	public String quiz_;
	public long gameTime_ = 0;
	public static String[] QUIZ_LIST = {"바나나", "딸기", "사과", "수박", "오렌지"};
	
	/**********************************
	 * GameRoom(String name) - 각 변수들을 초기화
	 * name - 방 이름
	**********************************/
	public GameRoom(String key, String name, boolean isLock, String password, Long maxNum){
		super(key, name);
		this.isLock_ = isLock;
		this.password_ = password;
		this.maxNum_ = maxNum;
	}

	/**********************************
	 * setHostClient(ClientHandler clientHandler) - 방에 방장 임명
	 * clientHandler - 방장이 될 클라이언트
	**********************************/
	public void setHostClient(ClientHandler clientHandler){
		this.hostClientHandler = clientHandler;
		//방장만 실행 가능한 명령들 추가
		this.hostClientHandler.requestHandler_.addCommand("kickClient", new CommandExecutorKickClientRoom()); //강제 퇴장 명령
		ServerLog.getInstance().log(this.getClass().getName(),  this.name_ + " 방 Host: [" + clientHandler.account_.id_ + "]");
	}

	/**********************************
	 * isAllClientReady() - 게임 방 모든 인원이 레디 상태인지 체크하는 함수
	**********************************/
	public boolean isAllClientReady(){
		if(this.clientHandlerManager_.clientHandlers_.size() < 2){ //방에 클라이언트 수가 1명 이하면 무조건 false -> 혼자서 게임 시작 못하게
			return false;			
		}
		Iterator<String> iterator = this.clientHandlerManager_.clientHandlers_.keySet().iterator();
		while(iterator.hasNext()){
			String id = iterator.next();
			ClientHandler client = this.clientHandlerManager_.getClientHandler(id);
			if(!client.account_.id_.equals(hostClientHandler.account_.id_)){ //방장을 제외하고 ready를 안한 클라이언트가 한명이라도 있으면 false return
				if(!client.account_.isReady_){
					return false;
				}
			}
		}
		return true;
	}
}

import java.util.HashMap;

//각종 명령 실행자를 관리
public class CommandManager {
	/**********************************
	 * executor_ - 실제 명령 실행자
	 * commands_ - 명령 실행자 목록
	 * defaultExecutor_ - 기본 명령 실행자
	**********************************/
	private CommandExecutor executor_ = null;
	public HashMap<String, CommandExecutor> commands_;

	/**********************************
	 * CommandManager() - 각 변수들을 초기화
	**********************************/
	public CommandManager() throws Exception {
		commands_ = new HashMap<String, CommandExecutor>();
		//서비스에서 작동할 명령들
		addCommand("pingPong", new CommandExecutorPingPong()); //계정 연결 여부 명령어
		addCommand("normalChat", new CommandExecutorNormalChat()); //기본 채팅 명령어 
		addCommand("readChat", new CommandExecutorReadChat()); //채팅 읽음 처리 명령어
		addCommand("statusChange", new CommandExecutorStatusChange()); //계정 상태 변경 명령어 
	}

	/**********************************
	 * addCommand(String command,CommandExecutor commandExecutor) - 명령 실행자를 목록에 추가
	 * command - 명령 실행자 키값
	 * commandExecutor - 추가 할 명령 실행자
	**********************************/
	public void addCommand(String command,CommandExecutor commandExecutor) {
//		ServerLog.getInstance().log(this.getClass().getName(), "'" + command + "' 명령을 추가합니다.");
		this.commands_.put(command,commandExecutor);
	}

	/**********************************
	 * removeCommand(String command) - 명령 실행자를 목록에서 제거
	 * command - 제거할 명령 실행자 키값
	**********************************/
	public void removeCommand(String command) {
		ServerLog.getInstance().log(this.getClass().getName(), "'" + command + "' 명령을 제거합니다.");
		this.commands_.remove(command);
	}

	/**********************************
	 * execute(String command, String request, ClientHandler clientHandler) - 명령 실행
	 * command - 실행 할 명령어
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String command, String request, ClientHandler clientHandler) throws Exception {
		try {			
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			ServerLog.getInstance().log(this.getClass().getName(),"명령어 : "+command);
			ServerLog.getInstance().log(this.getClass().getName(),"디테일 : "+request);

			executor_ = findExecutor(command); //수행 할 명령에 맞는 실행자를 가져옴
			if(executor_ == null){ //명령 실행자가 없는 경우
				ServerLog.getInstance().log(this.getClass().getName(), command + "은(는) 등록 되어있는 명령이 아닙니다.");
//				clientHandler.sendResponse("실행할 수 없는 명령입니다.");
			}else{ //명령 실행자가 있는 경우	
				ServerLog.getInstance().log(this.getClass().getName(),"명령 실행자 : " + executor_.getClass().getName());
				ServerLog.getInstance().clientLog(clientHandler.account_.id_, command + " 명령 수행");
				ServerLog.getInstance().clientLog(clientHandler.account_.id_, "디테일: " + request + "");
				executor_.execute(request, clientHandler); //명령 실행
			}
			
		} finally {
			executor_ = null;
		}
	}

	/**********************************
	 * findExecutor(String command) - 명령 실행자를 찾는 함수
	 * command - 찾을 명령 실행자의 키값
	**********************************/
	private CommandExecutor findExecutor(String command) {
		CommandExecutor executor = (CommandExecutor) this.commands_.get(command); //명령 실행자 찾기
		return executor; //있으면 반환 없으면 null 반환
	}
}

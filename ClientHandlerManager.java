import java.util.HashMap;
import java.util.Iterator;

//서버에 접속한 클라이언트 관리하는 객채
public class ClientHandlerManager {
	/**********************************
	 * handleBound_ - 클라이언트 매니저가 관리하는 클라이언트 범위 ex)ALL - 전역 매니저
	 * clientHandlerManager_ - 클라이언트 매니저 객체 전역 변수
	 * clientHandlers_ - 클라이언트 목록
	**********************************/
	public String handleBound_; 
	private static ClientHandlerManager clientHandlerManager_; 
	public HashMap<String, ClientHandler> clientHandlers_; //<클라이언트 아이디, 클라이언트 객채>

	/**********************************
	 * ClientHandlerManager() - 각 변수들을 초기화
	**********************************/
	public ClientHandlerManager(String handleBound) {
		this.handleBound_ = handleBound;
		this.clientHandlers_ = new HashMap<String, ClientHandler>();
		ServerLog.getInstance().log(this.getClass().getName(), "클라이언트 매니저 생성 [" + this.handleBound_ + "]");
	}

	/**********************************
	 * getInstance() - 클라이언트 매니저 객체를 생성하고 반환하는 함수
	**********************************/
	public static ClientHandlerManager getInstance() {
		//clientHandlerManager_가 null값이면 초기화
		if(clientHandlerManager_ == null)
			clientHandlerManager_ = new ClientHandlerManager("ALL");
		return clientHandlerManager_;
	}

	/**********************************
	 * addClientHandler(ClientHandler clientHandler) - 새로운 클라이언트 추가
	 * clientHandler - 추가 할 클라이언트
	**********************************/
	public void addClientHandler(ClientHandler clientHandler) {
		this.clientHandlers_.put(clientHandler.account_.id_, clientHandler); //클라이언트 추가 혹은 수정
		
		//접속 client IP 및 접속자 수 출력
		ServerLog.getInstance().log(this.getClass().getName(), "클라이언트 매니저 위치 [" + this.handleBound_ + "]");
		ServerLog.getInstance().log(this.getClass().getName(), "아이디 [ " + clientHandler.account_.id_ + " ] 추가");
		ServerLog.getInstance().log(this.getClass().getName(), "IP주소 [ " + clientHandler.ip_ + " ]");
		ServerLog.getInstance().log(this.getClass().getName(), "접속자 수:" + clientHandlers_.size());
	}

	/**********************************
	 * alterClientHandler(String id, ClientHandler newClientHandler) - 클라이언트 정보 수정
	 * id - 수정 할 클라이언트 아이디
	 * newClientHandler - 새로 바꿀 클라이언트 정보
	**********************************/
	public void alterClientHandler(ClientHandler newClientHandler) {
		ClientHandler oldCientHandler =  this.clientHandlers_.get(newClientHandler.account_.id_);
		oldCientHandler.alterClient(newClientHandler);
		
		//접속 client IP 및 접속자 수 출력
		ServerLog.getInstance().log(this.getClass().getName(), "클라이언트 매니저 위치 [" + this.handleBound_ + "]");
		ServerLog.getInstance().log(this.getClass().getName(), "아이디 [ " + oldCientHandler.account_.id_ + " ] 수정");
		ServerLog.getInstance().log(this.getClass().getName(), "IP주소 [ " + oldCientHandler.ip_ + " ]");
		ServerLog.getInstance().log(this.getClass().getName(), "접속자 수:" + clientHandlers_.size());
	}

	/**********************************
	 * removeClientHandler(ClientHandler clientHandler) - 클라이언트 추가
	 * clientHandler - 삭제 할 클라이언트
	**********************************/
	public void removeClientHandler(ClientHandler clientHandler) {
		this.clientHandlers_.remove(clientHandler.account_.id_); //클라이언트 삭제		
		
		//삭제 할 client IP 및 접속자 수 출력
		ServerLog.getInstance().log(this.getClass().getName(), "클라이언트 매니저 위치 [" + this.handleBound_ + "]");
		ServerLog.getInstance().log(this.getClass().getName(), "아이디 [ " + clientHandler.account_.id_ + " ] 클라이언트 삭제");
		ServerLog.getInstance().log(this.getClass().getName(), "접속자 수:" + clientHandlers_.size());
	}

	/**********************************
	 * getClientHandler(String id) - 클라이언트 가져오기
	 * id - 가져 올 클라이언트 아이디
	**********************************/
	public ClientHandler getClientHandler(String id) {
		return this.clientHandlers_.get(id); //클라이언트 가져오기	
	}

	/**********************************
	 * stop() - 모든 클라이언트 연결 해제
	**********************************/
	public void stop(){
		Iterator<String> iterator = this.clientHandlers_.keySet().iterator();
		
		while(iterator.hasNext()){
			String id = (String)iterator.next();
			ClientHandler clientHandler = clientHandlers_.get(id);
			clientHandler.requestHandler_.processRequest("cmd/exitServer", clientHandler); //서버 접속 종료 명령
		}	
	}
}
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//채팅 서버 객체
public class Server implements TCPServer {
	/**********************************
	 * PORT - 채팅 서버 포트 번호
	 * clientHandlerManager_ - 전체 클라이언트 관리 매니저
	 * roomManager_ - 방 관리 매니저
	 * waitingRoom_ - 대기실 (default 방)
	 * server_ - 서버 소켓
	 * pingPongThread - 클라이언트 실시간 연결 확인하는 스레드
	**********************************/
	private final static int PORT = 5000; 
	private ClientHandlerManager clientHandlerManager_; 
	private RoomManager roomManager_;
	private WaitingRoom waitingRoom_;	
	private ServerSocket server_;
	
	/**********************************
	 * Server() - 각 변수들을 초기화
	**********************************/
	public Server() {
		this.clientHandlerManager_ = ClientHandlerManager.getInstance();
		this.roomManager_ = RoomManager.getInstance();
		this.waitingRoom_ = new WaitingRoom();
	}
	
	public static void main(String[] args) throws IOException{
		try{
			TCPServer server = new Server();
			server.startServer();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**********************************
	 * startServer() - 서버를 시작하는 함수
	**********************************/
	@Override
	public void startServer() throws Exception{
		server_ = new ServerSocket(PORT); //서버 소켓 생성
		ServerLog.getInstance().log(this.getClass().getName(),"서버가 시작합니다.");
		this.roomManager_.addRoom(this.waitingRoom_); //룸 매니저에 대기실(default room)을 생성
//		this.roomManager_.loadChatRoom(); //채팅 방 불러오기
			
		while(true){
			try {
				ServerLog.getInstance().log(this.getClass().getName(),"연결요청을 기다립니다.");
				Socket connectedSocket = server_.accept();
				
				ServerLog.getInstance().log(this.getClass().getName(),"새로운 client가 접속했습니다.");
				processService(connectedSocket);
			} catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
		ServerLog.getInstance().log(this.getClass().getName(),"서버가 종료되었습니다.");
	}
	
	/**********************************
	 * shutDownServer() - 서버를 종료 하는 함수
	**********************************/
	public void shutDownServer() throws Exception {
		ServerLog.getInstance().log(this.getClass().getName(),"열려진 서버 서비스가 있다면 종료합니다.");
		
		//모든 클라이언트 접속 종료
		if(this.clientHandlerManager_ != null) 
			this.clientHandlerManager_.stop();
		
		ServerLog.getInstance().log(this.getClass().getName(),"서버 소켓의 연결을 닫습니다.");
		this.server_.close();
	}

	/**********************************
	 * processService(Socket connectedSocket) - 클라이언트의 소켓을 받아서 클라이언트 서비스 시작하는 함수
	 * connectedSocket - 연결된 소켓 정보
	**********************************/
	private void processService(Socket connectedSocket) throws IOException {				
		ClientHandler clientHandler = new ClientHandler(new CommunicatorUTF(connectedSocket)); //새로운 클라이언트 생성
				
		ClientHandler oldClientHandler = this.clientHandlerManager_.getClientHandler(clientHandler.account_.id_);
		if(oldClientHandler == null){ //이전에 접속한 적 없는 클라이언트 (새로 클라이언트 객채를 만들어야 되는 클라이언트)
			this.clientHandlerManager_.addClientHandler(clientHandler); //클라이언트 매니저에 클라이언트 추가			
		}else{ //이전에 접속한 적 있는 클라이언트 (클라이언트 정보만 갱신하면 되는 클라이언트)
			this.clientHandlerManager_.alterClientHandler(clientHandler); //클라이언트 매니저에 클라이언트 수정				
		}		
		
		ClientHandler newClientHandler = this.clientHandlerManager_.getClientHandler(clientHandler.account_.id_);
		newClientHandler.keepConnect();  //클라이언트 접속 유지
		
		if(!newClientHandler.account_.status_.equals("service")){ //클라이언트 소캣 연결이 서비스로 이루어 진 것이 아닐 경우
			WaitingRoom waitingRoom = (WaitingRoom)this.roomManager_.getRoom(RoomManager.waitingRoomKey); //waitingRoom 정보 가져오기 
			waitingRoom.enterClient(newClientHandler); //접속한 클라이언트 대기실로 입장
		}
	}
}


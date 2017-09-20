//서버 로그 출력 객채
public class ServerLog {
	/**********************************
	 * serverLog_ - 서버로그 객체 전역 변수
	**********************************/
	private static ServerLog serverLog_ = null;

	/**********************************
	 * ServerLog() - 생성자
	**********************************/
	private ServerLog() {}

	/**********************************
	 * getInstance() - 서버 로그 객체를 생성하고 반환하는 함수
	**********************************/
	public static ServerLog getInstance() {
		//serverLog_가 null값이면 초기화
		if(serverLog_ == null)
			serverLog_ = initLog();
		return serverLog_;
	}

	/**********************************
	 * initLog() - serverLog_를 초기화 후 반환
	**********************************/
	private static ServerLog initLog() {
		serverLog_ = new ServerLog();
		return serverLog_;
	}

	/**********************************
	 * log(String subTitle,String message) - 서버 로그 출력하는 함수
	 * subTitle - 해당 클래스 위치
	 * message - 상세 내용
	**********************************/
	public void log(String subTitle,String message) {
		System.out.println("LOG ["+subTitle+"] : "+message);
		System.out.flush();
	}

	/**********************************
	 * clientLog(String subTitle,String message) - 해당 클라이언트 행동 로그 Redis에 저장하는 함수
	 * id - 해당 클라이언트 아이디
	 * message - 상세 내용
	**********************************/
	public void clientLog(String id,String message) {
//		System.out.println("LOG ["+id+"] : "+message);
//		System.out.flush();
	}
}

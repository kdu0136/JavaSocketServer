
//계정 정보
public class Account {
    public String id_; //유저 아이디
    public String email_; //유저 이메일
    public String nick_; //유저 닉네임
    public String type_; //계정 종류 ex)google, facebook, local(자체 회원)
    public String status_; //계정 접속 상태 ex)service, app ->서비스에서 접속을 시도 한 건지 어플을 실행시켜서 접속을 시도 한 건지
	public boolean isReady_; //
	

    /**********************************
     * Account(int no, String id, String email, String nick, String type) - 각 변수들을 초기화
     **********************************/
    public Account(String id, String email, String nick, String type, String status) {
        this.id_ = id;
        this.email_ = email;
        this.nick_ = nick;
        this.type_ = type;
        this.status_ = status;
    }
}

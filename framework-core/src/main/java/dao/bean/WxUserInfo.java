package dao.bean;

/**
 * @description:
 * @author:Administrator
 * @date: 2018/5/2
 **/
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WxUserInfo {
	private String openid;
	private String session_key;
	private String unionid;
	private int errcode;
	private int errmsg;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSession_key() {
		return session_key;
	}

	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public int getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(int errmsg) {
		this.errmsg = errmsg;
	}



	public WxUserInfo() {
	}


	@Override
	public String toString() {
		return "Value{" +
				"openid=" + openid +
				", session_key='" + session_key + '\'' +
				'}';
	}

}

package cn.spdb.harrier.api.model;

/**
 * 用户注册对象
 * 
 * @author ruoyi
 */
public class RegisterBody extends LoginBody
{

	/**
	 * 用户邮箱
	 */
	private String userEmail;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	
}

package ekaiser.nzlov.plugin.user;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import ekaiser.nzlov.methodmap.EMethodMapManage;
import ekaiser.nzlov.methodmap.EMethodMessage;
import ekaiser.nzlov.notepad.data.NotepadData;
import ekaiser.nzlov.plugins.IEPlugin;
import ekaiser.nzlov.util.GuidCreator;
/**
 * 3、	修改用户信息：User:modify
 * 				0：用户名 1：姓名 2：身份证 3：性别 4：年龄 5：家电 6：移动电话
 * 				7：E-mail 8：住址 9：照片(文件数据)
 * 			Re：0|1
 *
 * @author nzlov
 *
 */
public class UserModifyPlugin extends IEPlugin{
	private static Logger logger = LogManager.getLogger("UserModifyPlugin");

	@Override
	public Object start() {
		// TODO Auto-generated method stub
    	logger.entry();
		EMethodMapManage.addMethodMap("User", this);

    	logger.exit();
		return true;
	}

	@Override
	public Object start(HashMap<String, Object> pa) {
		// TODO Auto-generated method stub
    	logger.entry();
    	logger.exit();
		return null;
	}

	@Override
	public Object stop() {
		// TODO Auto-generated method stub
    	logger.entry();
		EMethodMapManage.removeMethodMap("User:modify");
    	logger.exit();
		return null;
	}
	
	
	public void modify(EMethodMessage msg) throws UnsupportedEncodingException, SQLException{
    	logger.entry();
		IoSession session = (IoSession)msg.getObject();
		NotepadData data = (NotepadData)msg.getParameter();
		String sname = (String)session.getAttribute("name");
		String login = data.getDataBlock(0, "123").getDataToString();
		int limit = (int)EMethodMapManage.sendMethodMessage("Limit:isLimit", session, data);
		switch(limit){
		case 1:break;
		case 2:
			if(sname.equals(login)){
				break;
			}
		default:
			data.clean();
			data.setName("Error","123");
			data.putString("3", "123");
			session.write(data);
			logger.exit();
			return;
		}
		String name = data.getDataBlock(1, "123").getDataToString();
		String id = data.getDataBlock(2, "123").getDataToString();
		int sex = Integer.parseInt(data.getDataBlock(3, "123").getDataToString());
		int age = Integer.parseInt(data.getDataBlock(4, "123").getDataToString());
		String phone = data.getDataBlock(5, "123").getDataToString();
		String moblie = data.getDataBlock(6, "123").getDataToString();
		String email = data.getDataBlock(7, "123").getDataToString();
		String addr  = data.getDataBlock(8, "123").getDataToString();
		String photo = data.getDataBlock(9, "123").getDataToString();
		
		
		String sql = "update user_info_table ui,user_table u set ui.user_info_name='"+name+"' , ui.user_info_id='"+id+"' , ui.user_info_sex="+sex + " , ui.user_info_age=" + age +
							" , ui.user_info_phone='" + phone+"' ui.user_info_moblie='" + moblie +"' , ui.email='" + email+"' , ui.addr='" + addr +"' ui.photo='" + photo+"' where ui.user_id=u.id and " +
							"u.user_loginname='" + login+"';";
		int i = (int)EMethodMapManage.sendMethodMessage("Database:update", this, sql);
		
		data.clean();
		data.putString(i+"", "123");
		
		session.write(data);
		
    	logger.exit();
	}
	
}

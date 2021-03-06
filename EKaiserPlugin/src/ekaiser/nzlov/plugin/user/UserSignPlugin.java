package ekaiser.nzlov.plugin.user;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.text.SimpleDateFormat;
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
 * 7、	签到：User:sign 时间属性由服务器产生
 *           0：用户名 1：签到/签退状态 
 *             签到|签退状态
 *             上午签到 00
 *             上午签退 01
 *             中午签到 10
 *             中午签退 11
 *             晚上签到 20
 *             晚上签退 21
 * 			Re：0|1
 *
 */
public class UserSignPlugin extends IEPlugin{
	private static Logger logger = LogManager.getLogger("UserSignPlugin");

	private final static String v="1.1";
	@Override
	public Object start() {
		// TODO Auto-generated method stub
    	logger.entry();
    	
    	setVersion(v);
    	
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
		EMethodMapManage.removeMethodMap("User:sign");
    	logger.exit();
		return null;
	}
	
	
	public void sign(EMethodMessage msg) throws UnsupportedEncodingException{
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
		int state = Integer.parseInt(data.getDataBlock(1, "123").getDataToString());
		
		String user_id = (String) session.getAttribute("guid");
		
		GuidCreator g = new GuidCreator();
		String guid = g.createNewGuid(GuidCreator.AfterMD5);
		
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		String sql = "insert into sign_table(id,user_id,state,date) values('"+guid+"','"+user_id+"',"+state+",'"+date+"');";
		int i = (int)EMethodMapManage.sendMethodMessage("Database:update", this, sql);
		
		data.clean();
		data.putString(i+"", "123");
		
		session.write(data);
    	logger.exit();
	}
	
}

package ekaiser.nzlov.ekaisermanage.net;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import ekaiser.nzlov.methodmap.EMethodMapManage;
import ekaiser.nzlov.methodmap.EMethodMessage;
import ekaiser.nzlov.notepad.data.NotepadData;

public class ServerHandler extends IoHandlerAdapter{
	private static Logger logger = LogManager.getLogger("ServerHandler");
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	logger.entry();
    	logger.info("服务器发生异常： {}", cause.getMessage());
    	logger.exit();
    }
 
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	logger.entry();
    	
    	NotepadData emsg = (NotepadData)message;
    	
//    	logger.info("服务器接收到数据： {}",emsg);
        String content = emsg.getName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datetime = sdf.format(new Date());
        
        if(content.startsWith("Server")){
            logger.info("服务器过滤： " + datetime + "\t" + content);
            return;
        }
        
        logger.info("服务器处理: " + datetime + "\t" + content);
        
//        // 拿到所有的客户端Session
//        Collection<IoSession> sessions = session.getService().getManagedSessions().values();
//        // 向所有客户端发送数据
//        for (IoSession sess : sessions) {
//            sess.write(datetime + "\t" + content);
//        }
        
		String name = (String) session.getAttribute("name");
        if(name !=null){
        		EMethodMapManage.sendMethodMessage(content, session, emsg);
		}else if(content.equals("Login:login")){
			EMethodMapManage.sendMethodMessage(content, session, emsg);
		}else{
			emsg = new NotepadData("Error");
			emsg.putString("1", "123");
			session.write(emsg);
		}
    	logger.exit();
    }
 
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    	logger.entry();
    	logger.info("服务器发送消息： {}", (NotepadData)message);
    	logger.exit();
    }
 
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    	logger.entry();
    	logger.info("关闭当前session：{}#{}", session.getId(), session.getRemoteAddress());
        String user = (String) session.getAttribute("name");
        if(user!=null){
        	EMethodMapManage.sendMethodMessage("Login:closeSession", session, user);
        }
        CloseFuture closeFuture = session.close(true);
        closeFuture.addListener(new IoFutureListener<IoFuture>(){
            public void operationComplete(IoFuture future) {
                if (future instanceof CloseFuture) {
                    ((CloseFuture) future).setClosed();
                    logger.info("sessionClosed CloseFuture setClosed-->{},", future.getSession().getId());
                }
            }
        });
    	logger.exit();
    }
 
    @Override
    public void sessionCreated(IoSession session) throws Exception {
    	logger.entry();
    	logger.info("创建一个新连接：{}", session.getRemoteAddress());
    	logger.exit();
    }
 
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    	logger.entry();
    	logger.info("当前连接{}处于空闲状态：{}", session.getRemoteAddress(), status);
    	logger.exit();
    }
 
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    	logger.entry();
    	logger.info("打开一个session：{}#{}", session.getId(), session.getBothIdleCount());
    	logger.exit();
    }
}

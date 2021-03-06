package ekaiser.nzlov.ekaisermanage.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ekaiser.nzlov.ekaisermanage.data.StaticData;
import ekaiser.nzlov.ekaisermanage.net.Server;
import ekaiser.nzlov.methodmap.EMethodMapManage;
import ekaiser.nzlov.plugins.EPluginManage;

public class ServerMain {
	private static Logger logger = LogManager.getLogger("ServerMain"); 

	private Server server = null;
	
	public ServerMain(){
		logger.entry();
		EMethodMapManage.createEMethodMapManage();//创建方法映射管理器
		logger.info("创建方法映射管理器");
		EPluginManage.createPluginManage(StaticData.CONFIGF+"plugins.xml");//加载初始插件
		logger.info("加载初始插件");
		
		server = new Server();
		logger.info("创建网络IO");
		
		logger.exit();
	}
	
	public void start(){
		server.start(3421);
		
		logger.info(EMethodMapManage.getMethodMapList());
	}
	
	public void stop(){
		
	}
	
	public static void main(String[] args){
		new ServerMain().start();
	}
}

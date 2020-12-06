package recruit.crawling;

import java.util.Date;
import java.util.logging.Logger;

public class MyLogger {
	private static Logger log = Logger.getLogger(App.class.getName()); 
	public static void log(String msg) {
		log.info(new Date() + " : " + msg); 
	}
}

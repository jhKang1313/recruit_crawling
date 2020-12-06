package recruit.crawling;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class FileWriter {
	private final String filePath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "RecruitBatch"; 
	public Boolean makeFolder() throws Exception{
		File folder = new File(filePath);
		if(!folder.exists()){
			MyLogger.log(filePath + " 폴더가 없습니다. 새로 생성합니다.");
			return folder.mkdir();
		}
		return true;
	}
	public String getFileName(String fileName) throws Exception{
		return filePath + File.separator + fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	public abstract void write(String fileName, List<ApplicantInfo> content) throws Exception;
}

package recruit.crawling;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CSVFileWriter extends FileWriter{
	public String fieldTitle = "이름, 성별, 나이, 주소, 연락처, 이메일, URL \r\n";
	public BufferedWriter writer;		
	public void write(String fileName, List<ApplicantInfo> list) throws Exception {
		if(makeFolder()){
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFileName(fileName) + ".csv"), "MS949"));
			writer.write(fieldTitle);
			for(ApplicantInfo item : list){
				writer.write(item.name + "," + item.gender + "," + item.birth + "," + item.address + "," + "\'" + item.phone + "\'"+ "," + item.email + "," + item.url + "\r\n");
			}
			writer.close();
		}
	}
}

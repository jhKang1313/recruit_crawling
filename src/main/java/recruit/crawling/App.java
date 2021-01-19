package recruit.crawling;

import java.util.List;

public class App {
    public static void main( String[] args ) throws Exception{
    	RecruitSite site = new IncruitRecruitSite();
    			
    	List<ApplicantInfo> list = site.readPage(site.login("backersm", "Qqaazz1!"));
    	/*ApplicantInfo test = new ApplicantInfo();
    	test.name = "강진혁";
    	test.gender = "남";
    	test.birth = "1992";
    	test.phone = "01012345678";
    	
    	List<ApplicantInfo> list = new ArrayList<ApplicantInfo>();
    	list.add(test);*/
    	MyLogger.log("파일 쓰는중...");
    	FileWriter writer = new CSVFileWriter();
    	writer.write("recruit_", list);
    	MyLogger.log("파일 생성 완료");
    }
}
//Master
//Master

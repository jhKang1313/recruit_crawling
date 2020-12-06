package recruit.crawling;

import java.util.List;

import org.jsoup.Connection.Response;

public interface RecruitSite {
	public Response login(String id, String pw) throws Exception;
	public List<ApplicantInfo> readPage(Response res) throws Exception; 
}

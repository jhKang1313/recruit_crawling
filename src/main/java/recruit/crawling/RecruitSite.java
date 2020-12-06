package recruit.crawling;

import org.jsoup.Connection.Response;

public interface RecruitSite {
	public Response login(String id, String pw) throws Exception;
	public void readPage(Response res) throws Exception; 
}

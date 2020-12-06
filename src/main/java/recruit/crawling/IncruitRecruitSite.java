package recruit.crawling;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IncruitRecruitSite implements RecruitSite{
	private final String loginURL = "https://edit.incruit.com/login/loginprocess.asp";
	private final String encId = "9D1D2DA945622349B6DCFFBD7E2C97305797D0FDC9BBBC54CCCB53E7D9EE1739";
	private final String encPw = "B58CB9BC0EADCDEAFB829CA5B3A6A6FF5D2D987AA817DF428D347059CD563F5C";
	
	private final String searchingURL = "https://resumedb.incruit.com/list/searchresume.asp?SearchType=MAIN&birth2=1991&sex=1,%202&birth1=1997&rgn1=149&rgn2=11&mobileopenyn=Y&hropen=2&page={0}#list";
	
	private final String linkQuery = "th .txt-title a";
	private final String rowQuery = ".recruiter_list_warp table tr";
	private final String isCheckQuery = "span.contact-check";
	private final String baseInfoQuery = "table:eq(2) tr td";
	private final String subValue = ".recruiter_list_warp table input[type=hidden]";
	private final String rsmCodeQuery = ".recruiter_list_warp table tr input";
	
	public Response login(String id, String pw) throws Exception {
		Response response = (Response) Jsoup.connect(loginURL)
        		.data("txtUserID", "")
        		.data("txtPassword", "")
        		.data("SaveID", "on")
        		.data("SSL", "false")
        		.data("EncID", encId)
        		.data("EncPW", encPw)
        		.data("txtPartnerCode", "0")
        		.data("txtSubDomain", "www")
        		.data("isKeepLogIn", "N")
        		.data("strSSLFlag", "y")
        		.data("gotoURL", "https://www.incruit.com/")
        		.method(Method.POST).execute();
		return response;
	}
	public void readPage(Response res) throws Exception {
		List<ApplicantInfo> appList = new ArrayList<ApplicantInfo>();
		if(res == null){
			throw new RuntimeException("Session 값 없음 - 계정 정보 확인");
		}
		
		for(int page = 1 ; page < 2 ; page++){
			Document doc = Jsoup.connect(StringUtil.format(searchingURL, page + "")).cookies(res.cookies()).get();
			Elements elements = doc.select(rowQuery);
			for(int i = 0 ; i < elements.size() ; i++){
				Element ele = elements.get(i);
				if(ele.select(isCheckQuery).size() > 0){continue;}
				String link = ele.select(linkQuery).get(0).attr("href");
				
				String rsm = ele.select(rsmCodeQuery).get(0).attr("value");
				String subValue = ele.select(subValue).get(0).attr("value");
				
				Document infoDoc = Jsoup.connect(link).cookies(res.cookies()).get();
				Elements infoElements = infoDoc.select(baseInfoQuery);
				if(isValidAppl(infoElements)){
					
					
				}
				appList.add(setApplInfo(infoElements));
			}
		}
	}
	public void reqNewInfo()
	public Boolean isValidAppl(Elements eles) throws Exception{
		ApplicantInfo appItem = new ApplicantInfo();
		String name = eles.get(0).select(".lang_spc").get(0).text();
		return name.substring(0, 1).matches("[가-힣]");
	}
	public ApplicantInfo setApplInfo(Elements eles) throws Exception{
		ApplicantInfo appItem = new ApplicantInfo();
		for(int info = 0 ; info < eles.size() ; info++){
			Element infoEle = eles.get(info);
			switch(info){
			case 0:
				appItem.name = infoEle.select(".lang_spc").get(0).text();
				break;
			case 1:
				String[] temp = infoEle.select(".tb_txt_def").get(0).text().replaceAll(" ", "").split("/");
				appItem.gender = temp[0];
				appItem.birth = temp[1]; 
				break;
			case 2:
				appItem.address = infoEle.select(".line-block").get(0).text();
				break;
			case 3:
				appItem.email = infoEle.select(".tb_txt_def").get(0).text();
				break;
			case 4:
				appItem.phone = infoEle.select(".tb_txt_def").get(0).text();
				break;
			}
		}
		return appItem;
	}
}

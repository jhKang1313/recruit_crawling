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
	//private final String searchingURL = "https://resumedb.incruit.com/list/searchresume.asp?SearchType=MAIN&birth2=1991&sex=1,%202&birth1=1997&rgn1=149&rgn2=11&mobileopenyn=Y&hropen=2&page={0}#list";
	private final String newReqURL = "https://resumedb.incruit.com/info/Resume_AJAX.asp"; 
	
	private final String linkQuery = "th .txt-title a";
	private final String rowQuery = ".recruiter_list_warp table tr";
	private final String isCheckQuery = "span.contact-check";
	private final String baseInfoQuery = "div.my_profile_table_main table tr td";
	
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
	public List<ApplicantInfo> readPage(Response res) {
		List<ApplicantInfo> applList = new ArrayList<ApplicantInfo>();
		try{
			
			if(res == null){
				throw new RuntimeException("Session 값 없음 - 계정 정보 확인");
			}
			MyLogger.log("로그인 성공");
			for(int page = 1 ; page <= 10 ; page++){
				Document doc = Jsoup.connect(StringUtil.format(searchingURL, page + "")).cookies(res.cookies()).get();
				MyLogger.log(StringUtil.format("{0} 페이지를 가져옵니다.", page + ""));
				Elements applicantElements = doc.select(rowQuery);
				
				for(Element applEle : applicantElements){
					MyLogger.log(page + "페이지의 " + applicantElements.size() + "개 중" + applicantElements.indexOf(applEle) + "개 가져왔습니다.");
					//이미 확인된 건은 넘어간다.
					if(applEle.select(isCheckQuery).size() > 0){continue;}
					
					//링크 가져오기
					
					String applDtlLink = applEle.select(linkQuery).get(0).attr("href");
					MyLogger.log(applDtlLink + " 링크를 가져왔습니다.");
					//링크 들어가서
					Document applDtlInfoDoc = Jsoup.connect(applDtlLink).cookies(res.cookies()).get();
					Elements applDtlBaseInfoEles = applDtlInfoDoc.select(baseInfoQuery);
					//이게 유효하면
					if(isValidAppl(applDtlBaseInfoEles)){
						MyLogger.log(applDtlLink + " => 유효한 이름입니다. 열람 요청합니다.");
						// 열람 요청
						if(requestNewApplInfo(applEle, res) != null){
							applDtlInfoDoc = Jsoup.connect(applDtlLink).cookies(res.cookies()).get();
							applDtlBaseInfoEles = applDtlInfoDoc.select(baseInfoQuery);
							ApplicantInfo appliInfo = setApplInfo(applDtlBaseInfoEles);
							appliInfo.url = applDtlLink;
							applList.add(appliInfo);
							MyLogger.log(appliInfo.name + " => 가져오기 성공");
						}
					} else {
						MyLogger.log(applDtlLink + " => 유효하지 않은 이름입니다. 넘어갑니다.");
					}
				}
			}
		}catch(Exception e){
			MyLogger.log("오류 발생 ==> 연람한 곳까지 파일을 작성");
			return applList;
		}
		return applList;
	}
	public Response requestNewApplInfo(Element ele, Response res) throws Exception{
		String rsm = ele.nextElementSibling().id().split("_")[1];
		String subValue = ele.nextElementSibling().attr("value");
		Response response = (Response) Jsoup.connect(newReqURL)
				.data("rsm", rsm)
				.data("CntYn", "Y")
				.data("eLevelSubCd", subValue)
				.cookies(res.cookies())
				.method(Method.POST).execute();
		return response;
	}
	public String getAttr(Element ele, String query, String attr) throws Exception{
		Elements eles = ele.select(query);
		return eles.size() > 0 ? eles.get(0).attr(attr) : null;
	}
	public void reqNewInfo(){
		
	}
	public Boolean isValidAppl(Elements eles) throws Exception{
		String name = eles.get(0).select(".lang_spc").get(0).text();
		return name.substring(0, 1).matches("[가-힣]");
	}
	public String getText(Element ele, String query) throws Exception{
		Elements eles = ele.select(query);
		return eles.size() > 0 ? eles.get(0).text().replaceAll(",", " ") : "";
		
	}
	public ApplicantInfo setApplInfo(Elements eles) throws Exception{
		ApplicantInfo appItem = new ApplicantInfo();
		for(int info = 0 ; info < eles.size() ; info++){
			Element infoEle = eles.get(info);
			switch(info){
			case 0:
				appItem.name = getText(infoEle, ".lang_spc");
				break;
			case 1:
				String[] temp = getText(infoEle, ".tb_txt_def").split("/");
				appItem.gender = temp[0];
				appItem.birth = temp[1]; 
				break;
			case 2:
				appItem.address = getText(infoEle, ".line-block");
				break;
			case 3:
				appItem.email = getText(infoEle, ".tb_txt_def");
				break;
			case 4:
				appItem.phone = getText(infoEle, ".tb_txt_def");
				break;
			}
		}
		return appItem;
	}
}

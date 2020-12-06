package recruit.crawling;

public class App {
    public static void main( String[] args ) throws Exception{
    	RecruitSite site = new IncruitRecruitSite();
    	
    			
    	site.readPage(site.login("backersm", "Qqaazz1!"));
    	
    	
    }
}

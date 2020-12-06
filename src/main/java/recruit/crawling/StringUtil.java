package recruit.crawling;

public class StringUtil {
	public static String format(String sourceString, String...strings){
		for(int i = 0 ; i < strings.length ; i++){
			sourceString = sourceString.replaceAll("\\{" + i + "\\}" , strings[i]);
		}
		return sourceString;
	}
}

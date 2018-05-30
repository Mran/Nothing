import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Matchs {
    public static void main(String[] args) {
        String str = "#%fade[topic]#!";
        Pattern pattern = Pattern.compile("#%[^#%\\[\\]]{1,}(\\[topic\\])#%");
        Matcher matcher = pattern.matcher("#fade[topic]#");
        System.out.println(matcher.find());
        System.out.println(matcher.group());
    }
}

import java.util.ArrayList;

public class Times {
    public static void main(String[] args) {
        String content = "#测试话题1[topic]#带#带#测试话题1[topic]#";
        for (int i = 0; i < content.length(); ) {
            i += getItem(content.substring(i));
        }

    }

    static ArrayList<String> strings = new ArrayList<>();

    static int getItem(String content) {
        int first = content.indexOf('#');
        int next = 0;
        if (first == -1) {
            strings.add(content);
            System.out.println(content);
            return content.length();
        } else if (first != 0) {
            strings.add(content.substring(0, first ));
            System.out.println(content.substring(0, first ));
            return first;
        } else {
            next = content.indexOf('#', first + 1);
            if (content.substring(first, next).contains("[topic]") || content.substring(first, next).contains("[product]") || content.substring(first, next).contains("[at]")) {
                strings.add(content.substring(first, next + 1));
                System.out.println(content.substring(first, next + 1));
                return next-first+1;
            }else {
                strings.add(content.substring(first, next+1));
                return  next-first;
            }
        }

    }
}

package etl.dispatch.script.util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 求在M中找出N的排列数
 * 算法思想：递归
 *  eg:abcd的全排列结果分别为：a,b,c,d,ab,ac,ad,bc,bd,cd,abc,abd,acd,bcd,abcd
 *  可以看出，当求N位的组合数时，可以先固定前N-1位，然后在匹配最后一位可行值；以此类推可用递归的方法求出所有可能的值。
 *   
 * @author YHYR
 *
 */

public class Combine {
	
	private static String[] is = new String[] { "`app_version_id`", "`channel_id`", "`app_plat_id`", "`interaction_view_id`"};
    private static int total;
    private static int m = 4;
    static List<String> listSet = new ArrayList<>();
    public static void main(String[] args) {
        plzh(listSet,"",is, new ArrayList<Integer>(), m);
        System.out.println("total : " + total);
        for(String strSet: listSet){
        	 System.out.println(strSet);
        }
    }
    
    public static void plzh(List<String> listSet, String strSet, String[] is,  List<Integer> iL, int m) {
        if(m == 0) {
            listSet.add(strSet);
            return;
        }
        List<Integer> iL2;
        for(int i = 0; i < is.length; i++) {
            iL2 = new ArrayList<Integer>();
            iL2.addAll(iL);
            if(!iL.contains(i)) {
                String str = strSet +","+ is[i];
                iL2.add(i);
                plzh(listSet, str, is, iL2, m-1);
            }
        }
    }
	
}

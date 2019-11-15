import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Test {
	
	    private static String[] is = new String[] { "1", "2", "3", "4"};
	    private static int total;
	    private static int m = 4;
	    static List<String> listSet = new ArrayList<>();
	    public static void main(String[] args) {
	        List<Integer> iL = new ArrayList<Integer>();
	        new Test().plzh(listSet,"", iL,  m);
	        System.out.println("total : " + total);
	        for(String strSet: listSet){
	        	 System.out.println("strSet : " + strSet);
	        }
	    }
	    private void plzh(List<String> listSet, String strSet, List<Integer> iL, int m) {
	        if(m == 0) {
	            listSet.add(strSet);
	            total++;
	            return;
	        }
	        List<Integer> iL2;
	        for(int i = 0; i < is.length; i++) {
	            iL2 = new ArrayList<Integer>();
	            iL2.addAll(iL);
	            if(!iL.contains(i)) {
	                String str = strSet +";"+ is[i];
	                iL2.add(i);
	                plzh(listSet, str, iL2, m-1);
	            }
	        }
	    }
}
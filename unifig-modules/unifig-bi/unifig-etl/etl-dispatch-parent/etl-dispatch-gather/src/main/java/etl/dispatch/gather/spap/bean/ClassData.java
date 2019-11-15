package etl.dispatch.gather.spap.bean;

import java.io.Serializable;
import java.util.List;

public class ClassData implements Serializable {

	  private int next;
      private List<DataList> list;

      public int getNext() {
          return next;
      }

      public void setNext(int next) {
          this.next = next;
      }

      public List<DataList> getList() {
          return list;
      }

      public void setList(List<DataList> list) {
          this.list = list;
      }
}

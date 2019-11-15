package etl.dispatch.gather.spap.bean;

import java.io.Serializable;
import java.util.List;

public class RegisterList implements Serializable {

	private static final long serialVersionUID = 1L;
	private int next;
	private List<RegisterBean> list;

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public List<RegisterBean> getList() {
		return list;
	}

	public void setList(List<RegisterBean> list) {
		this.list = list;
	}

}

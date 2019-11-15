package etl.dispatch.boot.response;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;

public class ResultPage  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8715690182822472139L;

	/* 总数 */
    private int total;

    /* 每页显示条数，默认 10 */
    private int size;

    /* 总页数 */
    private int pages;

    /* 当前页 */
    private int current;
    
    private List<?> result;
    public ResultPage(Page<?> page){
    	this.total=(int)page.getTotal();
    	this.size=page.getSize();
    	this.pages=(int)page.getPages();
    	this.current=page.getCurrent();
    	this.result=page.getRecords();
    }


	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public List<?> getResult() {
		return result;
	}

	public void setResult(List<?> result) {
		this.result = result;
	}
    

}

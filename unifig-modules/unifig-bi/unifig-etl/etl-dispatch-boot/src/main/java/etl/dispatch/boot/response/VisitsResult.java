package etl.dispatch.boot.response;

import java.io.Serializable;

public class VisitsResult implements Serializable {
	private static final long serialVersionUID = 94864736703726013L;
	private String adoptToken;
	private Object result;
	

	public VisitsResult() {

	}

	public VisitsResult(Object result) {
		this.result = result;
	}


	public VisitsResult(String adoptToken, Object result) {
		super();
		this.adoptToken = adoptToken;
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getAdoptToken() {
		return adoptToken;
	}

	public void setAdoptToken(String adoptToken) {
		this.adoptToken = adoptToken;
	}


}

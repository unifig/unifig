package etl.dispatch.boot.response;

import java.io.Serializable;

/**
 *
 *
 */
public class ResponseCommand implements Serializable {
	private static final long serialVersionUID = 6700648664405181335L;
	public final static int STATUS_SUCCESS = 0;
	public final static int STATUS_ERROR = 1;
	public final static int STATUS_LOGIN_ERROR = 2;
	
	private int status;
	private Serializable result;

	public ResponseCommand(int status, Serializable result) {
		this.status = status;
		this.result = result;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Serializable getResult() {
		return result;
	}

	public void setResult(Serializable result) {
		this.result = result;
	}

}

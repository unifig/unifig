package etl.dispatch.boot.controller.error;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.util.NewMapUtil;

@RestController
@RequestMapping(value = "/error")
public class ErrorLogin {
	
		@GetMapping("nottoken")
		public Object loginErr(){
			return new ResponseCommand(ResponseCommand.STATUS_LOGIN_ERROR, new VisitsResult(new NewMapUtil("message", "Request token is not invalid, Please login again to get the new token ").get()));
		}
}

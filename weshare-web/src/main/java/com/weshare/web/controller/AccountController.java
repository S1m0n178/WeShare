package com.weshare.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weshare.component.RedisComponent;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.query.UserInfoQuery;
import com.weshare.entity.po.UserInfo;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.exception.BusinessException;
import com.weshare.redis.RedisUtils;
import com.weshare.service.UserInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户信息 Controller
 */
@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController{
@Resource
private UserInfoService userInfoService;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private RedisUtils redisUtils;

	@RequestMapping("/checkCode")
	public ResponseVO checkCode(){
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(100,42);
		String code = captcha.text();
		String checkCodeKey = redisComponent.saveCheckCode(code);
		String checkCodeBase64 = captcha.toBase64();

		Map<String,String>result = new HashMap<>();
		result.put("checkCode",checkCodeBase64);
		result.put("checkCodeKey",checkCodeKey);
		return getSuccessResponseVO(result);
	}
	@RequestMapping("/register")
	public ResponseVO register(@NotEmpty @Email @Size(max = 150) String email,
							   @NotEmpty @Size(max = 20) String nickName,
							   @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD) String registerPassword,
							   @NotEmpty String checkCodeKey,
							   @NotEmpty String checkCode){
		try{
			if(!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
				throw new BusinessException("图片验证码不正确");
			}
		    userInfoService.register(email,nickName,registerPassword);
			return getSuccessResponseVO(null);
		}finally {
			redisComponent.cleanCheckCode(checkCodeKey);
		}
	}

}
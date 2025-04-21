package com.weshare.admin.controller;

import com.weshare.component.RedisComponent;
import com.weshare.config.AppConfig;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.dto.TokenUserInfoDto;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.exception.BusinessException;
import com.weshare.redis.RedisUtils;
import com.weshare.service.UserInfoService;
import com.weshare.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息 Controller
 */
@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private AppConfig appConfig;


	@RequestMapping("/checkCode")
	public ResponseVO checkCode() {
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
		String code = captcha.text();
		String checkCodeKey = redisComponent.saveCheckCode(code);
		String checkCodeBase64 = captcha.toBase64();

		Map<String, String> result = new HashMap<>();
		result.put("checkCode", checkCodeBase64);
		result.put("checkCodeKey", checkCodeKey);
		return getSuccessResponseVO(result);
	}

	@RequestMapping("/login")
	public ResponseVO login(HttpServletRequest request,
							HttpServletResponse response,
							@NotEmpty String account,
							@NotEmpty String password,
							@NotEmpty String checkCodeKey,
							@NotEmpty String checkCode) {
		try {
			if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
				throw new BusinessException("图片验证码不正确");
			}
			String tmp = StringTools.encodeByMd5("admin123");
			if(!account.equals(appConfig.getAdminAccount())|| !password.equals(StringTools.encodeByMd5(appConfig.getAdminPassword()))){
				throw new BusinessException("账号密码错误");
			}
			String token = redisComponent.saveTokenInfo4Admin(account);
			saveToken2Cookie(response,token);
			return getSuccessResponseVO(account);
		} finally {
			redisComponent.cleanCheckCode(checkCodeKey);

			Cookie[] cookies = request.getCookies();
			//删除多余token
			if(cookies!=null) {
				String token = null;
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(Constants.TOKEN_ADMIN)) {
						token = cookie.getValue();
					}
				}
				if (!StringTools.isEmpty(token)) {
					redisComponent.cleanToken4Admin(token);
				}
			}

		}
	}

	@RequestMapping("/logout")
	public ResponseVO logout(HttpServletResponse response) {
		cleanCookie(response);
		return getSuccessResponseVO(null);
	}


}
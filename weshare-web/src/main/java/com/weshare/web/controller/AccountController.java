package com.weshare.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weshare.component.RedisComponent;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.dto.TokenUserInfoDto;
import com.weshare.entity.query.UserInfoQuery;
import com.weshare.entity.po.UserInfo;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.exception.BusinessException;
import com.weshare.redis.RedisUtils;
import com.weshare.service.UserInfoService;
import com.weshare.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class AccountController extends ABaseController {
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private RedisUtils redisUtils;

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

	@RequestMapping("/register")
	public ResponseVO register(@NotEmpty @Email @Size(max = 150) String email,
							   @NotEmpty @Size(max = 20) String nickName,
							   @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD) String registerPassword,
							   @NotEmpty String checkCodeKey,
							   @NotEmpty String checkCode) {
		try {
			if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
				throw new BusinessException("图片验证码不正确");
			}
			userInfoService.register(email, nickName, registerPassword);
			return getSuccessResponseVO(null);
		} finally {
			redisComponent.cleanCheckCode(checkCodeKey);
		}
	}

	@RequestMapping("/login")
	public ResponseVO login(HttpServletRequest request,
							HttpServletResponse response,
							@NotEmpty @Email String email,
							@NotEmpty String password,
							@NotEmpty String checkCodeKey,
							@NotEmpty String checkCode) {
		try {
			if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
				throw new BusinessException("图片验证码不正确");
			}
			String ip = getIpAddr();
			TokenUserInfoDto tokenUserInfoDto = userInfoService.login(email, password, ip);

			saveToken2Cookie(response, tokenUserInfoDto.getToken());
			//TODO 设置粉丝数 关注数 硬币数




			return getSuccessResponseVO(tokenUserInfoDto);
		} finally {
			redisComponent.cleanCheckCode(checkCodeKey);

			//删除多余token
			Cookie[] cookies = request.getCookies();
			if(cookies!=null) {
			String token = null;
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(Constants.TOKEN_WEB)) {
					token = cookie.getValue();
				}
			}
			if (!StringTools.isEmpty(token)) {
				redisComponent.cleanToken(token);
			}
			}
		}
	}

	@RequestMapping("/autoLogin")
	public ResponseVO autoLogin(HttpServletResponse response) {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
		if(tokenUserInfoDto==null){
			return getSuccessResponseVO(null);
		}
		if(tokenUserInfoDto.getExpireAt()-System.currentTimeMillis()<Constants.REDIS_KEY_EXPIRES_ONE_DAY){
			redisComponent.saveTokenInfo(tokenUserInfoDto);
			saveToken2Cookie(response, tokenUserInfoDto.getToken());
		}
		saveToken2Cookie(response, tokenUserInfoDto.getToken());
		//TODO 设置粉丝数 关注数 硬币数
		return getSuccessResponseVO(tokenUserInfoDto);
	}
	@RequestMapping("/logout")
	public ResponseVO logout(HttpServletResponse response) {
		cleanCookie(response);
		return getSuccessResponseVO(null);
	}


}
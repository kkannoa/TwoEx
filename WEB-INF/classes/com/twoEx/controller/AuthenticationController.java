package com.twoEx.controller;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.twoEx.bean.BuyerAccessLogBean;
import com.twoEx.bean.BuyerBean;
import com.twoEx.bean.SellerBean;
import com.twoEx.service.KakaoAuthentication;
import com.twoEx.service.SellerAuthentication;
import com.twoEx.utils.Encryption;
import com.twoEx.utils.ProjectUtils;



@Controller
public class AuthenticationController {
	@Autowired
	private KakaoAuthentication kakaoService;
	@Autowired
	private SqlSessionTemplate session;
	@Autowired
	private ProjectUtils pu;
	@Autowired
	private SellerAuthentication auth;
	@Autowired
	private Encryption enc;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {

		System.out.println("on");
		return "mainPage";
	}

	@RequestMapping(value = "/sellerLogOut", method = RequestMethod.POST)
	public ModelAndView sellerLogOut(ModelAndView mav) {
		System.out.println("sellerLogOut");
		this.auth.backController("sellerLogOut", mav);
		return mav;
	}

	@RequestMapping(value = "/sellerLogIn", method = RequestMethod.POST)
	public ModelAndView sellerLogIn(ModelAndView mav, @ModelAttribute SellerBean sb) {
		System.out.println("sellerLogIn");
		mav.addObject(sb);
		this.auth.backController("sellerLogIn", mav);
		return mav;
	}

	@RequestMapping(value = "/insertSeller", method = RequestMethod.POST)
	public ModelAndView insertSeller(ModelAndView mav, @ModelAttribute SellerBean sb) {
		System.out.println("insertSeller");
		mav.addObject(sb);
		this.auth.backController("insertSeller", mav);
		return mav;
	}

	@RequestMapping(value = "/moveSellerJoin", method = RequestMethod.POST)
	public ModelAndView moveSellerJoin(ModelAndView mav) {
		System.out.println("moveSellerJoin");
		this.auth.backController("moveSellerJoin", mav);
		return mav;
	}

	@RequestMapping(value = "/moveSelectJoin", method = RequestMethod.POST)
	public ModelAndView moveSelectJoin(ModelAndView mav) {
		System.out.println("moveSelectJoin");
		this.auth.backController("moveSelectJoin", mav);
		return mav;
	}

	@RequestMapping(value = "/insertKakao", method = RequestMethod.POST)
	public ModelAndView insertKakao(ModelAndView mav, @ModelAttribute BuyerBean bb) {
		System.out.println("insertKakao");
		mav.addObject(bb);
		this.kakaoService.backController("insertKakao", mav);
		return mav;
	}

	@RequestMapping(value="/kakaoLogIn")
	public String kakaoLogin() {
		StringBuffer loginUrl = new StringBuffer();
		loginUrl.append("https://kauth.kakao.com/oauth/authorize?client_id=");
		loginUrl.append("628883e2413da0a633c8d3b53d91b08a"); 
		loginUrl.append("&redirect_uri=");
		loginUrl.append("http://192.168.0.133/kakao_callback");
		/* ?????? ??? ???????????? ????????? ???????????? */
		/* https://developers.kakao.com/ */
		/* root-context db ????????? ???????????? ???????????? */
		/* "http://errorkillers.hoonzzang.com:20000/kakao_callback"
		 * ????????? http://192.168.0.133/kakao_callback 628883e2413da0a633c8d3b53d91b08a  
		 * ????????? 192.168.0.253 fe5c5e3b0473b8b623da3bc05a6ba479 172.30.1.47
		 * ????????? 192.168.0.9 67e077c6ce4c4c3ed58fc83d3a3a79c4
		 * ????????? 192.168.0.165  2f2662c79b2457f82f5e188d75b827ac
		 * ????????? 192.168.1.47 51a9dd8bb7a96129eb36d1848ea0bd73  
		 * ?????????
		 * */
		loginUrl.append("&response_type=code");
		return "redirect:"+loginUrl.toString();
	}

	@Transactional
	@RequestMapping(value = "/kakao_callback", method = RequestMethod.GET)
	public String redirectkakao(@RequestParam String code, HttpSession session) throws IOException {
		//???????????? ??????
		if(!isSession()) {
			System.out.println(code);
			
			//???????????? get
			String access_Token = kakaoService.getReturnAccessToken(code);

			//????????? ?????? get
			Map<String,Object> userInfo = kakaoService.getUserInfo(access_Token);
			System.out.println("???????????? ??????"+userInfo.get("nickname")+userInfo.get("profile_image"));
			System.out.println("-----------logIn");
			System.out.println(access_Token);
			System.out.println(userInfo.get("id"));

			//??? ?????? 
			BuyerBean buy = new BuyerBean();
			BuyerAccessLogBean bal = new BuyerAccessLogBean();
			buy.setBuyCode((String)userInfo.get("id"));
			buy.setBuyNickname((String)userInfo.get("nickname"));
			buy.setBuyProfile((String)userInfo.get("profile_image"));
			buy.setUserType("buyer");
			System.out.println("-------------------------check");


			System.out.println(buy.getBuyCode());
			System.out.println(buy.getBuyNickname());
			System.out.println(buy.getBuyProfile());

			bal.setBuyerAccessLogCode((String)userInfo.get("id"));     		

			//???????????? ??????
			if(this.session.selectOne("isBuyer", buy) != null) {
				System.out.println("isBuyer=true");

				//????????? ????????? ?????? ??????. ?????? ?????? ?????? ?????????.
				/*
				 * 1. jsp ??????
				 * 2. ????????? ?????? ??????????????? ?????? ????????? ????????? ???????????? db??? insert
				 * 
				 */

				//?????????????????? ??????
				try {
					if(convertToBool(this.session.selectOne("isBuyerAccess", bal))) {
						//??????????????????
						bal.setBuyerAccessLogAction(-1);
						if(convertToBool(this.session.insert("insBuyerAccessLog", bal))) {
							System.out.println("???????????? ?????? ??????");
						}
					} 
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("noAccessLogFound");
				}

				//???????????? ?????? 
				bal.setBuyerAccessLogAction(1);
				if(convertToBool(this.session.insert("insBuyerAccessLog", bal))) {
					System.out.println("????????? ?????? ??????");	
					//????????? ?????? ????????????
					try {
						buy.setBuyNickname(enc.aesEncode(buy.getBuyNickname(), buy.getBuyCode()));
						buy.setBuyProfile(enc.aesEncode(buy.getBuyProfile(), buy.getBuyCode()));
					} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException
							| NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
							| BadPaddingException e1) {e1.printStackTrace();}
					this.session.update("updateBuyerInfo", buy);
					//?????? (??????) ?????? --> pu
					try {
						BuyerBean bd = (BuyerBean) this.session.selectList("getBuyerAccessInfo", buy).get(0);
						bd.setBuyEmail(enc.aesDecode(bd.getBuyEmail(), bd.getBuyCode()));
						bd.setBuyRegion(enc.aesDecode(bd.getBuyRegion(), bd.getBuyCode()));
						bd.setBuyNickname(enc.aesDecode(bd.getBuyNickname(), bd.getBuyCode()));
						bd.setBuyProfile(enc.aesDecode(bd.getBuyProfile(), bd.getBuyCode()));
						bd.setUserType("buyer");
						String accessInfo = new Gson().toJson(bd);
						pu.setAttribute("accessInfo", accessInfo);
						System.out.println("if: " + pu.getAttribute("accessInfo"));
					} catch (Exception e) {e.printStackTrace();}
				}
			} else {
				try {
					System.out.println("--------------joinkakao----check");

					System.out.println(buy.getBuyCode());
					System.out.println(buy.getBuyNickname());
					System.out.println(buy.getBuyProfile());
					//???????????? ?????? ?????? ?????? ?????? ??????
					pu.setAttribute("accessInfo", buy);
					System.out.println("else:" + pu.getAttribute("accessInfo"));

					//?????? ?????? ?????? ????????? ??????
					return "kakaoJoin";

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			/*------------------------------*/

			if(userInfo.get("nickname") !=null) {
				session.setAttribute("userId", userInfo.get("nickname"));
				session.setAttribute("access_Token", access_Token);
			}
		}

		return "mainPage";
	}
	
	@Transactional
	@RequestMapping(value="/kakaoLogOut")
	public String logout(ModelMap modelMap, HttpSession session)throws IOException {
		//??????????????????
		if(isSession()) {
			String access_Token = (String)session.getAttribute("access_Token");
			System.out.println("-----------logOut");
			System.out.println(access_Token);
			System.out.println((String)session.getAttribute("id"));
			if(access_Token != null && !"".equals(access_Token)){
				kakaoService.getLogout(access_Token);
				session.removeAttribute("access_Token");
				session.removeAttribute("userId");
			}else {
				System.out.println("?????? ????????????");
			}

			try {
				String accessInfo = (String)pu.getAttribute("accessInfo");

				BuyerAccessLogBean bal = new BuyerAccessLogBean();
				JsonParser parser = new JsonParser();
				JsonElement bean = parser.parse(accessInfo);
				String buyCode = bean.getAsJsonObject().get("buyCode").getAsString();
				bal.setBuyerAccessLogCode(buyCode);
				bal.setBuyerAccessLogAction(-1);
				if(convertToBool(this.session.insert("insBuyerAccessLog", bal))) {
					System.out.println("kakao logout insert success");
				}
				pu.removeAttribute("accessInfo");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("????????????");
		}

		return "mainPage";
		//return "redirect:/";
	}

	private boolean convertToBool(int result) {
		return result >= 1 ? true : false;
	}

	boolean isSession() {
		boolean isSession = false;
		try {
			isSession = (this.pu.getAttribute("accessInfo")) != null ? true : false;		
		} catch (Exception e) {e.printStackTrace();}
		return isSession;
	}
}
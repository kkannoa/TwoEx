package com.twoEx.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.twoEx.bean.BuyerBean;
import com.twoEx.bean.ClassroomBean;
import com.twoEx.bean.LocationBean;
import com.twoEx.bean.OrderBean;
import com.twoEx.bean.ProductBean;
import com.twoEx.bean.SalesHistoryBean;
import com.twoEx.bean.SellerBean;
import com.twoEx.inter.MapperInterfaceSJH;
import com.twoEx.utils.Encryption;
import com.twoEx.utils.ProjectUtils;


@Service
public class ProductManagement {
	@Autowired
	private ProjectUtils pu;
	@Autowired
	private SqlSessionTemplate session;
	@Autowired
	private Encryption enc;
	
	public void backController(String serviceCode, ModelAndView mav) {
		try {
			if(this.pu.getAttribute("accessInfo") !=null) {
				switch(serviceCode) {
				case "moveSalesHistory":
					this.moveSalesHistory(mav);
					break;
				case "moveStatistics":
					this.moveStatistics(mav);
					break;
				case "getMaketStatistics":
					this.getMaketStatistics(mav);
					break;		
				default:
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void backController(String serviceCode, Model model) {
		try {
			if(this.pu.getAttribute("accessInfo") !=null) {
				switch(serviceCode) {
				case "getJanMonthsDate":
					this.getJanMonthsDate(model);
					break;
				case "getMarMonthsDate":
					this.getMarMonthsDate(model);
					break;
				case "getJunMonthsDate":
					this.getJunMonthsDate(model);
					break;
				case "getSalesHistory":
					this.getSalesHistory(model);
					break;	


				default:
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getMaketStatistics(ModelAndView mav) {
		ProductBean sel;		
		sel = (ProductBean)mav.getModel().get("productBean");
		
		mav.addObject("selectCte", insCTE(sel,"prdCteCode"));
		mav.addObject("checkedCte",makeSelectCTE(sel,"prdCteCode"));

		int life = session.selectOne("lifeSellSellStatistics", sel);
		int business = session.selectOne("businessSellStatistics", sel);
		int media = session.selectOne("mediaSellStatistics", sel);
		int tech = session.selectOne("techSellStatistics", sel);
		int design = session.selectOne("designSellStatistics", sel);
		int finance = session.selectOne("financeSellStatistics", sel);
		int law = session.selectOne("lawSellStatistics", sel);
		int hobby = session.selectOne("hobbySellStatistics", sel);
		
	
		int male = session.selectOne("sellMaleStatistics",sel);
		int Female = session.selectOne("sellFemaleStatistics",sel);
		int month = session.selectOne("sellMonthStatistics",sel);
		int month2 = session.selectOne("sellMonth2Statistics",sel);
		
		
		mav.addObject("lifeSellSellStatistics", life);
		mav.addObject("businessSellStatistics", business);
		mav.addObject("mediaSellStatistics", media);
		mav.addObject("techSellStatistics", tech);
		mav.addObject("designSellStatistics", design);
		mav.addObject("financeSellStatistics", finance);
		mav.addObject("lawSellStatistics", law);
		mav.addObject("hobbySellStatistics", hobby);
		
		
		int age10 = session.selectOne("age10MarketSell", sel);
		int age20 = session.selectOne("age20MarketSell", sel);
		int age30 = session.selectOne("age30MarketSell", sel);
		int age40 = session.selectOne("age40MarketSell", sel);
		int age50 = session.selectOne("age50MarketSell", sel);
		int age60 = session.selectOne("age60MarketSell", sel);
		
		mav.addObject("age10MarketSell", age10);
		mav.addObject("age20MarketSell", age20);
		mav.addObject("age30MarketSell", age30);
		mav.addObject("age40MarketSell", age40);
		mav.addObject("age50MarketSell", age50);
		mav.addObject("age60MarketSell", age60);
		
		mav.addObject("sellMaleStatistics", male);
		mav.addObject("sellFemaleStatistics", Female);
		
	
		mav.addObject("sellMonthStatistics", month);
		mav.addObject("sellMonth2Statistics", month2);
		
		mav.setViewName("myShop/getMarketStatistics");
		
		
	}

	

	//???????????? ????????? ??????
		private void getSalesHistory(Model model) {
			String accessInfo = null;
			SellerBean sel = new SellerBean();
			SalesHistoryBean shb = new SalesHistoryBean();
			System.out.println(sel.getOrdFromDate());
			List<SalesHistoryBean> shbList = new ArrayList<SalesHistoryBean>();
			sel.setOrdFromDate(((SellerBean)model.getAttribute("sellerBean")).getOrdFromDate());
			sel.setOrdToDate(((SellerBean)model.getAttribute("sellerBean")).getOrdToDate());
			
			//????????? ?????? ????????? ????????? ?????? ??????
			try {
				accessInfo = (String)pu.getAttribute("accessInfo");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        JsonParser parser = new JsonParser();
	        JsonElement bean = parser.parse(accessInfo);
	        sel.setSelCode(bean.getAsJsonObject().get("selCode").getAsString());
	        shbList = this.session.selectList("getSalesHistoryList", sel);
	        System.out.println("?????????????????????"+shbList);
	        
	        model.addAttribute("getSalesHistoryList", shbList);
		}


		//1???????????? ??????????????? ??????
		private void getJanMonthsDate(Model model) {
			String accessInfo = null;
			SellerBean sel = new SellerBean();
			SalesHistoryBean shb = new SalesHistoryBean();
			List<SalesHistoryBean> shbList = new ArrayList<SalesHistoryBean>();
			//????????? ?????? ????????? ????????? ?????? ??????
			try {
				accessInfo = (String)pu.getAttribute("accessInfo");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        JsonParser parser = new JsonParser();
	        JsonElement bean = parser.parse(accessInfo);
	        sel.setSelCode(bean.getAsJsonObject().get("selCode").getAsString());
	        shbList = this.session.selectList("getJanMonthsSalesList", sel);
	        System.out.println("?????????????????????"+shbList);
	        model.addAttribute("getJanMonthsSalesList", shbList);
		}
		
		//3???????????? ??????????????? ??????
		private void getMarMonthsDate(Model model) {
			String accessInfo = null;
			SellerBean sel = new SellerBean();
			SalesHistoryBean shb = new SalesHistoryBean();
			List<SalesHistoryBean> shbList = new ArrayList<SalesHistoryBean>();
			//????????? ?????? ????????? ????????? ?????? ??????
			try {
				accessInfo = (String)pu.getAttribute("accessInfo");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        JsonParser parser = new JsonParser();
	        JsonElement bean = parser.parse(accessInfo);
	        sel.setSelCode(bean.getAsJsonObject().get("selCode").getAsString());
	        shbList = this.session.selectList("getMarMonthsSalesList", sel);
	        System.out.println("?????????????????????"+shbList);
	        model.addAttribute("getMarMonthsSalesList", shbList);
		}
		
		
		//6???????????? ??????????????? ??????
		private void getJunMonthsDate(Model model) {
			String accessInfo = null;
			SellerBean sel = new SellerBean();
			SalesHistoryBean shb = new SalesHistoryBean();
			List<SalesHistoryBean> shbList = new ArrayList<SalesHistoryBean>();
			//????????? ?????? ????????? ????????? ?????? ??????
			try {
				accessInfo = (String)pu.getAttribute("accessInfo");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        JsonParser parser = new JsonParser();
	        JsonElement bean = parser.parse(accessInfo);
	        sel.setSelCode(bean.getAsJsonObject().get("selCode").getAsString());
	        shbList = this.session.selectList("getJunMonthsSalesList", sel);
	        System.out.println("?????????????????????"+shbList);
	        model.addAttribute("getJunMonthsSalesList", shbList);
		}

	//???????????? ????????? ??????
	private void moveSalesHistory(ModelAndView mav) {
		
		
        mav.addObject("getMoveSalesHistoryList", this.SalesHistoryMakeHTML(mav));
        //??????????????????????????? ????????? 
        OrderBean ord=(OrderBean)mav.getModel().get("orderBean");
        ProductBean prd=(ProductBean)mav.getModel().get("productBean");
       
        
        mav.addObject("selectCte", insCTE(prd,"prdCteCode"));
		mav.addObject("checkedCte",makeSelectCTE(prd,"prdCteCode"));
        
		mav.setViewName("myShop/salesHistory");
	}

	//?????? ?????? ??????????????? ??????
	private String SalesHistoryMakeHTML(ModelAndView mav) {
		StringBuffer sb = new StringBuffer();
		String accessInfo = null;
		SellerBean sel = new SellerBean();
		SalesHistoryBean shb = new SalesHistoryBean();
		
		shb = (SalesHistoryBean)mav.getModel().get("salesHistoryBean");
		List<SalesHistoryBean> shbList = new ArrayList<SalesHistoryBean>();
		
		//????????? ?????? ????????? ????????? ?????? ??????
		try {
			accessInfo = (String)pu.getAttribute("accessInfo");
		} catch (Exception e) {
			e.printStackTrace();
		}
        JsonParser parser = new JsonParser();
        JsonElement bean = parser.parse(accessInfo);
        sel.setSelCode(bean.getAsJsonObject().get("selCode").getAsString());
        System.out.println(sel);
		try {
			shbList = this.session.selectList("getMoveSalesHistoryList", sel);
			System.out.println(shbList);
			if(shbList.size() > 0) {
				int idx = 0;
				for(SalesHistoryBean sal : shbList) {
					idx++;
					sb.append("<div class=\"product__item item1\" onclick=\"moveProductInfo(\'"+ sal.getOrdPrdCteCode()+"\',\'" + sal.getOrdPrdSelCode()+ "\',\'" + sal.getOrdPrdCode() +"\')\">");
					sb.append("<div class=\"product__image__div\">");
					sb.append("<img class=\"product__image\" src=\""+ sal.getPrfLocation() +"\">");
					sb.append("</div>");
					sb.append("<div class=\"product__title__div\">" + sal.getPrdName() +"</div>");
					sb.append("<div class=\"product__price__div\">");
					sb.append("<span>"+" ?????? ?????? : "+ "</span>");
					sb.append("<span>"+ sal.getPrdPrice() +"</span>");
					sb.append("</div>");
					sb.append("<div class=\"product__etc__div\">");
					sb.append("<span>"+" ?????? ?????? : "+ "</span>");
					sb.append("<span>"+ sal.getOrdDate() +"</span>");
					sb.append("</div>");
					sb.append("</div>");
				}

			}
		}catch(Exception e){e.printStackTrace();}
		return sb.toString();
	}

	//???????????? ????????? ?????? -> 0916 ??????
	private void moveStatistics(ModelAndView mav) {
		System.out.println("moveStatisticsService");
		ProductBean sel;		
		sel = (ProductBean)mav.getModel().get("productBean");
		
		mav.addObject("selectCte", insCTE(sel, "prdCteCode"));
		mav.addObject("checkedCte", makeSelectCTE(sel, "prdCteCode"));
		
		int life = session.selectOne("lifeSell",sel);
		int business = session.selectOne("businessSell", sel);
		int media = session.selectOne("mediaSell", sel);
		int tech = session.selectOne("techSell", sel);
		int design = session.selectOne("designSell", sel);
		int finance = session.selectOne("financeSell", sel);
		int law = session.selectOne("lawSell", sel);
		int hobby = session.selectOne("hobbySell", sel);
		
		mav.addObject("lifeSell", life);
		mav.addObject("businessSell", business);
		mav.addObject("mediaSell", media);
		mav.addObject("techSell", tech);
		mav.addObject("designSell", design);
		mav.addObject("financeSell", finance);
		mav.addObject("lawSell", law);
		mav.addObject("hobbySell", hobby);
		
		
		int age10 = session.selectOne("age10", sel);
		int age20 = session.selectOne("age20", sel);
		int age30 = session.selectOne("age30", sel);
		int age40 = session.selectOne("age40", sel);
		int age50 = session.selectOne("age50", sel);
		int age60 = session.selectOne("age60", sel);
		
		int male = session.selectOne("sellMale",sel);
		int Female = session.selectOne("sellFemale",sel);
		
		int month = session.selectOne("sellMonth",sel);
		int month2 = session.selectOne("sellMonth2",sel);
		
		
		
		mav.addObject("age10", age10);
		mav.addObject("age20", age20);
		mav.addObject("age30", age30);
		mav.addObject("age40", age40);
		mav.addObject("age50", age50);
		mav.addObject("age60", age60);
	
		mav.addObject("male", male);
		mav.addObject("Female", Female);
		
		mav.addObject("month", month);
		mav.addObject("month2", month2);
		
		
	
		
		
		mav.setViewName("myShop/statistics");
	}
	//???????????? ????????? ?????? ???????????? ?????? select ???
		public String makeSelectCTE(ProductBean pb,String objName) {
				StringBuffer sb = new StringBuffer();
				List<ProductBean> aulList = new ArrayList<ProductBean>();
				aulList = this.session.selectList("getCte",pb);
				
				sb.append("<select name='" + objName + "' id='common__select'>");
				for(ProductBean prd : aulList) {
					sb.append("<option class='select__option' value='" + prd.getPrdCteCode() + "'>" + prd.getCteName() +"["+prd.getPrdCteCode()+"]"+"</option>");
				}
				sb.append("</select>");

				return sb.toString();
			} 
		
		//???????????? ???????????? ??????????????? DB????????? ????????????????????? ??????
		private String insCTE(ProductBean pb,String objName) {
			StringBuffer sb = new StringBuffer();
			List<ProductBean> aulList = new ArrayList<ProductBean>();
			
			aulList = this.session.selectList("getJoinCte",pb);
			sb.append("<form name='insCte'>");
			for(ProductBean prd : aulList) {
			
				sb.append("<input type='checkbox' name='prdCteCode' value='"+prd.getPrdCteCode()+"'>"+prd.getCteName()+"</input>");			
			}
			sb.append("</form>");
			return sb.toString();
		} 
	

}
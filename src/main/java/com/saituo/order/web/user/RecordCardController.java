package com.saituo.order.web.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.saituo.order.commons.SessionVariable;
import com.saituo.order.commons.VariableUtils;
import com.saituo.order.service.order.ProductService;
import com.saituo.order.service.order.RecordCardService;
import com.saituo.order.service.user.AddressService;
import com.saituo.order.service.user.UserRecordService;

@Controller
@RequiresAuthentication
@RequestMapping("order/record")
public class RecordCardController {

	@Autowired
	private RecordCardService recordCardService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private UserRecordService userRecordService;

	@Autowired
	private ProductService productService;

	@RequestMapping(value = "addProductToBag/{productId}", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> addProductToBag(@PathVariable("productId") String productId) {

		Map<String, String> mapData = Maps.newHashMap();
		String userId = VariableUtils.typeCast(SessionVariable.getCurrentSessionVariable().getUser().get("id"),
				String.class);
		boolean isAlreayAdded = recordCardService.isAddedIntoBuyCard(userId, productId);
		if (isAlreayAdded) {
			mapData.put("msg", "had");
			return mapData;
		}
		recordCardService.putProductIntoBag(userId, productId);
		mapData.put("msg", "sccuess");
		return mapData;
	}

	@RequestMapping(value = "removeOneProductFromBag/{productId}", method = RequestMethod.GET)
	public String removeOneProductFromBag(@PathVariable("productId") String productId) {

		String userId = VariableUtils.typeCast(SessionVariable.getCurrentSessionVariable().getUser().get("id"),
				String.class);
		recordCardService.removeProductListFromBuyCard(userId, productId);
		return "redirect:/order/record/list";
	}

	@RequestMapping(value = "batchremove", method = RequestMethod.POST)
	public String removeBatchProductFromBag(@RequestParam List<String> productIds) {

		String userId = VariableUtils.typeCast(SessionVariable.getCurrentSessionVariable().getUser().get("id"),
				String.class);
		List<String> productIdList = Lists.newArrayList();
		for (int i = 0; i < productIds.size(); i++) {
			productIdList.add(StringUtils.substringBefore(productIds.get(i), "_"));
		}
		if (productIdList == null || productIdList.size() == 0) {
			return "redirect:/order/buycard/list";
		}
		String[] array = productIdList.toArray(new String[productIdList.size()]);
		recordCardService.removeProductListFromBuyCard(userId, array);
		return "redirect:/order/buycard/list";
	}

	@RequestMapping(value = "get/productconut", method = RequestMethod.POST)
	public @ResponseBody Object getProductCountInBag() {
		String userId = VariableUtils.typeCast(SessionVariable.getCurrentSessionVariable().getUser().get("id"),
				String.class);
		return recordCardService.countProductInBagAboutBuyCard(userId);
	}

	@RequestMapping(value = "list", method = RequestMethod.GET)
	public void getProductList(Model model) {
		String userId = VariableUtils.typeCast(SessionVariable.getCurrentSessionVariable().getUser().get("id"),
				String.class);

		model.addAttribute("customerOrderingList", recordCardService.getProductListFromBag(userId));
		model.addAttribute("addressList", addressService.queryList(userId));
		model.addAttribute("agents", recordCardService.getAgentList(true));
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String saveRecordInfo(@RequestParam Map<String, Object> filter, @RequestParam List<String> productIds,
			@RequestParam List<String> discountPrices, @RequestParam List<String> subscripts,
			@RequestParam List<String> supplys, Model model) {

		List<String> productIdList = Lists.newArrayList();
		List<String> discountPriceList = Lists.newArrayList();
		List<String> subscriptList = Lists.newArrayList();
		List<String> supplyList = Lists.newArrayList();

		for (int i = 0; i < productIds.size(); i++) {
			String productStr = productIds.get(i);
			String productId = StringUtils.substringBefore(productStr, "_");
			Integer index = VariableUtils.typeCast(StringUtils.substringAfter(productStr, "_"), Integer.class);
			productIdList.add(productId);
			discountPriceList.add(discountPrices.get(index));
			subscriptList.add(subscripts.get(index));
			supplyList.add(supplys.get(index));
		}

		String userId = VariableUtils.typeCast(SessionVariable.getCurrentSessionVariable().getUser().get("id"),
				String.class);

		List<String> productOrderList = new ArrayList<String>();
		for (int i = 0; i < productIdList.size(); i++) {
			StringBuilder sb = new StringBuilder(120);
			sb.append(productIdList.get(i)).append("~").append(discountPriceList.get(i)).append("~")
					.append(subscriptList.get(i)).append("~").append(supplyList.get(i));
			productOrderList.add(sb.toString());
		}
		filter.put("productRecordList", productOrderList);
		// productRecordList : 产品编号~订购价格～数量~供应商id
		userRecordService.doCreateUserRecord(filter);
		recordCardService.removeProductListFromBuyCard(userId, productIdList.toArray(new String[productIdList.size()]));
		return "redirect:/order/record/list";
	}

	// @RequiresPermissions("perms[order:record:record_view]")
	// @RequestMapping(value = "record_view", method = RequestMethod.GET)
	// public void getReviewRecordInfoList(PageRequest pageRequest,
	// @RequestParam Map<String, Object> filter,
	// Model model) {
	//
	// String userId =
	// VariableUtils.typeCast(SessionVariable.getCurrentSessionVariable().getUser().get("id"),
	// String.class);
	// filter.put("userId", userId);
	// filter.putAll(pageRequest.getMap());
	// List<UserRecord> userRecordList =
	// userRecordService.getUserRecordList(filter);
	// List<Map<String, Object>> userOrderAndDetailInfoResultList =
	// Lists.newArrayList();
	// int userRecordCount = userRecordService.getUserRecordCount(filter);
	//
	// for (UserRecord userRecord : userRecordList) {
	// String userOrderId = String.valueOf(userRecord.getUserRecordId());
	// Map<String, Object> mapData = Maps.newHashMap();
	// mapData.put("userOrderId", userOrderId);
	// userOrderAndDetailInfoResultList.add(userOrderService.getDeatilOrderInfo(mapData));
	// }
	// Page<Map<String, Object>> page = new Page<Map<String,
	// Object>>(pageRequest, userOrderAndDetailInfoResultList,
	// userOrderCount);
	//
	// model.addAttribute("states",
	// VariableUtils.getVariables(UserOrderingState.class));
	// model.addAttribute("page", page);
	// model.addAttribute("userName",
	// SessionVariable.getCurrentSessionVariable().getUser().get("name"));
	// model.addAttribute("startDate", filter.get("startDate"));
	// model.addAttribute("endDate", filter.get("endDate"));
	// model.addAttribute("userOrderId", filter.get("userOrderId"));
	// }

}

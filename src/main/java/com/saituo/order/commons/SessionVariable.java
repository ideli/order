package com.saituo.order.commons;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.google.common.collect.Lists;
import com.saituo.order.service.account.UnautherizedException;

/**
 * session 变量对象，用于存储 http session 的一些常用属性，如：当前用户等
 *
 * @author maurice
 */
public class SessionVariable implements Serializable {

	private static final long serialVersionUID = -4890524275025276787L;

	public static final String DEFAULT_SESSION_KEY = "session_variable";

	// 当前用户
	private Map<String, Object> user = null;
	// 当前用户的所有资源
	private List<Map<String, Object>> authorizationInfo = Lists.newArrayList();
	// 当前用户的菜单集合
	private List<Map<String, Object>> menusList;
	// 当前用户的角色集合
	private List<String> roleList = Lists.newArrayList();
	// 当前用户所在组
	private Integer groupId = null;
	// 当前用户所在地市
	private Integer areaId;
	// 当前用户是否为内部用户
	private Boolean isInternalUser = false;
	// 当前用户电话
	private String mobile = "";
	/**
	 * session 变量对象
	 *
	 * @param user
	 *            用户实体 Map
	 */
	public SessionVariable(Map<String, Object> user) {
		this.user = user;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	/**
	 * 设置当前用户
	 *
	 * @param user
	 *            用户实体 Map
	 */
	public void setUser(Map<String, Object> user) {
		this.user = user;
	}

	/**
	 * 获取当前用户
	 *
	 * @return 用户实体 Map
	 */
	public Map<String, Object> getUser() {
		return user;
	}

	/**
	 * 获取当前用户拥有的资源集合
	 *
	 * @return 资源集合
	 */
	public List<Map<String, Object>> getAuthorizationInfo() {
		return authorizationInfo;
	}

	/**
	 * 获取当前用户拥有的资源菜单集合
	 *
	 * @return 资源集合
	 */
	public List<Map<String, Object>> getMenusList() {
		return menusList;
	}

	/**
	 * 设置当前用户拥有的资源集合
	 *
	 * @param authorizationInfo
	 *            资源实体 Map 集合
	 */
	public void setAuthorizationInfo(List<Map<String, Object>> authorizationInfo) {
		this.authorizationInfo = authorizationInfo;
	}

	/**
	 * 设置当前用户拥有的菜单集合
	 *
	 * @param menusList
	 *            资源实体 Map 集合
	 */
	public void setMenusList(List<Map<String, Object>> menusList) {
		this.menusList = menusList;
	}

	/**
	 * 获取当前用户的角色集合
	 * 
	 * @return
	 */
	public List<String> getRoleList() {
		return roleList;
	}

	/**
	 * 设置当前用户的角色集合
	 * 
	 * @param roleList
	 */
	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	public Boolean getIsInternalUser() {
		return isInternalUser;
	}

	public void setIsInternalUser(Boolean isInternalUser) {
		this.isInternalUser = isInternalUser;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * 获取当前 session 变量对象
	 *
	 * @return session 变量对象
	 */
	public static SessionVariable getCurrentSessionVariable() {

		Subject subject = SecurityUtils.getSubject();
		Object principal = subject.getPrincipal();
		if ((!subject.isAuthenticated() && !subject.isRemembered()) || principal == null) {
			throw new UnautherizedException("用户未经过认证");
		}
		return VariableUtils.typeCast(principal);
	}
}

package cn.spdb.harrier.api.model;

import java.util.List;

/**
 * 菜单树节点.
 * 
 */
public class TreeMenuNode {
	/** 属性. */
	private TreeMenuNodeAttribute attributes;
	/** 子节点. */
	private List<TreeMenuNode> children;
	/** 源码. */
	private String code;
	/** id. */
	private Integer id;
	/** 父节点. */
	private Integer parentId;
	/** 文本. */
	private String text;
	/** 排序 */
	private Integer menuRankNo;

	public TreeMenuNodeAttribute getAttributes() {
		return attributes;
	}

	public List<TreeMenuNode> getChildren() {
		return children;
	}

	public String getCode() {
		return code;
	}

	public Integer getParentId() {
		return parentId;
	}

	public String getText() {
		return text;
	}

	public void setAttributes(TreeMenuNodeAttribute attributes) {
		this.attributes = attributes;
	}

	public void setChildren(List<TreeMenuNode> children) {
		this.children = children;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getMenuRankNo() {
		return menuRankNo;
	}

	public void setMenuRankNo(Integer menuRankNo) {
		this.menuRankNo = menuRankNo;
	}

	@Override
	public String toString() {
		return "TreeMenuNode [attributes=" + attributes + ", children=" + children + ", code=" + code + ", id=" + id
				+ ", parentId=" + parentId + ", text=" + text + ", menuRankNo=" + menuRankNo + "]";
	}

 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((menuRankNo == null) ? 0 : menuRankNo.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeMenuNode other = (TreeMenuNode) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (menuRankNo == null) {
			if (other.menuRankNo != null)
				return false;
		} else if (!menuRankNo.equals(other.menuRankNo))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}

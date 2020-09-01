package com.fajar.entityprops.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fajar.entityprops.annotation.AdditionalQuestionField;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class EntityProperty implements Serializable {

	static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	/**
	* 
	*/
	private static final long serialVersionUID = 2648801606702528928L;

	private String entityName;
	private String alias;
	private String fieldNames;

	private String idField;
	private int formInputColumn;
	@Builder.Default
	private boolean editable = true;
	@Builder.Default
	private boolean withDetail = false;
	private String detailFieldName;

	private String imageElementsJson;
	private String dateElementsJson;
	private String currencyElementsJson;

	@Builder.Default
	private List<String> dateElements = new ArrayList<String>();
	@Builder.Default
	private List<String> imageElements = new ArrayList<String>();
	@Builder.Default
	private List<String> currencyElements = new ArrayList<String>();
	private List<EntityElement> elements;
	private List<String> fieldNameList;

	private boolean ignoreBaseField;
	private boolean isQuestionare;
	@Setter(value = AccessLevel.NONE)
	private String groupNames;

	public void setElementJsonList() {

		try {
			this.dateElementsJson = OBJECT_MAPPER.writeValueAsString(dateElements);
			this.imageElementsJson = OBJECT_MAPPER.writeValueAsString(imageElements);
			this.currencyElementsJson = OBJECT_MAPPER.writeValueAsString(currencyElements);
		} catch (JsonProcessingException e) { 
			e.printStackTrace();
		}
	}

	public void removeElements(String... fieldNames) throws  Exception {
		if (this.elements == null)
			return;
		for (int i = 0; i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			loop: for (String fName : fieldNameList) {
				if (fieldName.equals(fName)) {
					fieldNameList.remove(fName);
					break loop;
				}
			}
			loop2: for (EntityElement entityElement : this.elements) {
				if (entityElement.getId().equals(fieldName)) {
					this.elements.remove(entityElement);
					break loop2;
				}
			}
		}
		this.fieldNames = OBJECT_MAPPER.writeValueAsString(fieldNameList);
	}

	public void setGroupNames(String[] groupNamesArray) {
		int removedIndex = 0;
		for (int i = 0; i < groupNamesArray.length; i++) {
			if (groupNamesArray[i] == AdditionalQuestionField.DEFAULT_GROUP_NAME) {
				removedIndex = i;
			}
		}
		groupNamesArray = ArrayUtils.remove(groupNamesArray, removedIndex);
		this.groupNames = String.join(",", groupNamesArray);
		groupNames += "," + AdditionalQuestionField.DEFAULT_GROUP_NAME;
	}

//	static void main(String[] args) {
//		args =new String[] {"OO", "ff", "fff22"};
//		for (int i = 0; i < args.length; i++) {
//			if(args[i] == "OO")
//		}
//	}

	public String getGridTemplateColumns() {
		if (formInputColumn == 2) {
			return "20% 70%";
		}
		return StringUtils.repeat("auto ", formInputColumn);
	}

	public void determineIdField() {
		if (null == elements) {
			log.error("Entity ELements is NULL");
			return;
		}
		for (EntityElement entityElement : elements) {
			if (entityElement.isIdField() && getIdField() == null) {
				setIdField(entityElement.getId());
			}
		}
	}

}
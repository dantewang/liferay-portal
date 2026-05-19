/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMFieldsCounter;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldUtil;
import com.liferay.journal.exception.ArticleContentException;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XMLUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 * @author Bruno Basto
 */
@Component(service = JournalConverter.class)
public class JournalConverterImpl implements JournalConverter {

	@Override
	public String getContent(
			DDMStructure ddmStructure, Fields ddmFields, long groupId)
		throws Exception {

		Document document = getDocument(ddmStructure, ddmFields, groupId);

		try {
			return XMLUtil.stripInvalidChars(document.formattedString());
		}
		catch (Exception exception) {
			throw new ArticleContentException(
				"Unable to read content with an XML parser", exception);
		}
	}

	@Override
	public Fields getDDMFields(DDMStructure ddmStructure, String content)
		throws PortalException {

		try {
			if (Validator.isNull(content)) {
				return new Fields();
			}

			Document document = SAXReaderUtil.read(content);

			Fields ddmFields = new Fields();

			Field fieldsDisplayField = new Field(
				ddmStructure.getStructureId(), DDM.FIELDS_DISPLAY_NAME,
				StringPool.BLANK);

			ddmFields.put(fieldsDisplayField);

			DDMForm ddmForm = ddmStructure.getDDMForm();

			Element rootElement = document.getRootElement();

			String[] availableLanguageIds = StringUtil.split(
				rootElement.attributeValue("available-locales"));
			String defaultLanguageId = rootElement.attributeValue(
				"default-locale");

			StringBundler fieldsDisplaySB = new StringBundler();

			for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
				_addDDMFields(
					availableLanguageIds, defaultLanguageId, ddmFields,
					fieldsDisplaySB, ddmFormField, ddmStructure, rootElement,
					rootElement);
			}

			fieldsDisplayField.setValue(fieldsDisplaySB.toString());

			return ddmFields;
		}
		catch (DocumentException documentException) {
			throw new PortalException(documentException);
		}
	}

	@Override
	public Document getDocument(
			DDMStructure ddmStructure, Fields ddmFields, long groupId)
		throws Exception {

		Document document = SAXReaderUtil.createDocument();

		Element rootElement = document.addElement("root");

		rootElement.addAttribute(
			"available-locales", _getAvailableLocales(ddmFields));

		Locale defaultLocale = ddmFields.getDefaultLocale();

		if (!_language.isAvailableLocale(groupId, defaultLocale)) {
			defaultLocale = LocaleUtil.getSiteDefault();
		}

		rootElement.addAttribute(
			"default-locale", LocaleUtil.toLanguageId(defaultLocale));

		rootElement.addAttribute("version", "1.0");

		DDMFieldsCounter ddmFieldsCounter = new DDMFieldsCounter();

		DDMForm ddmForm = ddmStructure.getDDMForm();

		// Split the fields-display value once for the duration of this call.
		// _countFieldRepetition and _getFieldInstanceId would otherwise
		// re-split per leaf, turning getDocument() into O(L^2) in the leaf
		// count.

		Field fieldsDisplayField = ddmFields.get(DDM.FIELDS_DISPLAY_NAME);

		String[] fieldsDisplayValues = null;
		Map<String, List<String>> instanceIdsByFieldName = null;

		if (fieldsDisplayField != null) {
			String[] rawFieldsDisplayValues = _splitFieldsDisplayValue(
				fieldsDisplayField);

			fieldsDisplayValues = _toFilteredFieldNames(
				rawFieldsDisplayValues, ddmStructure);
			instanceIdsByFieldName = _toInstanceIdsByFieldName(
				rawFieldsDisplayValues);
		}

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			_updateDynamicElementElement(
				ddmFields, fieldsDisplayValues, instanceIdsByFieldName,
				ddmFieldsCounter, ddmFormField, rootElement, -1);
		}

		return document;
	}

	private void _addDDMFields(
			String[] availableLanguageIds, String defaultLanguageId,
			Fields ddmFields, StringBundler fieldsDisplaySB,
			DDMFormField ddmFormField, DDMStructure ddmStructure,
			Element element, Element rootElement)
		throws PortalException {

		String ddmFormFieldName = ddmFormField.getName();

		List<Element> dynamicElementElements = _getDynamicElementElements(
			element, ddmFormFieldName);

		if (dynamicElementElements == null) {
			dynamicElementElements = _getDynamicElementElements(
				element,
				DDMFormFieldUtil.getLegacyDDMFormFieldName(ddmFormFieldName));
		}

		if (dynamicElementElements == null) {

			// This may happen when fields in the structure have been
			// rearranged. In this case, look for the field starting from the
			// root again. See LPD-69008.

			dynamicElementElements = _getDynamicElementElements(
				rootElement, ddmFormFieldName);
		}

		if (dynamicElementElements == null) {
			if (Objects.equals(
					ddmFormField.getType(),
					DDMFormFieldTypeConstants.FIELDSET)) {

				_updateFieldsDisplay(
					fieldsDisplaySB, ddmFormFieldName,
					StringUtil.randomString());
			}

			_addNestedDDMFields(
				availableLanguageIds, defaultLanguageId, ddmFields,
				fieldsDisplaySB, ddmFormField, ddmStructure, element,
				rootElement);

			return;
		}

		for (Element dynamicElementElement : dynamicElementElements) {
			if (!ddmFormField.isTransient()) {
				Field existingDDMField = ddmFields.get(ddmFormFieldName);

				if (existingDDMField == null) {
					String legacyDDMFormFieldName =
						DDMFormFieldUtil.getLegacyDDMFormFieldName(
							ddmFormFieldName);

					if (!StringUtil.equals(
							ddmFormFieldName, legacyDDMFormFieldName)) {

						existingDDMField = ddmFields.get(
							legacyDDMFormFieldName);

						if (existingDDMField != null) {
							existingDDMField.setName(ddmFormFieldName);
						}
					}
				}

				Field ddmField = _getField(
					availableLanguageIds, ddmStructure, defaultLanguageId,
					dynamicElementElement, ddmFormFieldName);

				if (existingDDMField != null) {
					for (Locale locale : ddmField.getAvailableLocales()) {
						existingDDMField.addValues(
							locale, ddmField.getValues(locale));
					}
				}
				else {
					ddmFields.put(ddmField);
				}
			}

			_updateFieldsDisplay(
				fieldsDisplaySB, ddmFormFieldName,
				dynamicElementElement.attributeValue("instance-id"));

			_addNestedDDMFields(
				availableLanguageIds, defaultLanguageId, ddmFields,
				fieldsDisplaySB, ddmFormField, ddmStructure,
				dynamicElementElement, rootElement);
		}
	}

	private void _addNestedDDMFields(
			String[] availableLanguageIds, String defaultLanguageId,
			Fields ddmFields, StringBundler fieldsDisplaySB,
			DDMFormField ddmFormField, DDMStructure ddmStructure,
			Element element, Element rootElement)
		throws PortalException {

		for (DDMFormField nestedDDMFormField :
				ddmFormField.getNestedDDMFormFields()) {

			_addDDMFields(
				availableLanguageIds, defaultLanguageId, ddmFields,
				fieldsDisplaySB, nestedDDMFormField, ddmStructure, element,
				rootElement);
		}
	}

	private int _countFieldRepetition(
		String[] fieldsDisplayValues, String fieldName, String parentFieldName,
		int parentOffset) {

		if (fieldsDisplayValues == null) {
			return 0;
		}

		int offset = -1;

		int repetitions = 0;

		for (String fieldDisplayName : fieldsDisplayValues) {
			if (offset > parentOffset) {
				break;
			}

			if (fieldDisplayName.equals(parentFieldName)) {
				offset++;
			}

			if (fieldDisplayName.equals(fieldName) &&
				(offset == parentOffset)) {

				repetitions++;
			}
		}

		return repetitions;
	}

	private String _getAvailableLocales(Fields ddmFields) {
		Set<Locale> availableLocales = ddmFields.getAvailableLocales();

		Locale[] availableLocalesArray = new Locale[availableLocales.size()];

		availableLocalesArray = availableLocales.toArray(availableLocalesArray);

		String[] languageIds = LocaleUtil.toLanguageIds(availableLocalesArray);

		return StringUtil.merge(languageIds);
	}

	private Serializable _getCheckboxMultipleValue(
		DDMFormField ddmFormField, Element dynamicContentElement) {

		DDMFormFieldOptions ddmFormFieldOptions =
			(DDMFormFieldOptions)ddmFormField.getProperty("options");

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();

		if (options.size() == 1) {
			if (GetterUtil.getBoolean(dynamicContentElement.getText())) {
				Set<Map.Entry<String, LocalizedValue>> entries =
					options.entrySet();

				Iterator<Map.Entry<String, LocalizedValue>> iterator =
					entries.iterator();

				Map.Entry<String, LocalizedValue> entry = iterator.next();

				return JSONUtil.putAll(
					entry.getKey()
				).toString();
			}

			return StringPool.BLANK;
		}

		return FieldConstants.getSerializable(
			ddmFormField.getDataType(), dynamicContentElement.getText());
	}

	private List<Element> _getDynamicElementElements(
		Element element, String name) {

		Element parentElement = _getParentElement(element, name);

		if (parentElement == null) {
			return null;
		}

		List<Element> dynamicElementElements = null;

		for (Element dynamicElement :
				parentElement.elements("dynamic-element")) {

			if (!Objects.equals(dynamicElement.attributeValue("name"), name)) {
				continue;
			}

			if (dynamicElementElements == null) {
				dynamicElementElements = new ArrayList<>();
			}

			dynamicElementElements.add(dynamicElement);
		}

		return dynamicElementElements;
	}

	private Field _getField(
			String[] availableLanguageIds, DDMStructure ddmStructure,
			String defaultLanguageId, Element dynamicElementElement,
			String fieldName)
		throws PortalException {

		Field ddmField = new Field();

		ddmField.setDDMStructureId(ddmStructure.getStructureId());

		Locale defaultLocale = null;

		if (defaultLanguageId == null) {
			defaultLocale = LocaleUtil.getSiteDefault();
		}
		else {
			defaultLocale = LocaleUtil.fromLanguageId(defaultLanguageId);
		}

		ddmField.setDefaultLocale(defaultLocale);

		if (!GetterUtil.getBoolean(
				ddmStructure.getFieldProperty(fieldName, "localizable"))) {

			availableLanguageIds = StringPool.EMPTY_ARRAY;
		}

		ddmField.setName(fieldName);

		DDMFormField ddmFormField = ddmStructure.getDDMFormField(fieldName);

		Set<String> missingLanguageIds = SetUtil.fromArray(
			availableLanguageIds);

		missingLanguageIds.remove(defaultLanguageId);

		List<Element> dynamicContentElements = dynamicElementElement.elements(
			"dynamic-content");

		for (Element dynamicContentElement : dynamicContentElements) {
			Locale locale = defaultLocale;

			String languageId = dynamicContentElement.attributeValue(
				"language-id");

			if (Validator.isNotNull(languageId)) {
				locale = LocaleUtil.fromLanguageId(languageId, true, false);

				if (locale == null) {
					continue;
				}

				missingLanguageIds.remove(languageId);
			}

			Serializable serializable = _getFieldValue(
				ddmFormField, dynamicContentElement);

			ddmField.addValue(locale, serializable);
		}

		return ddmField;
	}

	private String _getFieldInstanceId(
		Map<String, List<String>> instanceIdsByFieldName, String fieldName,
		int index) {

		if (instanceIdsByFieldName == null) {
			return null;
		}

		List<String> instanceIds = instanceIdsByFieldName.get(fieldName);

		if ((instanceIds == null) || (index >= instanceIds.size())) {
			return null;
		}

		return instanceIds.get(index);
	}

	private Serializable _getFieldValue(
		DDMFormField ddmFormField, Element dynamicContentElement) {

		if (Objects.equals(
				DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE,
				ddmFormField.getType())) {

			return _getCheckboxMultipleValue(
				ddmFormField, dynamicContentElement);
		}

		if (Objects.equals(
				DDMFormFieldTypeConstants.SELECT, ddmFormField.getType())) {

			return _getSelectValue(dynamicContentElement);
		}

		String value = dynamicContentElement.getText();

		return FieldConstants.getSerializable(
			LocaleUtil.ROOT,
			LocaleUtil.fromLanguageId(
				dynamicContentElement.attributeValue("language-id")),
			ddmFormField.getDataType(), value.trim());
	}

	private Element _getParentElement(Element element, String name) {
		for (Element dynamicElement : element.elements("dynamic-element")) {
			if (Objects.equals(dynamicElement.attributeValue("name"), name)) {
				return element;
			}

			Element parentElement = _getParentElement(dynamicElement, name);

			if (parentElement != null) {
				return parentElement;
			}
		}

		return null;
	}

	private String _getSelectValue(Element dynamicContentElement) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<Element> optionElements = dynamicContentElement.elements("option");

		if (!optionElements.isEmpty()) {
			for (Element optionElement : optionElements) {
				jsonArray.put(optionElement.getText());
			}
		}
		else {
			jsonArray.put(dynamicContentElement.getText());
		}

		return jsonArray.toString();
	}

	private String[] _splitFieldsDisplayValue(Field fieldsDisplayField) {
		String value = (String)fieldsDisplayField.getValue();

		return StringUtil.split(value);
	}

	private String[] _toFilteredFieldNames(
		String[] rawFieldsDisplayValues, DDMStructure ddmStructure) {

		List<String> fieldsDisplayValues = new ArrayList<>();

		for (String value : rawFieldsDisplayValues) {
			String fieldName = StringUtil.extractFirst(
				value, DDM.INSTANCE_SEPARATOR);

			if (ddmStructure.hasField(fieldName)) {
				fieldsDisplayValues.add(fieldName);
			}
		}

		return fieldsDisplayValues.toArray(new String[0]);
	}

	private Map<String, List<String>> _toInstanceIdsByFieldName(
		String[] rawFieldsDisplayValues) {

		Map<String, List<String>> instanceIdsByFieldName = new HashMap<>();

		for (String value : rawFieldsDisplayValues) {
			String fieldName = StringUtil.extractFirst(
				value, DDM.INSTANCE_SEPARATOR);
			String instanceId = StringUtil.extractLast(
				value, DDM.INSTANCE_SEPARATOR);

			List<String> instanceIds = instanceIdsByFieldName.computeIfAbsent(
				fieldName, key -> new ArrayList<>());

			instanceIds.add(instanceId);
		}

		return instanceIdsByFieldName;
	}

	private void _updateContentDynamicElement(
		int count, DDMFormField ddmFormField, Element dynamicElementElement,
		Field field) {

		for (Locale locale : field.getAvailableLocales()) {
			Serializable fieldValue = field.getValue(locale, count);

			if (fieldValue == null) {
				fieldValue = field.getValue(field.getDefaultLocale(), count);
			}

			String valueString = StringPool.BLANK;

			if (fieldValue != null) {
				valueString = String.valueOf(fieldValue);
			}

			if (StringUtil.equals(valueString, DDM.FIELD_EMPTY_VALUE)) {
				continue;
			}

			Element dynamicContentElement = dynamicElementElement.addElement(
				"dynamic-content");

			dynamicContentElement.addAttribute(
				"language-id", LocaleUtil.toLanguageId(locale));

			_updateDynamicContentValue(
				ddmFormField, dynamicContentElement, ddmFormField.getName(),
				ddmFormField.getType(), valueString.trim(),
				ddmFormField.isMultiple());
		}
	}

	private void _updateDynamicContentValue(
		DDMFormField ddmFormField, Element dynamicContentElement,
		String fieldName, String fieldType, String fieldValue,
		boolean multiple) {

		if (Objects.equals(
				DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE, fieldType)) {

			try {
				JSONArray fieldValueJSONArray = _jsonFactory.createJSONArray(
					fieldValue);

				DDMFormFieldOptions ddmFormFieldOptions =
					(DDMFormFieldOptions)ddmFormField.getProperty("options");

				Map<String, LocalizedValue> options =
					ddmFormFieldOptions.getOptions();

				if (options.size() > 1) {
					dynamicContentElement.addCDATA(fieldValue);

					for (int i = 0; i < fieldValueJSONArray.length(); i++) {
						String selectedValue = fieldValueJSONArray.getString(i);

						Element optionReferenceElement =
							dynamicContentElement.addElement(
								"option-reference");

						optionReferenceElement.addCDATA(
							ddmFormFieldOptions.getOptionReference(
								selectedValue));
					}

					return;
				}

				if (fieldValueJSONArray.length() == 1) {
					fieldValue = Boolean.TRUE.toString();
				}
				else {
					fieldValue = StringPool.BLANK;
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get dynamic data mapping form field for " +
							fieldName,
						portalException);
				}
			}

			dynamicContentElement.addCDATA(fieldValue);
		}
		else if (Objects.equals(DDMFormFieldTypeConstants.RADIO, fieldType) &&
				 Validator.isNotNull(fieldValue)) {

			DDMFormFieldOptions ddmFormFieldOptions =
				ddmFormField.getDDMFormFieldOptions();

			dynamicContentElement.addCDATA(fieldValue);

			Element optionReferenceElement = dynamicContentElement.addElement(
				"option-reference");

			optionReferenceElement.addCDATA(
				ddmFormFieldOptions.getOptionReference(fieldValue));
		}
		else if (Objects.equals(DDMFormFieldTypeConstants.SELECT, fieldType) &&
				 Validator.isNotNull(fieldValue)) {

			JSONArray jsonArray = null;

			try {
				jsonArray = _jsonFactory.createJSONArray(fieldValue);
			}
			catch (JSONException jsonException) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to parse object", jsonException);
				}

				return;
			}

			if (multiple) {
				for (int i = 0; i < jsonArray.length(); i++) {
					Element optionElement = dynamicContentElement.addElement(
						"option");

					optionElement.addCDATA(jsonArray.getString(i));

					Element optionReferenceElement =
						dynamicContentElement.addElement("option-reference");

					DDMFormFieldOptions ddmFormFieldOptions =
						ddmFormField.getDDMFormFieldOptions();

					optionReferenceElement.addCDATA(
						ddmFormFieldOptions.getOptionReference(
							jsonArray.getString(i)));
				}
			}
			else {
				Element optionElement = dynamicContentElement.addElement(
					"option");

				optionElement.addCDATA(jsonArray.getString(0));

				Element optionReferenceElement =
					dynamicContentElement.addElement("option-reference");

				DDMFormFieldOptions ddmFormFieldOptions =
					ddmFormField.getDDMFormFieldOptions();

				optionReferenceElement.addCDATA(
					ddmFormFieldOptions.getOptionReference(
						jsonArray.getString(0)));
			}
		}
		else {
			dynamicContentElement.addCDATA(fieldValue);
		}
	}

	private void _updateDynamicElementElement(
			Fields ddmFields, String[] fieldsDisplayValues,
			Map<String, List<String>> instanceIdsByFieldName,
			DDMFieldsCounter ddmFieldsCounter, DDMFormField ddmFormField,
			Element dynamicElementElement, int parentOffset)
		throws Exception {

		String ddmFormFieldName = ddmFormField.getName();

		int count = ddmFieldsCounter.get(ddmFormFieldName);

		int repetitions = _countFieldRepetition(
			fieldsDisplayValues, ddmFormFieldName,
			dynamicElementElement.attributeValue("name"), parentOffset);

		for (int i = 0; i < repetitions; i++) {
			Element childDynamicElementElement =
				dynamicElementElement.addElement("dynamic-element");

			childDynamicElementElement.addAttribute(
				"field-reference", ddmFormField.getFieldReference());
			childDynamicElementElement.addAttribute(
				"index-type", ddmFormField.getIndexType());

			childDynamicElementElement.addAttribute(
				"instance-id",
				_getFieldInstanceId(
					instanceIdsByFieldName, ddmFormFieldName, count + i));

			childDynamicElementElement.addAttribute("name", ddmFormFieldName);
			childDynamicElementElement.addAttribute(
				"type", ddmFormField.getType());

			List<DDMFormField> nestedDDMFormFields =
				ddmFormField.getNestedDDMFormFields();

			Field field = ddmFields.get(ddmFormFieldName);

			if (!Objects.equals(
					ddmFormField.getType(),
					DDMFormFieldTypeConstants.FIELDSET) &&
				!ddmFormField.isTransient() && (field != null)) {

				_updateContentDynamicElement(
					ddmFieldsCounter.get(ddmFormFieldName), ddmFormField,
					childDynamicElementElement, field);
			}
			else if (ListUtil.isNotEmpty(nestedDDMFormFields)) {
				for (DDMFormField nestedDDMFormField : nestedDDMFormFields) {
					_updateDynamicElementElement(
						ddmFields, fieldsDisplayValues, instanceIdsByFieldName,
						ddmFieldsCounter, nestedDDMFormField,
						childDynamicElementElement, count + i);
				}
			}

			ddmFieldsCounter.incrementKey(ddmFormFieldName);
		}
	}

	private void _updateFieldsDisplay(
		StringBundler fieldsDisplaySB, String fieldName, String instanceId) {

		if (Validator.isNull(instanceId)) {
			instanceId = StringUtil.randomString();
		}

		if (fieldsDisplaySB.length() > 0) {
			fieldsDisplaySB.append(StringPool.COMMA);
		}

		fieldsDisplaySB.append(fieldName);
		fieldsDisplaySB.append(DDM.INSTANCE_SEPARATOR);
		fieldsDisplaySB.append(instanceId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalConverterImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}
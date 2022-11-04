/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.notification.recipient.script;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.internal.util.WorkflowServiceTrackerCustomizer;
import com.liferay.portal.workflow.kaleo.runtime.notification.recipient.script.NotificationRecipientEvaluator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Michael C. Han
 */
@Component(service = NotificationRecipientEvaluator.class)
public class MultiLanguageNotificationRecipientEvaluator
	implements NotificationRecipientEvaluator {

	@Override
	public Map<String, ?> evaluate(
			KaleoNotificationRecipient kaleoNotificationRecipient,
			ExecutionContext executionContext)
		throws PortalException {

		String notificationRecipientEvaluatorKey =
			_getNotificationRecipientEvaluatorKey(
				kaleoNotificationRecipient.getRecipientScriptLanguage(),
				kaleoNotificationRecipient.getRecipientScript());

		NotificationRecipientEvaluator notificationRecipientEvaluator =
			_notificationRecipientEvaluators.get(
				notificationRecipientEvaluatorKey);

		if (notificationRecipientEvaluator == null) {
			throw new IllegalArgumentException(
				"No notification recipient evaluator for script language " +
					notificationRecipientEvaluatorKey);
		}

		return notificationRecipientEvaluator.evaluate(
			kaleoNotificationRecipient, executionContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(scripting.language=*)(objectClass=" +
				NotificationRecipientEvaluator.class.getName() + "))",
			new WorkflowServiceTrackerCustomizer
				<NotificationRecipientEvaluator>(
					bundleContext,
					"Must have a scripting.language property for: ", true,
					"scripting.language", _notificationRecipientEvaluators,
					true));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private String _getNotificationRecipientEvaluatorKey(
			String language, String notificationRecipientEvaluatorClassName)
		throws KaleoDefinitionValidationException {

		ScriptLanguage scriptLanguage = ScriptLanguage.parse(language);

		if (scriptLanguage.equals(ScriptLanguage.JAVA)) {
			return language + StringPool.COLON +
				notificationRecipientEvaluatorClassName;
		}

		return language;
	}

	private String[] _getScriptingLanguages(
		NotificationRecipientEvaluator notificationRecipientEvaluator,
		Map<String, Object> properties) {

		Object value = properties.get("scripting.language");

		String[] scriptingLanguages = GetterUtil.getStringValues(
			value, new String[] {String.valueOf(value)});

		if (ArrayUtil.isEmpty(scriptingLanguages)) {
			throw new IllegalArgumentException(
				"Must have a scripting.language property for: " +
					ClassUtil.getClassName(notificationRecipientEvaluator));
		}

		return scriptingLanguages;
	}

	private final Map<String, NotificationRecipientEvaluator>
		_notificationRecipientEvaluators = new ConcurrentHashMap<>();
	private ServiceTracker
		<NotificationRecipientEvaluator, NotificationRecipientEvaluator>
			_serviceTracker;

}
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

package com.liferay.portal.workflow.kaleo.runtime.internal.condition;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.model.KaleoCondition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.condition.ConditionEvaluator;
import com.liferay.portal.workflow.kaleo.runtime.internal.util.WorkflowServiceTrackerCustomizer;

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
@Component(service = ConditionEvaluator.class)
public class MultiLanguageConditionEvaluator implements ConditionEvaluator {

	@Override
	public String evaluate(
			KaleoCondition kaleoCondition, ExecutionContext executionContext)
		throws PortalException {

		String conditionEvaluatorKey = _getConditionEvaluatorKey(
			kaleoCondition.getScriptLanguage(),
			StringUtil.trim(kaleoCondition.getScript()));

		ConditionEvaluator conditionEvaluator = _conditionEvaluators.get(
			conditionEvaluatorKey);

		if (conditionEvaluator == null) {
			throw new IllegalArgumentException(
				"No condition evaluator found for script language " +
					conditionEvaluatorKey);
		}

		return conditionEvaluator.evaluate(kaleoCondition, executionContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(scripting.language=*)(objectClass=" +
				ConditionEvaluator.class.getName() + "))",
			new WorkflowServiceTrackerCustomizer<ConditionEvaluator>(
				bundleContext, null, true, "scripting.language",
				_conditionEvaluators, true));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private String _getConditionEvaluatorKey(
			String language, String conditionEvaluatorClassName)
		throws KaleoDefinitionValidationException {

		ScriptLanguage scriptLanguage = ScriptLanguage.parse(language);

		if (scriptLanguage.equals(ScriptLanguage.JAVA)) {
			return language + StringPool.COLON + conditionEvaluatorClassName;
		}

		return language;
	}

	private final Map<String, ConditionEvaluator> _conditionEvaluators =
		new ConcurrentHashMap<>();
	private ServiceTracker<ConditionEvaluator, ConditionEvaluator>
		_serviceTracker;

}
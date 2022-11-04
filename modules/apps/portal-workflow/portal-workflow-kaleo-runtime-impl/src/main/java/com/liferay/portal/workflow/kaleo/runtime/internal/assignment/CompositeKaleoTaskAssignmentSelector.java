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

package com.liferay.portal.workflow.kaleo.runtime.internal.assignment;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.KaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.internal.util.WorkflowServiceTrackerCustomizer;

import java.util.Collection;
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
@Component(service = CompositeKaleoTaskAssignmentSelector.class)
public class CompositeKaleoTaskAssignmentSelector
	implements KaleoTaskAssignmentSelector {

	@Override
	public Collection<KaleoTaskAssignment> getKaleoTaskAssignments(
			KaleoTaskAssignment kaleoTaskAssignment,
			ExecutionContext executionContext)
		throws PortalException {

		String assigneeClassName = kaleoTaskAssignment.getAssigneeClassName();

		KaleoTaskAssignmentSelector kaleoTaskAssignmentSelector =
			_kaleoTaskAssignmentSelectors.get(assigneeClassName);

		if (kaleoTaskAssignmentSelector == null) {
			throw new IllegalArgumentException(
				"No task assignment selector found for class name " +
					assigneeClassName);
		}

		return kaleoTaskAssignmentSelector.getKaleoTaskAssignments(
			kaleoTaskAssignment, executionContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(assignee.class.name=*)(objectClass=" +
				KaleoTaskAssignmentSelector.class.getName() + "))",
			new WorkflowServiceTrackerCustomizer<KaleoTaskAssignmentSelector>(
				bundleContext, null, false, "assignee.class.name",
				_kaleoTaskAssignmentSelectors, true));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private String[] _getAssigneeClassNames(
		KaleoTaskAssignmentSelector kaleoTaskAssignmentSelector,
		Map<String, Object> properties) {

		Object value = properties.get("assignee.class.name");

		String[] assigneeClassNames = GetterUtil.getStringValues(
			value, new String[] {String.valueOf(value)});

		if (ArrayUtil.isEmpty(assigneeClassNames)) {
			throw new IllegalArgumentException(
				"The property \"assignee.class.name\" is invalid for " +
					ClassUtil.getClassName(kaleoTaskAssignmentSelector));
		}

		return assigneeClassNames;
	}

	private final Map<String, KaleoTaskAssignmentSelector>
		_kaleoTaskAssignmentSelectors = new ConcurrentHashMap<>();
	private ServiceTracker
		<KaleoTaskAssignmentSelector, KaleoTaskAssignmentSelector>
			_serviceTracker;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.ClassTestRule;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.runner.Description;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class DestinationAwaitClassTestRule
	extends ClassTestRule
		<Set
			<ObjectValuePair
				<CountDownLatch, ServiceRegistration<MessageListener>>>> {

	public static final DestinationAwaitClassTestRule INSTANCE =
		new DestinationAwaitClassTestRule(DestinationNames.HOT_DEPLOY);

	public DestinationAwaitClassTestRule(String... destinationNames) {
		_destinationNames = destinationNames;
	}

	@Override
	public void afterClass(
			Description description,
			Set
				<ObjectValuePair
					<CountDownLatch, ServiceRegistration<MessageListener>>>
						endCountDownLatches)
		throws Throwable {

		endCountDownLatches.forEach(
			objectValuePair -> {
				CountDownLatch countDownLatch = objectValuePair.getKey();

				countDownLatch.countDown();

				ServiceRegistration<MessageListener> serviceRegistration =
					objectValuePair.getValue();

				serviceRegistration.unregister();
			});
	}

	@Override
	public Set
		<ObjectValuePair<CountDownLatch, ServiceRegistration<MessageListener>>>
				beforeClass(Description description)
			throws InterruptedException {

		Set
			<ObjectValuePair
				<CountDownLatch, ServiceRegistration<MessageListener>>>
					endCountdownLatches = new HashSet<>();

		for (String destinationName : _destinationNames) {
			Destination destination = MessageBusUtil.getDestination(
				destinationName);

			if (destination == null) {
				if (System.getenv("JENKINS_HOME") != null) {
					throw new IllegalStateException(
						destinationName + " is not available");
				}

				continue;
			}

			final CountDownLatch startCountDownLatch = new CountDownLatch(1);

			final CountDownLatch endCountDownLatch = new CountDownLatch(1);

			final Message countDownMessage = new Message();

			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			ServiceRegistration<MessageListener> serviceRegistration =
				bundleContext.registerService(
					MessageListener.class,
					new MessageListener() {

						@Override
						public void receive(Message message) {
							if (countDownMessage == message) {
								startCountDownLatch.countDown();

								try {
									endCountDownLatch.await();

									destination.unregister(this);
								}
								catch (InterruptedException
											interruptedException) {

									ReflectionUtil.throwException(
										interruptedException);
								}
							}
						}

					},
					MapUtil.singletonDictionary(
						"destination.name", destinationName));

			destination.send(countDownMessage);

			startCountDownLatch.await();

			endCountdownLatches.add(
				new ObjectValuePair<>(endCountDownLatch, serviceRegistration));
		}

		return endCountdownLatches;
	}

	private final String[] _destinationNames;

}
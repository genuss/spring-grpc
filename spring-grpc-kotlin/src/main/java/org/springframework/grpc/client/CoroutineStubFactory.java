package org.springframework.grpc.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.kotlin.AbstractCoroutineStub;
import java.util.function.Supplier;

public class CoroutineStubFactory implements StubFactory<AbstractCoroutineStub<?>> {

	@Override
	public AbstractCoroutineStub<?> create(Supplier<ManagedChannel> channel,
			Class<? extends AbstractCoroutineStub<?>> type) {
		try {
			return type.getConstructor(Channel.class, CallOptions.class)
				.newInstance(channel.get(), CallOptions.DEFAULT);

		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to create stub", e);
		}
	}

	protected static boolean supports(Class<?> type) {
		return AbstractCoroutineStub.class.isAssignableFrom(type);
	}

}

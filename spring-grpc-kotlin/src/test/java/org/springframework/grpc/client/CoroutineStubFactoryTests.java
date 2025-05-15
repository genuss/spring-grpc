package org.springframework.grpc.client;

import static org.assertj.core.api.Assertions.assertThat;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.kotlin.AbstractCoroutineStub;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.grpc.client.GrpcClientFactory.GrpcClientRegistrationSpec;

class CoroutineStubFactoryTests {

	private final GrpcChannelFactory channelFactory = Mockito.mock(GrpcChannelFactory.class);

	private final StaticApplicationContext context = new StaticApplicationContext();

	private final GrpcClientFactory factory;

	CoroutineStubFactoryTests() {
		Mockito.when(channelFactory.createChannel(Mockito.anyString(), Mockito.any()))
			.thenReturn(Mockito.mock(ManagedChannel.class));
		context.registerBean(GrpcChannelFactory.class, () -> channelFactory);
		factory = new GrpcClientFactory(context);
	}

	@Test
	void testRegisterAndCreate() {
		context.registerBean(CoroutineStubFactory.class, CoroutineStubFactory::new);
		GrpcClientFactory.register(context,
				GrpcClientRegistrationSpec.of("local")
					.factory(CoroutineStubFactory.class)
					.types(MyCoroutineStub.class));
		assertThat(factory.getClient("local", MyCoroutineStub.class, null)).isNotNull();
	}

	static class MyCoroutineStub extends AbstractCoroutineStub<MyCoroutineStub> {

		public MyCoroutineStub(Channel channel, CallOptions callOptions) {
			super(channel, callOptions);
		}

		@Override
		protected MyCoroutineStub build(Channel channel, CallOptions callOptions) {
			return new MyCoroutineStub(channel, callOptions);
		}

	}

}
package org.example;
import java.util.concurrent.TimeUnit;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class ExampleConfiguration {

	static OpenTelemetry initOpenTelemetry(String jaegerEndpoint) {
		// Export traces to Jaeger over OTLP
	    OtlpGrpcSpanExporter jaegerOtlpExporter =
	        OtlpGrpcSpanExporter.builder()
	            .setEndpoint(jaegerEndpoint)
	            .setTimeout(30, TimeUnit.SECONDS)
	            .build();
	    
	    OtlpGrpcMetricExporter metricOtlp =
	    		OtlpGrpcMetricExporter.builder()
		            .setEndpoint(jaegerEndpoint)
		            .setTimeout(30, TimeUnit.SECONDS)
		            .build();
	    
		Resource resource = Resource.getDefault().merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "service2")));

				SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
				  .addSpanProcessor(BatchSpanProcessor.builder(jaegerOtlpExporter).build())
				  .setResource(resource)
				  .build();

				SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
				  .registerMetricReader(PeriodicMetricReader.builder(metricOtlp).build())
				  .setResource(resource)
				  .build();

				OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
				  .setTracerProvider(sdkTracerProvider)
				  .setMeterProvider(sdkMeterProvider)
				  .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
				  .buildAndRegisterGlobal();
		return openTelemetry;
	}
}

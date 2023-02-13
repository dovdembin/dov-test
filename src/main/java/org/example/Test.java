package org.example;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class Test {
	 private static final Logger log = LoggerFactory.getLogger(Test.class);
	long startTime = System.currentTimeMillis();
	private final Tracer tracer;
	private final Meter meter;
	private final LongHistogram histogram;
	
	public Test(OpenTelemetry openTelemetry) {
		tracer = openTelemetry.getTracer("io.opentelemetry.example");
		meter = openTelemetry.getMeter("io.opentelemetry.example");
		histogram = meter.histogramBuilder("super_timer").ofLongs().setUnit("ms").build();
	}

	private void myWonderfulUseCase(int i) throws InterruptedException {
		
	    
		LongCounter counter = meter
			      .counterBuilder("processed_jobs")
			      .setDescription("Processed jobs")
			      .setUnit("1")
			      .build();
	    
		long startTime = System.currentTimeMillis();
        Span exampleSpan = tracer.spanBuilder("oteljenkins").startSpan();
        try (Scope scope = exampleSpan.makeCurrent()) {
          exampleSpan.addEvent("Event 0");
          Attributes attributes = Attributes.of(AttributeKey.stringKey("Key"), "SomeWork");
          counter.add(123, attributes);
          exampleSpan.setAttribute("good", "true");
          exampleSpan.setAttribute("exampleNumber", i);
          log.debug("ok this is greatt");
          Thread.sleep(100);
          this.parentTwo(i);
          exampleSpan.addEvent("Event 1");
        } finally {
          histogram.record(System.currentTimeMillis() - startTime);
          exampleSpan.end();
        }
	  }

	private void doWork() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// do the right thing here
		}
	}
	  
	private void parentTwo(int i) {
		Span parentSpan = this.tracer.spanBuilder("parent").startSpan();
		try(Scope scope = parentSpan.makeCurrent()) {
			childTwo(i);
		} finally {
			parentSpan.end();
		}
	}

	private void childTwo(int i) {
		Span childSpan = this.tracer.spanBuilder("child")
				// NOTE: setParent(...) is not required;
				// `Span.current()` is automatically added as the parent
				.startSpan();
		System.out.println(i);
		
		LongCounter orderValueCounter = meter.counterBuilder("order_value").build();
		orderValueCounter.add(i);
		Span.current().addEvent("my event");
	    if(i%2==0) {
	    	log.info("ok this is greatt");
	    	Span.current().setAttribute("co.elastic.discardable", false);
	    	childSpan.setStatus(StatusCode.ERROR, "operationThatCouldFail failed");
			System.out.println("printing I");
			Attributes eventAttributes = Attributes.of(
				    AttributeKey.stringKey("kokokey"), "lokovalue",
				    AttributeKey.longKey("result"), 11L);
			try {
				int num = i / 0;
			} catch (Exception e) {
				childSpan.recordException(e);
			}
			meter
			  .gaugeBuilder("cpu_usage")
			  .setDescription("CPU Usage")
			  .setUnit("ms")
			  .buildWithCallback(measurement -> {
			    measurement.record(33, Attributes.of(AttributeKey.stringKey("Key"), "SomeWork"));
			  });
			childSpan.addEvent("outcome",eventAttributes);
			 
	    }
		try(Scope scope = childSpan.makeCurrent()) {
			// do stuff
		} finally {
			childSpan.end();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		 
		
		// Parsing the input
	    if (args.length < 1) {
	      System.out.println("Missing [endpoint]");
	      System.exit(1);
	    }
	    String jaegerEndpoint = args[0];
		OpenTelemetry openTelemetry = ExampleConfiguration.initOpenTelemetry(jaegerEndpoint);
		Test example = new Test(openTelemetry);
		 
		
	    
	    for (int i = 0; i < 25; i++) {
	    	example.myWonderfulUseCase(i);
	    	Thread.sleep(1000);
	    }

	    // sleep for a bit to let everything settle
	    Thread.sleep(2000);

	    System.out.println("Bye");
	    
//		// Start the example
//		Test example = new Test(openTelemetry);
//	    // generate a few sample spans
//	    for (int i = 0; i < 10; i++) {
//	      example.myWonderfulUseCase();
//	    }
//
//	    System.out.println("Bye");
	}
	
	 
}

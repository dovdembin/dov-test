package io.opentelemetry.example.metrics;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

/**
 * Example of using {@link DoubleCounter} to count disk space used by files with specific
 * extensions.
 */
public final class DoubleCounterExample {

  private static final Tracer tracer = GlobalOpenTelemetry.getTracer("io.opentelemetry.example.metrics");
  private static final Meter sampleMeter = GlobalOpenTelemetry.getMeter("io.opentelemetry.example.metrics");
  private static final File directoryToCountIn = FileSystemView.getFileSystemView().getHomeDirectory();
  private static final DoubleCounter diskSpaceCounter =
      sampleMeter
          .counterBuilder("calculated_used_space")
          .setDescription("Counts disk space used by file extension.")
          .setUnit("MB")
          .ofDoubles()
          .build();
  private static final AttributeKey<String> FILE_EXTENSION_KEY = stringKey("file_extension");

  public static void main(String[] args) {
    Span span = tracer.spanBuilder("calculate space").setSpanKind(SpanKind.INTERNAL).startSpan();
    DoubleCounterExample example = new DoubleCounterExample();
    try (Scope scope = span.makeCurrent()) {
      List<String> extensionsToFind = new ArrayList<>();
      extensionsToFind.add("dll");
      extensionsToFind.add("png");
      extensionsToFind.add("txt");
      example.calculateSpaceUsedByFilesWithExtension(extensionsToFind, directoryToCountIn);
    } catch (Exception e) {
      span.setStatus(StatusCode.ERROR, "Error while calculating used space");
    } finally {
    	System.out.println("end");
      span.end();
    }
  }

  public void calculateSpaceUsedByFilesWithExtension(List<String> extensions, File directory) {
	  File file = new File("C:\\Users\\dov_dembin\\eclipse-workspace\\dov-test\\src\\main\\java\\io\\opentelemetry\\example\\metrics");
     
    
     
      
        for (String extension : extensions) {
           
            // we can add values to the counter for specific labels
            // the label key is "file_extension", its value is the name of the extension
            diskSpaceCounter.add(
                (double) file.length() / 1_000_000, Attributes.of(FILE_EXTENSION_KEY, extension));
           
        }
       
     
  }
}

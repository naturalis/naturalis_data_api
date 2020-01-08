package nl.naturalis.nba.etl;

import java.io.Closeable;
import java.util.Collection;

import nl.naturalis.nba.api.model.IDocumentObject;

public interface DocumentObjectWriter<T extends IDocumentObject> extends Closeable {

  /**
   * Adds the specified objects to a queue of objects waiting to be indexed.
   * When the size of the queue reaches a certain treshold (specified through
   * the {@code queueSize} parameter of the constructor), all objects in the
   * queue are flushed at once to Elasticsearch. In other words, calling
   * {@code queue} does not necessarily immediately trigger the specified
   * objects to be indexed.
   * 
   * @param objects - ...
   */
  void write(Collection<T> objects);

  /**
   * Flushes the contents of the queue to ElasticSearch. While processing your
   * data sources you don't have to call this method explicitly as it is done
   * implicitly by the {@link #write(Collection) queue} method once the queue fills
   * up. However, you <b>must</b> call this method yourself (e.g. in a finally
   * block) once all source data has been processed to make sure any remaining
   * objects in the queue are written to Elasticsearch. Alternatively, you can
   * set up a try-with-resources block to achieve the same.
   */
  default void flush() {}
  
  /**
   * Determines whether to suppress ERROR and WARN messages while still
   * letting through INFO messages. This is sometimes helpful if you expect
   * large amounts of well-known errors and warnings that just clog up your
   * log file.
   * 
   * @param suppressErrors - ...
   */
  void suppressErrors(boolean suppressErrors);

}

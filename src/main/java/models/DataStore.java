package models;

import java.io.IOException;
import java.util.List;

/**
 * @author Neeilan
 *
 */
public interface DataStore<T> {
	public void add(T item) throws IOException;
	public List<T> findAll();
	public void remove(T item) throws IOException;
}
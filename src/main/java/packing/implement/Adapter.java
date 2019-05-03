package packing.implement;

import java.util.List;
import java.util.function.BooleanSupplier;

import packing.payload.BoxItem;
import packing.payload.Container;

/**
 * Logical packager for wrapping preprocessing / optimizations.
 */
public interface Adapter {
	void initialize(List<BoxItem> boxes, List<Container> container);
	Container accepted(PackResult result);
	PackResult attempt(int containerIndex, BooleanSupplier interrupt);
	boolean hasMore(PackResult result);
}

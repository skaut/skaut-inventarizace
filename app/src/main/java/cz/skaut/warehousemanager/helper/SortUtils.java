package cz.skaut.warehousemanager.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.skaut.warehousemanager.entity.Warehouse;

public class SortUtils {

	private final Map<Warehouse, List<Warehouse>> map;
	private final List<Warehouse> result;

	public SortUtils() {
		map = new HashMap<>();
		result = new ArrayList<>();
	}

	public List<Warehouse> sort(List<Warehouse> input) {
		for (Warehouse w : input) {
			Warehouse parent = w.getParentWarehouse();
			if (map.containsKey(parent)) {
				map.get(parent).add(w);
			} else {
				List<Warehouse> list = new ArrayList<>();
				list.add(w);
				map.put(parent, list);
			}
		}
		processWarehouse(null, 0);
		return result;
	}

	private void processWarehouse(Warehouse warehouse, int indentCount) {
		if (warehouse != null) {
			result.add(warehouse);
		}
		List<Warehouse> children = map.get(warehouse);
		if (children != null) {
			for (Warehouse child : children) {
				processWarehouse(child, indentCount + 1);
			}
		}
	}
}

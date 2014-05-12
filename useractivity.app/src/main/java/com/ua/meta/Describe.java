//from http://techieme.in/techieme/generictostring/

package com.ua.meta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Generic Utility to be used as an alternative to toString method. This utility
 * can print the toString method of any object ( even the one&#39;s without a
 * suitable overriden toString() method )
 * 
 * <p>
 * This also provides few options as below:
 * <ul>
 * <li>Ignore describing collections.</li>
 * <li>Ignore describing the arrays.</li>
 * <li>Ignore printing the type information of each field.</li>
 * </ul>
 * 
 * <p>
 * The default variant will print all the collections & arrays and all the type
 * information. Also included a sample usage in the main method.
 * 
 * 
 * @author dprasad
 * 
 */
public class Describe {

	boolean ignoreCollections;
	boolean ignoreArrays;
	boolean ignoreType;

	public Describe(boolean ignoreCollections, boolean ignoreArrays,
			boolean ignoreType) {
		this.ignoreArrays = ignoreArrays;
		this.ignoreCollections = ignoreCollections;
		this.ignoreType = ignoreType;
	}

	public Describe() {
		this.ignoreArrays = false;
		this.ignoreCollections = false;
		this.ignoreType = false;
	}

	String describeInner(Object o, StringBuilder sb) {

		if (o == null) {
			sb.append("null");
		} else {

			if (o instanceof Collection) {
				if (!ignoreCollections) {
					sb.append(" [");

					for (Object i : (Collection) o) {
						describeInner(i, sb);
						sb.append(", ");
					}

					sb.replace(sb.length() - 2, sb.length(), "");
					sb.append("] ");
				}

				else {
					sb.append("ignored collection");
				}
			}

			else if (o instanceof Map) {
				if (!ignoreCollections) {
					sb.append(" [");

					for (Object i : ((Map) o).entrySet()) {
						if (i instanceof Entry) {
							describeInner(((Entry) i).getKey(), sb);
							sb.append(" : ");
							describeInner(((Entry) i).getValue(), sb);
							sb.append(", ");
						}
					}

					sb.replace(sb.length() - 2, sb.length(), "");
					sb.append("] ");
				} else {
					sb.append("ignored collection");
				}
			}

			else if (o.getClass().isArray()) {
				if (!ignoreArrays) {
					sb.append(" [");

					for (Object i : (Object[]) o) {
						describeInner(i, sb);
						sb.append(", ");
					}

					sb.replace(sb.length() - 2, sb.length(), "");
					sb.append("] ");
				} else {
					sb.append("ignored arrays");
				}
			}

			else if (o instanceof Number || o instanceof Character
					|| o instanceof Boolean || o instanceof String || o instanceof XMLGregorianCalendar) {
				sb.append(o);
			}

			else if (o instanceof Enum) {
				sb.append(((Enum) o).name());
			}

			else {
				sb.append(reflect(o));
			}
		}

		return null;
	}

	private List<Field> getAllFieldsRec(Class clazz, List<Field> list) {
		Class superClazz = clazz.getSuperclass();
		if (superClazz != null && !isFrameworkClass(superClazz)) {
			getAllFieldsRec(superClazz, list);
		}
		list.addAll(Arrays.asList(clazz.getDeclaredFields()));
		return list;
	}

	private boolean isFrameworkClass(Class clazz) {
		if (clazz.getName().startsWith("java."))
			return true;
		if (clazz.getName().startsWith("sun."))
			return true;
		if (clazz.getName().startsWith("org."))
			return true;
		return false;

	}

	private String reflect(Object o) {
		StringBuilder s = new StringBuilder();

		if (isFrameworkClass(o.getClass())) {
			s.append(o);
			return s.toString();
		}
		List<Field> fields = new ArrayList<Field>();
		fields = getAllFieldsRec(o.getClass(), fields);
		s.append("Object{");

		for (Field f : fields) {
			s.append(f.getName());
			s.append("=(");

			try {
				f.setAccessible(true);
				if (!ignoreType) {
					String type = f.getGenericType().toString();
					s.append(type);
					s.append(", ");
				}
				Object value = f.get(o);

				describeInner(value, s);
				s.append(")");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();

			}

			s.append(", ");
		}
		s.replace(s.length() - 2, s.length(), "");
		s.append("} ");
		return s.toString();
	}

	public String describe(Object o) {
		if (o != null) {
			StringBuilder sb = new StringBuilder();
			describeInner(o, sb);
			return sb.toString();
		}
		return null;
	}
}
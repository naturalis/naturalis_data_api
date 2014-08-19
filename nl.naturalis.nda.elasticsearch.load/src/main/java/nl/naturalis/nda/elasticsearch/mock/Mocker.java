package nl.naturalis.nda.elasticsearch.mock;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import nl.naturalis.nda.domain.Specimen;

import org.domainobject.util.ClassUtil;
import org.domainobject.util.ExceptionUtil;

public class Mocker {

	private final HashMap<Class<?>, List<Field>> fieldCache = new HashMap<Class<?>, List<Field>>();
	private final HashMap<Class<?>, Integer> maxListSizes = new HashMap<Class<?>, Integer>();
	private final Random random = new Random();


	public <T> T createMock(Class<T> cls, Class<?>... relations)
	{
		return createMock(cls, null, Arrays.asList(relations));
	}


	public void setMaxListSize(Class<?> forClass, int maxSize)
	{
		maxListSizes.put(forClass, maxSize);
	}


	@SuppressWarnings("unchecked")
	private <T> T createMock(Class<T> cls, Class<?> ancestor, List<Class<?>> relations)
	{
		try {
			T mock = cls.newInstance();
			for (Field f : getFields(cls)) {
				int i = f.getModifiers();
				if (Modifier.isFinal(i)) {
					continue;
				}
				if (!f.isAccessible()) {
					f.setAccessible(true);
				}
				if (f.getType() == String.class) {
					String value = f.getName() + "_" + random.nextInt(99);
					f.set(mock, value);
				}
				else if (f.getType() == int.class || f.getType() == Integer.class) {
					f.set(mock, random.nextInt());
				}
				else if (f.getType() == long.class || f.getType() == Long.class) {
					f.set(mock, random.nextLong());
				}
				else if (f.getType() == boolean.class || f.getType() == Boolean.class) {
					boolean value = random.nextInt(2) == 1;
					f.set(mock, value);
				}
				else if (f.getType() == Date.class) {
					Date value = new Date(random.nextInt());
					f.set(mock, value);
				}
				else if (ClassUtil.isA(f.getType(), List.class)) {
					ParameterizedType listType = (ParameterizedType) f.getGenericType();
					Class<?> listElementClass = (Class<?>) listType.getActualTypeArguments()[0];
					// A Class containing a List whose elements are of that same class
					// will cause infinite recursion
					if (!listElementClass.equals(cls)) {
						if (relations.contains(listElementClass) && listElementClass != ancestor) {
							//int maxSize = maxListSizes.containsKey(listElementClass) ? maxListSizes.get(listElementClass) : 5;
							int listSize = 2 + random.nextInt(3);
							@SuppressWarnings("rawtypes")
							List list = new ArrayList(listSize);
							for (int j = 0; j < listSize; ++j) {
								list.add(createMock(listElementClass, cls, relations));
							}
							f.set(mock, list);
						}
					}
				}
				else if (relations.contains(f.getType()) && f.getType() != ancestor) {
					Object value = createMock(f.getType(), cls, relations);
					f.set(mock, value);
				}
			}
			return mock;
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
	}


	private List<Field> getFields(Class<?> cls)
	{
		List<Field> list = fieldCache.get(cls);
		if (list == null) {
			list = new ArrayList<Field>();
			while (!cls.equals(Object.class)) {
				Field[] fields = cls.getDeclaredFields();
				for (Field f : fields) {
					//int i = f.getModifiers();
					if (!f.isAccessible()) {
						f.setAccessible(true);
					}
					list.add(f);
				}
				cls = cls.getSuperclass();
			}
		}
		return list;
	}

}

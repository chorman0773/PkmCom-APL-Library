package github.lightningcreations.lclib;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Comparator;

public final class LexicographicalCompare<T extends Comparable<T>> implements Comparator<T> {
	private MethodHandle[] fieldGetters;
	
	public static <T extends Comparable<T>> LexicographicalCompare<T> lexicographicalCompare(Lookup lookup,Class<T> cl,String...fieldNames ) {
		return new LexicographicalCompare<>(lookup,cl,fieldNames);
	}
	
	private LexicographicalCompare(Lookup lookup,Class<T> cl,String...fieldNames) {
		try {
			//Use Reflection to get Types of the Variables, then use lookup to get access to the (probably) private field
			fieldGetters = new MethodHandle[fieldNames.length];
			for(int i = 0;i<fieldNames.length;i++) {
				String field = fieldNames[i];
				Field f= cl.getField(field);
				Class<?> type = f.getType();
				fieldGetters[i] = lookup.findGetter(cl, field, type);
			}
		}catch(NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compare(T a,T b) {
		try {
			int res;
			for(MethodHandle m:fieldGetters)
				if((res=((Comparable)m.invoke(a)).compareTo(((Comparable)m.invoke(b))))!=0)
					return res;
			return 0;
		}catch(Error|RuntimeException e) {
			throw e;
		}catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

}

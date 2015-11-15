package com.github.damianmcdonald.alerter.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.log4j.Logger;

import com.github.damianmcdonald.alerter.mail.Mailer;

public class BeanUtils {

  private final static Logger logger = Logger.getLogger(Mailer.class.getName());
  private final static ConvertUtilsBean converter = BeanUtilsBean.getInstance().getConvertUtils();
  
  @SuppressWarnings("rawtypes")
  public static Map<String, String> recursiveDescribe(final Object object) {
    final Set cache = new HashSet();
    return recursiveDescribe(object, null, cache);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Map<String, String> recursiveDescribe(final Object object, String prefix, final Set cache) {
    if (object == null || cache.contains(object)) return Collections.EMPTY_MAP;
    cache.add(object);
    prefix = (prefix != null) ? prefix + "." : "";
    final Map<String, String> beanMap = new TreeMap<String, String>();
    final Map<String, Object> properties = getProperties(object);
    for (String property : properties.keySet()) {
      final Object value = properties.get(property);
      try {
        if (value == null) {
          // ignore nulls
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
          beanMap.putAll(convertAll((Collection) value, prefix + property, cache));
        } else if (value.getClass().isArray()) {
          beanMap.putAll(convertAll(Arrays.asList((Object[]) value), prefix + property, cache));
        } else if (Map.class.isAssignableFrom(value.getClass())) {
          beanMap.putAll(convertMap((Map) value, prefix + property, cache));
        } else {
          beanMap.putAll(convertObject(value, prefix + property, cache));
        }
      } catch (Exception e) {
        if(e.getMessage() != null) logger.error("Error accessing bean attribute: " + e.getMessage());
        // e.printStackTrace();
      }
    }
    return beanMap;
  }

  private static Map<String, Object> getProperties(final Object object) {
    final Map<String, Object> propertyMap = getFields(object);
    // getters take precedence in case of any name collisions
    propertyMap.putAll(getGetterMethods(object));
    return propertyMap;
  }

  private static Map<String, Object> getGetterMethods(final Object object) {
    final Map<String, Object> result = new HashMap<String, Object>();
    BeanInfo info;
    try {
      info = Introspector.getBeanInfo(object.getClass());
      for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
        final Method reader = pd.getReadMethod();
        if (reader != null) {
          final String name = pd.getName();
          if (!"class".equals(name)) {
            try {
              final Object value = reader.invoke(object);
              result.put(name, value);
            } catch (Exception e) {
              if(e.getMessage() != null) logger.error("Error accessing bean attribute: " + e.getMessage());
              // e.printStackTrace();
            }
          }
        }
      }
    } catch (IntrospectionException e) {
      if(e.getMessage() != null) logger.error("Error accessing bean attribute: " + e.getMessage());
      // e.printStackTrace();
    }
    return result;
  }

  private static Map<String, Object> getFields(final Object object) {
    return getFields(object, object.getClass());
  }

  @SuppressWarnings("rawtypes")
  private static Map<String, Object> getFields(final Object object, final Class<?> classType) {
    final Map<String, Object> result = new HashMap<String, Object>();
    final Class superClass = classType.getSuperclass();
    if (superClass != null) result.putAll(getFields(object, superClass));
    // get public fields only
    final Field[] fields = classType.getFields();
    for (Field field : fields) {
      try {
        result.put(field.getName(), field.get(object));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  @SuppressWarnings("rawtypes")
  private static Map<String, String> convertAll(final Collection<Object> values, final String key, final Set cache) {
    final Map<String, String> valuesMap = new HashMap<String, String>();
    final Object[] valArray = values.toArray();
    for (int i = 0; i < valArray.length; i++) {
      final Object value = valArray[i];
      if (value != null) valuesMap.putAll(convertObject(value, key + "(" + i + ")", cache));
    }
    return valuesMap;
  }

  @SuppressWarnings("rawtypes")
  private static Map<String, String> convertMap(final Map<Object, Object> values, final String key, final Set cache) {
    final Map<String, String> valuesMap = new HashMap<String, String>();
    for (Object thisKey : values.keySet()) {
      final Object value = values.get(thisKey);
      if (value != null) valuesMap.putAll(convertObject(value, key + "(" + thisKey + ")", cache));
    }
    return valuesMap;
  }

  @SuppressWarnings("rawtypes")
  private static Map<String, String> convertObject(final Object value, final String key, final Set cache) {
    // if this type has a registered converted, then get the string and return
    if (converter.lookup(value.getClass()) != null) {
      final String stringValue = converter.convert(value);
      final Map<String, String> valueMap = new HashMap<String, String>();
      valueMap.put(key, stringValue);
      return valueMap;
    } else {
      // otherwise, treat it as a nested bean that needs to be described itself
      return recursiveDescribe(value, key, cache);
    }
  }
  
}

package blockchain.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * json工具类.
 *
 * @author wangfeng
 */
public final class JsonUtil {

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
     */
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * <p>
     * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
     * <p>
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("parse json string error:" + jsonString, e);
            return null;
        }
    }

    public static <T> T fromJsonThrows(String jsonString, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonString, clazz);

    }

    /**
     * 返回复杂类型
     *
     * @param jsonString
     * @param reference
     * @return
     */
    public static <T> T fromJson(String jsonString, TypeReference<T> reference) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(jsonString, reference);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("parse json string error:" + jsonString, e);
            return null;
        }
    }

    public static String writeJsonValue(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("write json string error", e);
        }
        return null;
    }

    public static String readNodeValue(String nodeName, String content) {
        try {
            JsonNode node = mapper.readTree(content);
            JsonNode nameNode = node.path(nodeName);
            if (nameNode != null) {
                return nameNode.textValue();
            }
        } catch (IOException e) {
            logger.error("read json tree error", e);
        }

        return null;
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }


    /**
     * 参数名字母顺序排序
     *
     * @param paraMap         参数map
     * @param containParaName 是否包含参数
     * @return
     * @throws
     */
    public static String mapToString(Map<String, String> paraMap, boolean containParaName) {

        return mapToString(paraMap, containParaName, true);
    }


    /**
     * 参数名字母顺序排序
     *
     * @param paraMap         参数map
     * @param containParaName 是否包含参数
     * @return
     * @throws
     */
    public static String mapToString(Map<String, String> paraMap, boolean containParaName, boolean toUpperCase) {

        StringBuffer buff = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : paraMap.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                if (containParaName) {
                    if (toUpperCase) {
                        buff.append(key.toUpperCase());
                    } else {
                        buff.append(key);
                    }
                    buff.append("=");
                }
                buff.append(val);
                buff.append("&");
            }

            if (buff.length() > 0) {
                buff.delete(buff.length() - 1, buff.length()); // 去掉最后的&
            }
        } catch (Exception e) {
            throw e;
        }
        return buff.toString();
    }

    /**
     * jsonString -> map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, String> jsonStringToMap(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        try {
            mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            return mapper.readValue(jsonStr, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("parse json string error:" + jsonStr, e);
            return null;
        }

    }

    /**
     * jsonString -> map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> jsonStringToMap2(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        try {
            mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            return mapper.readValue(jsonStr, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("parse json string error:" + jsonStr, e);
            return null;
        }

    }


    /**
     * jsonString -> map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> parseJSON2Map(String jsonStr) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            //最外层解析
            JSONObject json = JSONObject.fromObject(jsonStr);
            for (Object k : json.keySet()) {
                Object v = json.get(k);
                //如果内层还是数组的话，继续解析
                if (v instanceof JSONArray) {
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    Iterator<JSONObject> it = ((JSONArray) v).iterator();
                    while (it.hasNext()) {
                        JSONObject json2 = it.next();
                        list.add(parseJSON2Map(json2.toString()));
                    }
                    map.put(k.toString(), list);
                } else {
                    map.put(k.toString(), v);
                }
            }
        } catch (Exception e) {
            logger.error("parseJSON2Map error:" + jsonStr, e);
        }
        return map;
    }

    /**
     * jsonString -> map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, String> parseJSON2MapSimple(String jsonStr) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            //最外层解析
            JSONObject json = JSONObject.fromObject(jsonStr);
            for (Object k : json.keySet()) {
                Object v = json.get(k);
                map.put(k.toString(), v.toString());
            }
        } catch (Exception e) {
            logger.error("parseJSON2MapSimple error:" + jsonStr, e);
        }

        return map;
    }


    public static JSONArray sortJsonArray(JSONArray array, Comparator<JSONObject> comparable) {
        if (array == null || array.isEmpty()) {
            return new JSONArray();
        }
        JSONArray result = new JSONArray();
        JSONObject[] objArray = new JSONObject[array.size()];
        for (int index = 0; index < array.size(); index++) {
            objArray[index] = (JSONObject) array.get(index);
        }
        Arrays.sort(objArray, comparable);
        for (JSONObject obj : objArray) {
            result.add(obj);
        }
        return result;
    }

    //南交所倒叙排列比较器,按TRADEDATE和TRADETIME组合倒叙排列
    public static final Comparator<JSONObject> NJS_DATE_JSON_DESC = new Comparator<JSONObject>() {

        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            String o1DateStr = getComDateStr(o1);
            String o2DateStr = getComDateStr(o2);
            return o2DateStr.compareTo(o1DateStr);
        }

        private String getComDateStr(JSONObject o) {
            if (o.get("TRADEDATE") == null || o.get("TRADETIME") == null) {
                throw new RuntimeException("JsonUtils.NJS_DATE_JSON_DESC:不支持比较的json");
            }
            String dateStr = (String) o.get("TRADEDATE") + (String) o.get("TRADETIME");
            return dateStr;
        }
    };

    public static void main(String[] args) {
        int aaa = 1;
        String tt = JsonUtil.fromJson("true", String.class);
        System.out.println(tt);
    }

}

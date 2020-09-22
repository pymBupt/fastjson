package com.alibaba.json.bvt.issue_3300;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author yumin.pym
 */
public class Issue3448 extends TestCase {
    public static class Item {
        private String id;

        public Item() {
        }

        public Item(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class SelfTypeReference<T> {

    }

    public static class SelfTypeReference2<T, R> {

    }

    private <T> List<T> parseList(String text, SelfTypeReference<T> selfTypeReference) {
        Type genericSuperclass = selfTypeReference.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType)genericSuperclass).getActualTypeArguments();
        return JSON.parseObject(text, new TypeReference<List<T>>(actualTypeArguments) {});
    }

    @Test
    public void test_parseList() {
        List<Map<String, List<String>>> list = new ArrayList(4);
        list.add(Collections.singletonMap("key1", Collections.singletonList("item")));
        String text = JSON.toJSONString(list);
        System.out.println("text = " + text);

        List<Map<String, List<String>>> result = parseList(text, new SelfTypeReference<Map<String, List<String>>>() {});
        System.out.println("result = " + result);
        TestCase.assertTrue(result.get(0) instanceof Map);
        TestCase.assertTrue(result.get(0).get("key1").get(0) instanceof String);
    }

    private <K, V> Map<K, V> parseMap(String text, SelfTypeReference2<K, V> selfTypeReference2) {
        Type genericSuperclass = selfTypeReference2.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType)genericSuperclass).getActualTypeArguments();
        return JSON.parseObject(text, new TypeReference<Map<K, V>>(actualTypeArguments) {});
    }

    @Test
    public void test_parseMap() {
        Map<String, String> map = Collections.singletonMap("key1", "value1");
        String text = JSON.toJSONString(map);

        Map<String, String> result = parseMap(text, new SelfTypeReference2<String, String>() {});
        System.out.println("result = " + result);
        TestCase.assertTrue(result instanceof Map);
        TestCase.assertEquals("value1", result.get("key1"));
    }

    private <T1, T2> List<Map<T1, List<T2>>> parseList2(String text, SelfTypeReference2<T1, T2> selfTypeReference2) {
        Type genericSuperclass = selfTypeReference2.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType)genericSuperclass).getActualTypeArguments();
        return JSON.parseObject(text, new TypeReference<List<Map<T1, List<T2>>>>(actualTypeArguments) {});
    }

    @Test
    public void test_parseList2() {
        List<Map<String, List<Item>>> list = new ArrayList(4);
        list.add(Collections.singletonMap("key1", Collections.singletonList(new Item("1001"))));
        String text = JSON.toJSONString(list);
        System.out.println("text = " + text);

        List<Map<String, List<Item>>> result = parseList2(text, new SelfTypeReference2<String, Item>() {});
        System.out.println("result = " + result);
        TestCase.assertTrue(result.get(0) instanceof Map);
        TestCase.assertTrue(result.get(0).get("key1").get(0) instanceof Item);
    }
}

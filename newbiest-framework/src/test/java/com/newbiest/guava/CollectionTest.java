package com.newbiest.guava;

import com.google.common.collect.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by guoxunbo on 2018/5/5.
 */
public class CollectionTest {


    @Test
    public void mapsTest() {
        Map map = Maps.newHashMap();
        assert map != null && map.size() == 0;

        Map<String, String> left = ImmutableMap.of("a", "a", "b", "b");
        Map<String, String> right = ImmutableMap.of("a", "a", "b", "b", "c", "c");

        // 用来比较两个Map以获取所有不同点。该方法返回MapDifference对象，把不同点的维恩图分解
        MapDifference<String, String> difference = Maps.difference(left, right);

        // 两边共有的 键值都一样
        Assert.assertEquals("{a=a, b=b}", difference.entriesInCommon().toString());

        // 只有左边有
        Assert.assertEquals("{}", difference.entriesOnlyOnLeft().toString());
        // 只有右边有
        Assert.assertEquals("{c=c}", difference.entriesOnlyOnRight().toString());

        // 创建期望大小的Map
        Maps.newHashMapWithExpectedSize(100);

        Maps.newConcurrentMap();
        Maps.newIdentityHashMap();
        Maps.newTreeMap();
        Maps.newLinkedHashMap();
    }

    @Test
    public void setsTest() {
        // 初始化Set元素
        Set set = Sets.newHashSet();
        assert set != null && set.size() == 0;
        // 初始化Set元素
        Set set1 = Sets.newHashSet(1, 2,3);
        Assert.assertEquals(3, set1.size());
        Assert.assertEquals("[1, 2, 3]", set1.toString());


        Set<Integer> set2 = Sets.newHashSet(1,2,3,4,5);
        Set<Integer> set3 = Sets.newHashSet(3,4,5,6);

        //SetView继承了Set故可以直接当Set使用
        //交集
        Sets.SetView<Integer> inter = Sets.intersection(set2, set3);
        Assert.assertEquals("[3, 4, 5]", inter.toString());
        // 将Set拷贝到一个不可变集合之中
        Set setx = inter.immutableCopy();

        //差集,在A中不在B中
        Sets.SetView<Integer> diff = Sets.difference(set2, set3);
        Assert.assertEquals("[1, 2]", diff.toString());

        //并集
        Sets.SetView<Integer> union = Sets.union(set2, set3);
        Assert.assertEquals("[1, 2, 3, 4, 5, 6]", union.toString());

        // 计算多个Set的笛卡尔积
        Set<String> setA = ImmutableSet.of("a", "b");
        Set<String> setB = ImmutableSet.of("c", "d", "e");

        Set<List<String>> cartesian = Sets.cartesianProduct(setA, setB);
        Assert.assertEquals("[[a, c], [a, d], [a, e], [b, c], [b, d], [b, e]]", cartesian.toString());

        // 返回Set的所有子集
        // [] [a] [b] [a, b]
        Set<Set<String>> powerSet = Sets.powerSet(setA);

        Sets.newConcurrentHashSet();
        Sets.newTreeSet();
        Sets.toImmutableEnumSet();
        Sets.newLinkedHashSet();
        Sets.newIdentityHashSet();
    }

    @Test
    public void listsTest() {
        // 创建集合
        List<Integer> list = Lists.newArrayList();
        assert list != null && list.size() == 0;
        // 初始化集合元素
        List<Integer> list1 = Lists.newArrayList(1, 2,3);
        Assert.assertEquals(3, list1.size());
        Assert.assertEquals("[1, 2, 3]", list1.toString());
        // 反转
        list1 = Lists.reverse(list1);
        Assert.assertEquals("[3, 2, 1]", list1.toString());

        // 把List按指定大小分割 分割出来的元素为1个元素
        List<List<Integer>> list2 = Lists.partition(list1, 1);
        Assert.assertEquals(3, list2.get(0).get(0), 0.0);
        Assert.assertEquals(2, list2.get(1).get(0), 0.0);
        Assert.assertEquals(1, list2.get(2).get(0), 0.0);

        // 创建一个最大只能100个元素的集合
        Lists.newArrayListWithCapacity(100);
        // 创建一个最大可能100的元素的集合
        Lists.newArrayListWithExpectedSize(100);

        Lists.newCopyOnWriteArrayList();
        Lists.newLinkedList();

    }

    /**
     * 不可变集合的创建方式
     */
    @Test
    public void immutableTest() {
        // 用of创建
        ImmutableSet<String> s1 = ImmutableSet.of("a","b","c","d");
        // 用copyOf创建
        ImmutableSet<String> s2 = ImmutableSet.copyOf(s1);
        // 通过builder创建
        ImmutableSet<String> s3 = ImmutableSet.<String>builder().addAll(s2).add("e").build();
        Assert.assertEquals("[a, b, c, d]", s1.toString());
        Assert.assertEquals("[a, b, c, d]", s2.toString());
        Assert.assertEquals("[a, b, c, d, e]", s3.toString());
        // 转成ImmutableList
        ImmutableList<String> l1 = s3.asList();
        Assert.assertEquals("[a, b, c, d, e]", l1.toString());

    }

    @Test
    public void multisetTest() {
        Multiset<String> s1 = LinkedHashMultiset.create();
        s1.add("a");
        s1.add("a");
        s1.add("a");
        s1.add("a");
        s1.add("b");
        // 添加或删除指定元素使其在集合中的数量是count 比如让a在s1中的数量是5
        s1.setCount("a",5);
        // 统计元素在集合中的数量
        Assert.assertEquals(5, s1.count("a"));
        Assert.assertEquals(1, s1.count("b"));
        Assert.assertEquals("[a, a, a, a, a, b]", s1.toString());
        //所有元素计数的总和,包括重复元素
        Assert.assertEquals(6, s1.size());
        //所有元素计数的总和,不包括重复元素
        Assert.assertEquals(2, s1.elementSet().size());
        //清空集合
        s1.clear();
        Assert.assertEquals(0, s1.size());
    }

    @Test
    public void multimapTest() {
        //Multimap是把键映射到任意多个值的一般方式  相当于Map<String, Collection<Integer>> map
        Multimap<String,Integer> map = HashMultimap.create();
        map.put("a",1); //key相同时不会覆盖原value
        map.put("a",2);
        map.put("a",3);
        map.put("b",1);
        map.put("b",2);

        Assert.assertEquals("{a=[1, 2, 3], b=[1, 2]}", map.toString());

        //get返回的是集合
        Assert.assertEquals(3, map.get("a").size());
        Assert.assertEquals(2, map.get("b").size());

        //返回所有”键-单个值映射”的个数
        Assert.assertEquals(5, map.size());
        // 转换从普通map
        Map<String, Collection<Integer>> mapView = map.asMap();
    }

    @Test
    public void biMapTest() {
        BiMap<String,String> biMap= HashBiMap.create();
        biMap.put("a1","a");
        biMap.put("b1","b");
        biMap.put("a1","c");

        Assert.assertEquals("c", biMap.get("a1"));
        try {
            // 当值
            biMap.put("c1","c");
        } catch (Exception e) {
            Assert.assertEquals("value already present: c", e.getMessage());
        }
        // 强制替换键
        biMap.forcePut("c1","c");
        Assert.assertEquals("{b1=b, c1=c}", biMap.toString());

        //通过value找key 即将值和key互换位置
        Assert.assertEquals("c1", biMap.inverse().get("c"));
        Assert.assertEquals("{b=b1, c=c1}", biMap.inverse().toString());
    }

    @Test
    public void tableTest() {
        //Table<Row, Column, Value>
        Table<String,String,Integer> table = HashBasedTable.create();
        table.put("xiaoming","java",110);
        table.put("xiaoming","c",120);

        table.put("xiaogang","java",111);
        table.put("xiaogang","c",122);

        Assert.assertEquals("{xiaoming={java=110, c=120}, xiaogang={java=111, c=122}}", table.toString());

        Set<Table.Cell<String,String,Integer>> cells = table.cellSet();
        Assert.assertEquals(4, cells.size());

        Assert.assertEquals("{java=110, c=120}", table.row("xiaoming").toString());
        Assert.assertEquals("111", table.row("xiaogang").get("java").toString());

        Assert.assertEquals("[xiaoming, xiaogang]", table.rowKeySet().toString());
        Assert.assertEquals("[java, c]", table.columnKeySet().toString());

        Assert.assertEquals("[110, 120, 111, 122]", table.values().toString());
    }
}

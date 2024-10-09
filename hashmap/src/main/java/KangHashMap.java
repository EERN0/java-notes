import java.util.Objects;

public class KangHashMap<K extends Comparable<K>, V> {
    static class Node<K extends Comparable<K>, V> {
        private K key;
        private V val;

        private Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.val = value;
        }
    }

    // 默认容量
    private final int DEFAULT_CAPACITY = 16;
    private final float LOAD_FACTOR = 0.75f;

    // hashmap的大小
    private int size;
    // 桶数组
    private Node<K, V>[] bucket;

    // 构造器
    public KangHashMap() {
        bucket = new Node[DEFAULT_CAPACITY];
        size = 0;
    }

    public KangHashMap(int capacity) {
        bucket = new Node[capacity];
        size = 0;
    }


    // 根据key计算索引下标
    private int getIndex(K key) {
        int hashCode = key.hashCode();
        int idx = hashCode % bucket.length;
        return Math.abs(idx);
    }

    // 添加元素
    public void put(K key, V val) {
        if (size >= bucket.length * LOAD_FACTOR) {
            resize();
        }
        // 添加元素
        putVal(key, val, bucket);
    }

    // 真正进行添加元素
    public void putVal(K key, V val, Node<K, V>[] table) {
        int idx = getIndex(key);
        Node<K, V> node = table[idx];

        if (node == null) {
            table[idx] = new Node<>(key, val);
            size++;
        } else {
            // 发生了冲突
            Node<K, V> tail = null;
            while (node != null) {
                // 冲突链表上有key相同的节点，更新val
                if (node.key.hashCode() == key.hashCode() && Objects.equals(node.key, key)) {
                    node.val = val;
                    return;
                }
                tail = node;
                node = node.next;
            }
            // 当前key不在链表上，插入链表尾部
            tail.next = new Node<>(key, val);
            size++;
        }
    }

    // 查找元素
    public V get(K key) {
        int idx = getIndex(key);
        Node<K, V> node = bucket[idx];
        if (node == null) return null;

        // 查找整个哈希槽
        while (node != null) {
            if (node.key.hashCode() == key.hashCode() && (Objects.equals(node.key, key))) {
                return node.val;
            }
            node = node.next;
        }
        return null;
    }

    // hashmap中元素个数
    public int size() {
        return size;
    }

    // 扩容
    private void resize() {
        // 创建两倍大小的数组
        Node<K, V>[] newBuckets = new Node[bucket.length * 2];
        // 把元素挪动到新桶数组
        rehash(newBuckets);
    }

    // 把元素搬到新数组
    private void rehash(Node<K, V>[] newBuckets) {
        // 重新计算大小 putVal方法会更新size
        size = 0;

        for (int i = 0; i < bucket.length; i++) {
            Node<K, V> node = bucket[i];
            if (node == null) {
                continue;
            }
            // 把整个哈希槽内的所有元素搬完
            while (node != null) {
                // 元素放入新数组
                putVal(node.key, node.val, newBuckets);
                node = node.next;
            }
        }
    }

    public static void main(String[] args) {
        KangHashMap<String,String> map = new KangHashMap();
        for (int i = 0; i < 100; i++) {
            map.put("刘华强" + i, "你这瓜保熟吗？" + i);
        }
        System.out.println(map.size());
        for (int i = 0; i < 100; i++) {
            System.out.println(map.get("刘华强" + i));
        }


        map.put("刘华强1","哥们，你这瓜保熟吗？");
        map.put("刘华强1","你这瓜熟我肯定要啊！");
        System.out.println(map.get("刘华强1"));
    }
}

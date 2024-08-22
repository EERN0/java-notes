import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class myUnsafe {
    public static Unsafe reflectGetUnsafe() {
        try {
            // 获得私有unsafe对象
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            ((Field) theUnsafe).setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

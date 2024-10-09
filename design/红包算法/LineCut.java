import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LineCut {
    public static List<Double> generateRedPackets(double totalMoney, int n) {
        List<Double> cuts = new ArrayList<>();
        Random rand = new Random();

        // 生成 n-1 个切点，把[0,totalMoney]分成 n 段
        for (int i = 0; i < n - 1; i++) {
            cuts.add(Math.round(rand.nextDouble() * (totalMoney - 0.01) * 100.0) / 100.0 + 0.01);
        }
        // 加上左右边界
        cuts.add(0.0);
        cuts.add(totalMoney);

        // 排序切分点
        Collections.sort(cuts);

        List<Double> redPackets = new ArrayList<>();

        // 计算每段长度 cuts.get(i+1)-cuts.get(i) 作为红包金额
        for (int i = 0; i < n; i++) {
            // 浮点数运算，四舍五入
            double money = Math.round((cuts.get(i + 1) - cuts.get(i)) * 100.0) / 100.0;
            redPackets.add(money);
        }
        return redPackets;
    }

    public static void main(String[] args) {
        List<Double> packets = generateRedPackets(100, 5);
        for (Double packet : packets) {
            System.out.print(packet + " ");
        }
    }
}

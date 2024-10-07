/*
二倍均值法：假设剩余的总金额是 M，还有 n 个人没有分配红包，那么对于当前要分配的人，其红包金额是一个随机数，最大值不超过当前剩余金额的两倍平均值

算法步骤：
1.假设有n个红包，总金额为M
2.对于第i个人，随机金额为 random(0.01, M/n * 2)，即当前剩余金额的两倍均值，最后一个人拿走所有的钱
3.将这一金额从剩余的金额中扣除，剩余的 n-1 个人再分配剩下的钱，重复该过程直到所有红包分配完
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoubleMean {
    /**
     * @param money 钱
     * @param cnt   红包数
     */
    public static List<Double> generateRedPackets(double money, int cnt) {
        List<Double> redPackets = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < cnt; i++) {
            if (cnt - i == 1) {     // 最后一个人拿剩余的所有钱
                redPackets.add(Math.round(money * 100.0) / 100.0);
            } else {
                // 计算二倍均值法中的最大值
                double maxMoney = money / (cnt - i) * 2;
                // 生成随机数
                double random = Math.round(rand.nextDouble() * (maxMoney - 0.01) * 100.0) / 100.0 + 0.01;
                redPackets.add(random);
                money -= random;
            }
        }
        return redPackets;
    }

    public static void main(String[] args) {
        List<Double> packets = generateRedPackets(10, 5);
        for (Double packet : packets) {
            System.out.println(packet);
        }
    }
}

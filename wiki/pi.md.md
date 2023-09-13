![PI公式](https://images.gitee.com/uploads/images/2020/0914/134902_2e358399_1971.png "屏幕截图.png")

```
static BigDecimal pi(int max, int scale) {
    String s = "1.0";
    BigDecimal pi = new BigDecimal("1.0");
    BigDecimal one = new BigDecimal("1.0");
    for (int i = max; i > 0; i --) {
        s = 1 + "+" + i + "/(" + (2.0 * i + 1) + ")*(" + s + ")";
        pi = one.add(new BigDecimal(i * 1.0)
                    .divide(new BigDecimal(2.0 * i + 1), scale, BigDecimal.ROUND_HALF_UP)
                    .multiply(pi))
                .setScale(scale, BigDecimal.ROUND_HALF_UP);
    }
    sysout(s);
    return pi.multipy(new BigDecimal("2.0")).setScale(scale, BigDecimal.ROUND_HALF_UP);
}
```
import java.math.BigInteger;

class DeriItem { //求导项
    private BigInteger deriCoeff = BigInteger.ZERO;
    private BigInteger deriExpo = BigInteger.ZERO;

    public BigInteger getDeriCoeff() {
        return deriCoeff;
    }

    public BigInteger getDeriExpo() {
        return deriExpo;
    }

    public void deri(BigInteger coeff, BigInteger expo) { //设置求导后的数
        if (coeff.equals(BigInteger.ZERO) || expo.equals(BigInteger.ZERO)) {
            //do nothing，still
        } else if (!coeff.equals(BigInteger.ZERO)) {
            deriCoeff = coeff.multiply(expo); // 导数系数 = 原系数*指数
            deriExpo = expo.subtract(BigInteger.ONE);//导数幂 = 指数-1
        }
    }

    public void fixCoeff(BigInteger coeff2) { // add together
        deriCoeff = deriCoeff.add(coeff2);
    }
}

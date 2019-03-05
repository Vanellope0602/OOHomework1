import java.math.BigInteger;

class Item { //原始项
    private BigInteger coeff = BigInteger.ZERO;
    private BigInteger exponent = BigInteger.ZERO;
    //先提取出coeff系数，然后再提取出exponent次数

    public BigInteger getCoeff() {
        return coeff;
    }

    public BigInteger getExponent() {
        return exponent;
    }

    private boolean xoccur = false;
    private boolean isNeg = false;
    private boolean isNeg2 = false;
    private boolean haveCoe = false;
    private boolean haveExp = false;

    public void Get(String string) { //拿已经分割好的一个项进来提取，确保合法？
        char[] temp = string.toCharArray();
        for (int i = 0; i < temp.length; ) {
            if (temp[i] == '-' && !xoccur) {
                isNeg = !isNeg;
                i++;
            }
            else if (Character.isDigit(temp[i]) && !xoccur) { //是数字，目前为系数，尚未读到x
                haveCoe = true;
                int j = i;
                String num = "";
                while (Character.isDigit(temp[j])) {
                    num = num + temp[j]; // 提取数字
                    j++;
                }
                if (isNeg) {
                    num = "-" + num;
                }
                coeff = new BigInteger(num, 10);
                i = j;
            }
            else if (temp[i] == 'x') {
                xoccur = true;
                i++;
            }
            else if (temp[i] == '-' && xoccur) {
                isNeg2 = !isNeg2; //幂次变为相反数
                i++;
            }
            else if (Character.isDigit(temp[i]) && xoccur) { //是数字在x后面，为幂次
                haveExp = true;
                int k = i + 1;
                String num1 = "" + temp[i];
                while (k < temp.length && Character.isDigit(temp[k])) {
                    num1 = num1 + temp[k];
                    k++;
                } //按理说此时已经读完了

                if (isNeg2) {
                    num1 = "-" + num1;
                }
                exponent = new BigInteger(num1, 10);
                break;
            }
            else { // 跳过空格，^符号
                i++;
                continue;
            }

            if (!haveCoe && xoccur) { //出现了x,没写系数，但是有可能有符号，为1或-1
                if (isNeg) {
                    coeff = new BigInteger("-1",10);
                }
                else {
                    coeff = BigInteger.ONE;
                }
            }
            if (!haveExp && xoccur) {
                exponent = BigInteger.ONE; //没写次数，默认为1
            }
        }

    }
}
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

public class Derivation {
    //在这里写一个合并同类项的方法

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        String input = s.nextLine();
        input = "+" + input;
        List<String> poly = new ArrayList<String>();//原始分割字符串
        List<String> deripoly = new ArrayList<String>(); //求导后的字符串

        String reg = "(\\s*[+-]\\s*[+-]?)" +
                "(\\d+\\s*(\\*))?((\\s*x\\s*)(\\s*(\\^)\\s*[+-]?\\d+)?)?";
        Pattern p = Pattern.compile(reg);
        //无法识别常数项，其实不需要识别常数项，全为常数的时候判断一下合法即可
        //如果List中出现3个符号连着一组，则WRONG FORMAT
        Matcher m = p.matcher(input);
        //重新做一个非法输入判定，只需要找序列里面有没有非法情况即可
        //整数之间有空格？| - - 16符号与数字有空格？ | 三个连续符号？| 系数x之间 * ？ | 空串？
        boolean valid = true;
        Pattern v = Pattern.compile("\\d+[x]");
        Matcher mv = v.matcher(input);
        if (mv.matches()) {
            System.out.println("WRONG FORMAT!");
            valid = false;
            //return; // exit the program!
        } // 判断非法还是有问题，可以单独写一个判断非法的函数

        Item[] items = new Item[1000];//最长不超过1000
        DeriItem[] deriItems = new DeriItem[1000];
        int i = 0;

        while (m.find()) {
            poly.add(m.group()); // now m.group() is a separate string
            items[i] = new Item();//这个items[i]可能是无效的，要WRONG FORMAT
            System.out.println("this group is " + m.group());
            items[i].Get(m.group()); // 提取系数和次数
            System.out.println("the coeff is " + items[i].getCoeff()
                    + " the exp is " + items[i].getExponent());

            deriItems[i] = new DeriItem();
            deriItems[i].deri(items[i].getCoeff(), items[i].getExponent());//设置求导后系数和次数

            if (!deriItems[i].getDeriCoeff().equals(BigInteger.ZERO)) { // 系数有效
                deripoly.add(deriItems[i].execute()); //把求导后字符串加入deripoly
            }//这一步先不要做
            i++; // the num of groups
        }
        //已经制作好原始正确答案的表达式，接下来合并即可
        //发现问题，合并要在没有转化为
        System.out.println(deripoly);

    }

}

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
        System.out.println("this char[] length is " + temp.length);
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
                coeff = (isNeg) ? new BigInteger("-1",10) : BigInteger.ONE;
            }
            if (!haveExp && xoccur) {
                exponent = BigInteger.ONE; //没写次数，默认为1
            }
        }

    }
}

class DeriItem { //求导项
    private BigInteger deriCoeff = BigInteger.ZERO;
    private BigInteger deriExpo = BigInteger.ZERO;

    public BigInteger getDeriCoeff() {
        return deriCoeff;
    }

    public BigInteger getDeriExpo() {
        return deriExpo;
    }

    public void deri(BigInteger coeff, BigInteger expo) {//设置求导后的数
        if (coeff.equals(BigInteger.ZERO) || expo.equals(BigInteger.ZERO)) { //系数为0
            //do nothing，still
        }
        else if (!coeff.equals(BigInteger.ZERO)) {
            deriCoeff = coeff.multiply(expo); // 导数系数 = 原系数*指数
            deriExpo = expo.subtract(BigInteger.ONE);//导数幂 = 指数-1
        }
    }

    public String execute() {
        if (this.getDeriExpo().equals(BigInteger.ZERO)) { // 次数为0，不用写*x^
            return (this.getDeriCoeff().toString());
        }
        else if (this.getDeriExpo().equals(BigInteger.ONE)) { //次数为1，不用写^
            if (this.getDeriCoeff().equals(BigInteger.ONE)) { //系数为1，不写1
                return ("x");
            }
            return (this.getDeriCoeff().toString() + "*x");
        }
        else {
            if (this.getDeriCoeff().equals(BigInteger.ONE)) { //次数大，系数1
                return ("x^" + this.getDeriExpo().toString());
            }
            return (this.getDeriCoeff().toString() + "*x^" + this.getDeriExpo().toString());
        }
    }

}
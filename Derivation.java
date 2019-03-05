import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.math.BigInteger;
import java.util.Scanner;

public class Derivation {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        String input = s.nextLine();
        input = input.trim(); // clear space
        if (input.isEmpty() || input.matches("\\s*")) { //空串判断
            //System.out.println("empty");
            System.out.println("WRONG FORMAT!");
            return;
        }
        if (input.startsWith("*") || input.startsWith("^") || input.endsWith("^")
                || input.endsWith("*") || input.endsWith("+") || input.endsWith("-")) {
            System.out.println("WRONG FORMAT!");
            return;
        }

        List<String> poly = new ArrayList<String>();//原始分割字符串
        List<DeriItem> deripoly = new ArrayList<DeriItem>(); //求导后的项

        String reg = "(\\s*[+-]?\\s*[+-]?)" +
                "(\\d+\\s*(\\*))?((\\s*x\\s*)(\\s*(\\^)\\s*[+-]?\\d+)?)?";
        Pattern p = Pattern.compile(reg);
        //无法识别常数项，其实不需要识别常数项，全为常数的时候判断一下合法即可
        //如果List中出现3个符号连着一组，则WRONG FORMAT
        Matcher m = p.matcher(input);
        //重新做一个非法输入判定，只需要找序列里面有没有非法情况即可
        //整数之间有空格？| - - 16符号与数字有空格？ | 三个连续符号？| 系数x之间 * ？| 非法字符
        Pattern v = Pattern.compile("(\\d+\\s+\\d+)|(\\d+\\s*x)|(\\d+\\s*\\^)" +
                "|(x\\s*\\d+)" + // 9 9, 9x, x9
                "|([^0-9x(+)(\\-)(\\*)(\\^) \\t\\r])" + //invalid
                "|(\\d+\\s*\\*\\s*\\d+)|(\\d+\\s*(\\^)\\s*\\d+)" + // 7*7, 7^7
                "|([+-]\\s*[+-]\\s+\\d+)"); // - - 3, ++ 4
        Matcher mv = v.matcher(input);
        if (mv.find()) { // 寻找非法组
            //System.out.println(mv.group() + " ");
            System.out.println("WRONG FORMAT!");
            return; // exit the program!
        }
        Pattern v2 = Pattern.compile("(^[\\*])|(^[\\^])" +
                "|([+-]\\s*\\^)|(\\*\\s*\\^)|(x\\s*\\*\\s*x)|(x\\s*\\*\\s*\\d+)" +
                "|(\\*\\s*[+-])|(\\^\\s*\\*)|([+-]\\s*\\*)" +
                "|((\\s*[+-]\\s*){3,})|(\\^\\s*x)"); // +-+
        Matcher mv2 = v2.matcher(input);
        if (mv2.find()) {
            //System.out.println(mv2.group() + " ");
            System.out.println("WRONG FORMAT!");
            return; // exit the program!
        }

        Item[] items = new Item[1000];//最长不超过1000
        DeriItem[] deriItems = new DeriItem[1000];//一堆类
        BigInteger temp;//用来存出现过的指数 幂，while里面直接合并同类项
        BigInteger minusOne = BigInteger.ZERO.subtract(BigInteger.ONE);
        int i = 0;


        while (m.find()) {
            poly.add(m.group()); // now m.group() is a separate string
            items[i] = new Item();//这个items[i]可能是无效的，要WRONG FORMAT

            items[i].Get(m.group()); // 提取系数和次数

            deriItems[i] = new DeriItem();
            deriItems[i].deri(items[i].getCoeff(), items[i].getExponent());
            if (deriItems[i].getDeriCoeff().equals(BigInteger.ZERO)) {
                i++;
                continue; // ignore
            }

            temp = deriItems[i].getDeriExpo();//当前项求导后的指数
            boolean merge = false;
            for (int j = 0; j < i; j++) { // j 从0开始一个个比较是否出现过当前幂次数
                if (deriItems[j].getDeriExpo().equals(temp)) {
                    deriItems[j].fixCoeff(deriItems[i].getDeriCoeff());
                    merge = true;
                    break;
                }
            }
            if (merge) {
                continue;
            }
            else {
                deripoly.add(deriItems[i]); //把求导后的deriItems[]加入deripoly
            }
            //如果没有merge，说明为首个项，或没有找到同类项，那么
            i++; // the num of groups
        }
        boolean first = true;
        boolean allzeros = true;
        for (int k = 0; k < i; k++) {
            BigInteger co = deriItems[k].getDeriCoeff();
            if (!co.equals(BigInteger.ZERO)) {
                allzeros = false;
            }
        }
        if (allzeros) {
            System.out.println("0");
            return;
        }

        for (int k = 0; k < i; k++) {
            BigInteger co = deriItems[k].getDeriCoeff();
            BigInteger ex = deriItems[k].getDeriExpo();
            if (co.equals(BigInteger.ZERO)) { //若系数为0，则直接跳过
                continue;
            }
            else { //系数不为0，判断是否为+-1？先输出系数
                if (k == 0 || first) { //第一项被输出的系数 不用输出加号
                    if (co.equals(minusOne)) { //系数为-1
                        if (ex.equals(BigInteger.ZERO)) {
                            System.out.print("-1"); //次数为0
                        }
                        else { //次数不为0 -x^2
                            System.out.print("-"); //系数为-1，则输出 '-'
                        }

                    }
                    else { // 不为-1，直接输出系数
                        System.out.print(co);
                    }
                    first = false;
                }
                else { // 后面的项，根据符号
                    if (co.compareTo(BigInteger.ZERO) == 1) { //系数大于0
                        if (co.equals(BigInteger.ONE)) { //系数为1
                            System.out.print("+");//系数为1，免写
                        }
                        else {
                            System.out.print("+" + deriItems[k].getDeriCoeff());
                        }
                    }
                    else { //系数小于0，直接输出系数
                        if (co.equals(minusOne)) { //系数为-1
                            System.out.print("-");
                        }
                        else { //小于0 ，且不是-1，则直接输出带符号数字
                            System.out.print(deriItems[k].getDeriCoeff());
                        }

                    }
                } //系数输出完毕

                //然后看次数
                if (!ex.equals(BigInteger.ONE)) { //次数不等于1
                    if (co.equals(BigInteger.ONE)) {
                        if (!ex.equals(BigInteger.ZERO)) {
                            System.out.print("x^" + ex);
                        }
                    }
                    else if (!ex.equals(BigInteger.ZERO)) { //系数不为1，且次数不为0
                        System.out.print("*x^" + ex);
                    }
                    else { //次数为0
                        //nothing
                    }
                }
                else if (ex.equals(BigInteger.ONE)) {
                    System.out.print("*x");//次数等于1
                } //次数为0则写常数项

            }
        }
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

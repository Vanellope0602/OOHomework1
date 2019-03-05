import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.math.BigInteger;
import java.util.Scanner;

public class Derivation {

    private Item[] items = new Item[1000];//最长不超过1000
    private DeriItem[] deriItems = new DeriItem[1000];//一堆类

    public BigInteger coeff(int k) { //
        return deriItems[k].getDeriCoeff();
    }

    public BigInteger exopo(int k) {
        return deriItems[k].getDeriExpo();
    }

    private int totalItemNum = 0;

    public int getTotalItemNum() {
        return totalItemNum;
    }

    public void setTotalItemNum(int i) {
        totalItemNum = i;
    }

    public void findInvalid(Matcher m) {
        List<String> poly = new ArrayList<String>();//原始分割字符串
        List<DeriItem> deripoly = new ArrayList<DeriItem>(); //求导后的项
        BigInteger temp;//用来存出现过的指数 幂，while里面直接合并同类项
        int i = 0;
        while (m.find()) {
            //System.out.println(m.group()); // 砍掉前面这一项然后在 find紧跟后面的这一项是否合法？
            poly.add(m.group()); // now m.group() is a separate string
            items[i] = new Item();//这个items[i]可能是无效的，要WRONG FORMAT

            items[i].Get(m.group()); // 提取系数和次数

            deriItems[i] = new DeriItem(); //然后设置导数系数和次数
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
        setTotalItemNum(i);
    }

    public void printZero() {
        int i = getTotalItemNum();
        boolean allzeros = true;
        for (int k = 0; k < i; k++) {
            if (!coeff(k).equals(BigInteger.ZERO)) {
                allzeros = false;
            }
        }
        if (allzeros) {
            System.out.println("0");
            return;
        }
    }

    public void print() { // it's not zero!
        int i = getTotalItemNum();
        BigInteger minusOne = BigInteger.ZERO.subtract(BigInteger.ONE);
        String output = "";
        for (int k = 0; k < i; k++) {
            if (coeff(k).equals(BigInteger.ZERO)) { //若系数为0，则直接跳过
                continue;
            }
            else { //coeff not zero
                if (coeff(k).equals(BigInteger.ONE)) { // 系数为1
                    if (exopo(k).equals(BigInteger.ZERO)) { //指数为0
                        output = output + "+1";
                    }
                    else if (exopo(k).equals(BigInteger.ONE)) {
                        output = output + "+x";
                    }
                    else {
                        output = output + "+x^" + exopo(k);
                    }
                }
                else if (coeff(k).equals(minusOne)) { // 系数为-1
                    if (exopo(k).equals(BigInteger.ZERO)) { //指数为0
                        output = output + "-1";
                    }
                    else if (exopo(k).equals(BigInteger.ONE)) {
                        output = output + "-x";
                    }
                    else {
                        output = output + "-x^" + exopo(k);
                    }
                }
                else if (coeff(k).compareTo(BigInteger.ZERO) == 1) { // > 0
                    if (exopo(k).equals(BigInteger.ZERO)) { //指数为0
                        output = output + "+" + coeff(k);//加系数
                    }
                    else if (exopo(k).equals(BigInteger.ONE)) {
                        output = output + "+" + coeff(k) + "*x";
                    }
                    else { //有必要写指数
                        output = output + "+" + coeff(k) + "*x^" + exopo(k);
                    }
                }
                else { // <0
                    if (exopo(k).equals(BigInteger.ZERO)) { //指数为0
                        output = output + coeff(k);//系数自带符号
                    }
                    else if (exopo(k).equals(BigInteger.ONE)) {
                        output = output + coeff(k) + "*x";
                    }
                    else { //有必要写指数
                        output = output + coeff(k) + "*x^" + exopo(k);
                    }
                }
            }
        }
        if (output.startsWith("+")) {
            output = output.substring(1);
        }
        System.out.println(output);
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        String input = s.nextLine();
        input = input.trim(); // clear space
        //input = "+" + input;
        if (input.isEmpty() || input.matches("\\s*")) { //空串判断
            //System.out.println("empty");
            System.out.println("WRONG FORMAT!");
            return;
        }
        if (input.startsWith("*") || input.startsWith("^")
                || input.endsWith("^")
                || input.endsWith("*") || input.endsWith("+")
                || input.endsWith("-")) {
            System.out.println("WRONG FORMAT!");
            return;
        }
        //要解决首项允许三个字符存在的情况

        String reg = "(\\s*[+-]?\\s*[+-]?)" + //每一项都必须符号开头
                "(\\d+\\s*(\\*))?((\\s*x\\s*)(\\s*(\\^)\\s*[+-]?\\d+)?)?";
        Pattern p = Pattern.compile(reg);
        //无法识别常数项，其实不需要识别常数项，全为常数的时候判断一下合法即可
        //如果List中出现3个符号连着一组，则WRONG FORMAT
        Matcher m = p.matcher(input);
        //重新做一个非法输入判定，只需要找序列里面有没有非法情况即可
        //整数之间有空格？| - - 16符号与数字有空格？ | 三个连续符号？| 系数x之间 * ？| 非法字符
        Pattern v = Pattern.compile("(\\d+\\s+\\d+)|(\\d+\\s*x)|(\\d+\\s*\\^)" +
                "|(x\\s*\\d+)|(\\^\\s*[+-]\\s+\\d+)" + // 9 9,9x,x9,7^,x^-  2分离
                "|([^0-9x(+)(\\-)(\\*)(\\^) \\t\\r])" + //invalid
                "|(\\d+\\s*\\*\\s*\\d+)|(\\d+\\s*(\\^)\\s*\\d+)" + // 7*7, 7^7
                "|([+-]\\s*[+-]\\s+\\d+)"); // - - 3, ++ 4
        Matcher mv = v.matcher(input);
        if (mv.find()) { // 寻找非法组
            //System.out.println(mv.group() + " invalid");
            System.out.println("WRONG FORMAT!");
            return; // exit the program!
        }
        Pattern v2 = Pattern.compile("(\\^\\s*[+-]?\\s*\\d+\\s*\\*)" + // ^2 *
                "|([+-]\\s*\\^)|(\\*\\s*\\^)|(\\*\\s*\\*)" + // + ^, *  ^ , **
                "|(x\\s*\\*?\\s*x)|(\\^\\s*\\*?\\s*\\^)" + // xx, x*x, ^^, ^*^
                "|(x\\s*\\*\\s*\\d+)" + // x*2
                "|(\\*\\s*[+-])|(\\^\\s*\\*)|([+-]\\s*\\*)" + // *-, +*
                "|((\\s*[+-]\\s*){3,})|(\\^\\s*x)"); // +-+, ^x
        Matcher mv2 = v2.matcher(input);
        if (mv2.find()) { //找非法组2
            //System.out.println(mv2.group() + " invalid2");
            System.out.println("WRONG FORMAT!");
            return; // exit the program!
        }

        Derivation d = new Derivation();
        d.findInvalid(m);
        d.printZero();
        d.print();

    }

}

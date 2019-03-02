import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

public class Derivation {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        String input = s.nextLine();
        input = "+" + input;
        List<String> poly = new ArrayList<String>();

        String reg = "(\\s*[+-]\\s*[+-]?)(\\d+\\s*(\\*))?((\\s*x\\s*)(\\s*(\\^)\\s*[+-]?\\d+)?)?";
        Pattern p = Pattern.compile(reg);
        //无法识别常数项，其实不需要识别常数项，全为常数的时候判断一下合法即可
        //如果List中出现3个符号连着一组，则WRONG FORMAT
        Matcher m = p.matcher(input);
        //重新做一个非法输入判定，只需要找序列里面有没有非法情况即可
        //整数之间有空格？| - - 16符号与数字有空格？ | 三个连续符号？ | 空串？
        boolean valid = true;
        Pattern v = Pattern.compile("(\\d+\\s+\\d+)|([+-]{2}\\s+\\d+)");
        Matcher mv = v.matcher(input);
        if (mv.matches()) {
            System.out.println("WRONG FORMAT!");
            valid = false;
            //return; // exit the program!
        }// 判断非法还是有问题，可以单独写一个判断非法的函数

        Item[] items = new Item[1000];//最长不超过1000
        int i = 0;

        while(m.find()) {
                poly.add(m.group()); // now m.group() is a separate string
                //= m.group();
                items[i] = new Item();//这个items[i]可能是无效的，要WRONG FORMAT

                String temp = items[i].deri(m.group()); // 返回求导后的单个项
                System.out.println(temp);

                System.out.println("this group is " + m.group());
                System.out.println("the arraylist is " + poly);
                i++;
            }

    }


}
class Item {
    private BigInteger coeff = BigInteger.ZERO;
    private boolean co = true; // 先读系数
    private boolean x = false;
    private BigInteger exponent = BigInteger.ZERO;
    //先提取出coeff系数，然后再提取出exponent次数

    private boolean isNeg = false;
    private boolean isNeg2 = false;
    private boolean haveCoe = false;
    private boolean haveExp = false;


    public String deri(String string) { //拿已经分割好的一个项进来求导，确保合法？、
        string = string + " ";
        char[] temp = string.toCharArray();
        System.out.println("this char[] length is " + temp.length);
        for (int i = 0; i < temp.length; ) {
            System.out.println("element No." + i + "now x is " + x);
            if (temp[i] == '-' && !x) {
                isNeg = !isNeg;
                i++;
            }
            else if (Character.isDigit(temp[i]) && !x) { // 是数字，目前为系数，尚未读到x
                haveCoe = true;
                int j = i;
                String num = "";
                while (Character.isDigit(temp[j])) {
                    num = num + temp[j]; // 提取数字
                    j++;
                }
                //num转化为coeff
                if (isNeg) {
                    num = "-" + num;
                }
                coeff = new BigInteger(num, 10);

                i = j;
            }
            else if (temp[i] == 'x') {
                x = true;
                i++;
            }
            else if (temp[i] == '-' && x) {
                isNeg2 = !isNeg2; //幂次变为相反数
                i++;
            }
            else if (Character.isDigit(temp[i]) && x) { //是数字在x后面，为幂次
                haveExp = true;
                int k = i+1;
                String num1 = "" + temp[i];
                while (Character.isDigit(temp[k])) { // 从下一个开始判断
                    System.out.println("we are here expo, k is " + k);
                    num1 = num1 + temp[k];
                    k++;
                } //按理说此时已经读完了
                //num1转化为power
                System.out.println("we are here k is " + k);
                if (isNeg2) {
                    num1 = "-" + num1;
                }
                exponent = new BigInteger(num1, 10);
                break;
                //i = k;
            }
            else { // 跳过空格，^符号
                i++;
                continue;
            }

            if (!haveCoe) {
                coeff = BigInteger.ONE; //没写系数，默认为1
            }
            if (!haveExp) {
                exponent = BigInteger.ONE; //没写次数，默认为1
            }

        }

        return ("the coeff is " + coeff + " the exp is" + exponent);
    }
}
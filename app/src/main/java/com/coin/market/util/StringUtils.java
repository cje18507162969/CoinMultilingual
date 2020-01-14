package com.coin.market.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LiYang on 2018/12/21.
 */

public class StringUtils {

    /**
     * 建议用此
     *
     * @param number 数据
     * @param len    保留长度
     * @return 返回结果        此方法不四舍五入  cjh
     */
    public static String double2String(double number, int len) {
        return BigDecimal.valueOf(number)
                .setScale(len, BigDecimal.ROUND_DOWN)
                .toPlainString();
    }


    /**
     * 对double数据进行取精度.
     * @param value  double数据.
     * @param scale  精度位数(保留的小数位数).
     * @param roundingMode  精度取值方式.
     * @return 精度计算后的数据. RoundingMode.HALF_EVEN //使用银行家算法
     */
    public static double round(double value, int scale,
                               RoundingMode roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }

    /**
     * String 字符串的操作【三位一逗号、去除科学计数法】
     */
    public static String getCommaString(double str) {
        if (isIntegerForDouble(str)) {
            DecimalFormat df = new DecimalFormat("######");
            return df.format(Double.parseDouble(str + ""));
        } else {
            return str + "";
        }

    }

    /**
     * 1.判断字符串是否仅为数字:
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {

        for (int i = str.length(); --i >= 0; ) {

            if (!Character.isDigit(str.charAt(i))) {

                return false;

            }

        }

        return true;

    }

    /**
     * double 保留小数点 2位
     */
    public static String getDoubleTwo(double str) {
        if (!isIntegerForDouble(str)) {
            DecimalFormat df = new DecimalFormat("######0.0000");
            return df.format(Double.parseDouble(str + ""));
        } else {
            return str + "";
        }

    }

    /**
     * 保留小数点后两位
     * @param str
     * @return
     */
    public static String getStringTwo(String str){
        DecimalFormat format = new DecimalFormat("0.00");
        String a = format.format(new BigDecimal(str));
        return a;
    }

    public static double getDouble2(double str){
        DecimalFormat format = new DecimalFormat("0.00");
        String a = format.format(new BigDecimal(str));
        if (!TextUtils.isEmpty(a)){
            return Double.valueOf(a);
        }
        return 0;
    }

    /**
     * 乘法运算
     * @param m1
     * @param m2
     * @return
     */
    public static double mul(double m1, double m2) {
        BigDecimal p1 = new BigDecimal(Double.toString(m1));
        BigDecimal p2 = new BigDecimal(Double.toString(m2));
        return p1.multiply(p2).doubleValue();
    }

    /**
     * 减法运算
     * @param m1
     * @param m2
     * @return
     */
    public static double subDouble(double m1, double m2) {
        BigDecimal p1 = new BigDecimal(Double.toString(m1));
        BigDecimal p2 = new BigDecimal(Double.toString(m2));
        return p1.subtract(p2).doubleValue();
    }

    /**
     * 过滤一次 判断是否为整数  若是整数返回整数  若小数返回小数
     */
    public static String isIntegerDouble(double obj) {
        String result = "";
        if (isIntegerForDouble(obj)) {
            result = (int) obj + "";
        } else {
            result = obj + "";
        }
        return result;
    }

    /**
     * 判断double是否是整数
     *
     * @param obj
     * @return
     */
    public static boolean isIntegerForDouble(double obj) {
        double eps = 1e-10;  // 精度范围
        return obj - Math.floor(obj) < eps;
    }

    public static double StringForDouble(String str) {
        double p = Double.parseDouble(str);
        return p;
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * java去掉字符串前面的0
     */
    public static String RemoveZero(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String newStr = str.replaceAll("^(0+)", "");
        return newStr;
    }

    /**
     * 判断2个List 是否相同
     */
    public static boolean getDiffrent2(List<String> list1, List<String> list2) {
        //long st = System.nanoTime();
        //System.out.println("getDiffrent2 total times " + (System.nanoTime() - st));
        return !list1.retainAll(list2);
    }

    /**
     * 此方法 专用于 需求为多少的倍数  numb 为输入数    Multiple 为倍数
     */
    public static boolean isOk(int mub, int Multiple) {
        boolean type = false;
        try {
            if (mub % Multiple == 0) {
                type = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    /**
     * 字符串截取
     */
    public static String getNewNumber(String Str, int size) {
        try {
            String str = Str;
            //截取0-size之间的的字符串
            str.substring(0, size);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 字符串截取第*个字符
     */
    public static String getEditPosition(String Str, int size) {
        String newString = "";
        try {
            String str = Str;
            //截取0-size之间的的字符串
            newString = str.substring(size, size + 1);
            return newString;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 金额显示  后面带单位
     */
    public static String getNewNumber2(double price) {
        try {
            String str = price + "";
            String aa = "";
            //截取0-size之间的的字符串
            if (price > 100000000) {
                aa = double2String(price / 100000000, 2) + "亿";
            } else if (price > 10000) {
                aa = double2String(price / 10000, 2) + "万";
            } else if (price > 1000) {
                aa = double2String(price / 1000, 2) + "千";
            } else {
                aa = double2String(price, 2) + "";
            }
            return aa;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 扫一扫 扫出 钱包的二维码 字符串 过滤掉多余字符
     */
    public static String splitData(String str, String strStart, String strEnd) {
        String tempStr;
        tempStr = str.substring(str.indexOf(strStart) + strStart.length(), str.lastIndexOf(strEnd));
        return tempStr;
    }

    /**
     *  替换字符  （用于替换手机号 中间4位）
     */
    public static String splitPhone(String str) {

        String maskNumber = str.substring(0,3)+"****"+str.substring(7,str.length());

        return maskNumber;
    }

    /**
     * 将传入的is一行一行解析读取出来出来
     */
    public static String readJsonFromAssets(Context mContext, String json) {
        InputStreamReader inputStreamReader;
        String resultString = "";
        try {
            inputStreamReader = new InputStreamReader(mContext.getAssets().open(json), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStreamReader.close();
            bufferedReader.close();
            resultString = stringBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr
     *            身份证号
     * @return true 有效：false 无效
     * @throws ParseException
     */
    public static boolean IDCardValidate(String IDStr) throws ParseException {
        String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ 号码的长度18位 ================
        if (IDStr.length() != 18) {
            return false;
        }
        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        }
        if (isNumeric(Ai) == false) {
            //errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return false;
        }
        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 日
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
//          errorInfo = "身份证生日无效。";
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                //errorInfo = "身份证生日不在有效范围。";
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            //errorInfo = "身份证月份无效";
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            //errorInfo = "身份证日期无效";
            return false;
        }
        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            //errorInfo = "身份证地区编码错误。";
            return false;
        }
        // ================ 判断最后一位的值 ================
//        int TotalmulAiWi = 0;
//        for (int i = 0; i < 17; i++) {
//            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
//        }
//        int modValue = TotalmulAiWi % 11;
//        String strVerifyCode = ValCodeArr[modValue];
//        Ai = Ai + strVerifyCode;
//
//        if (IDStr.length() == 18) {
//            if (Ai.equals(IDStr) == false) {
//                //errorInfo = "身份证无效，不是合法的身份证号码";
//                return false;
//            }
//        } else {
//            return true;
//        }
        return true;
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings("unchecked")
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
//      hashtable.put("71", "台湾");
//      hashtable.put("81", "香港");
//      hashtable.put("82", "澳门");
//      hashtable.put("91", "国外");
        return hashtable;
    }



    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param strDate
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 语言环境检测
     * @param context
     * @return
     */
    public static String getLanguage(final Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String Country = locale.getCountry();
        return language;
    }

}
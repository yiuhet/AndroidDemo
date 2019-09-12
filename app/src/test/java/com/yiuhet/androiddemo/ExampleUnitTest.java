package com.yiuhet.androiddemo;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        ArrayList<Integer> arrayList = new ArrayList();
//        arrayList.add(1);
//        arrayList.add(2);
//        ArrayList<Integer> arrayList1 = new ArrayList();
//        arrayList1.add(5);
//        arrayList1.add(6);
//        arrayList1.add(7);
//        arrayList1.add(8);
//        ArrayList<Integer> t = new ArrayList();
//        t.addAll(arrayList);
//        t.addAll(arrayList1);
//        t.subList(t.size() - arrayList.size(), t.size()).clear();

//        System.out.println(t.toString());


        assertEquals(4, 2 + 2);
    }

    @Test
    public void sss() throws ParseException {
        long now = System.currentTimeMillis();
        SimpleDateFormat sdfOne = new SimpleDateFormat("yyyy-MM-dd");
        long overTime = (now - (sdfOne.parse(sdfOne.format(now)).getTime())) / 1000;
        //当前毫秒数
        System.out.println(now);
        //当前时间  距离当天凌晨  秒数
        System.out.println(overTime);
        //当天凌晨毫秒数
        System.out.println(sdfOne.parse(sdfOne.format(now)).getTime());
        //当天凌晨日期
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdfTwo.format(sdfOne.parse(sdfOne.format(now)).getTime()));

        Random random = new Random();
        //触发区间次日凌晨[0,4]点，创建个0-240分钟的随机数
        long delay = random.nextInt(241) * 60 * 1000;
        System.out.println("delay:" + delay);
        //距离次日凌晨还剩余的时间
        long time = getTomorrowZero();
        System.out.println("job time : " + Unix2Date(time + delay, ""));
    }

    public static String Unix2Date(long unix, String formats) {
        formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = unix;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    public long getTomorrowZero() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime().getTime() + 1;
    }
}
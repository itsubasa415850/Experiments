/*
 * File Name     : CommonUtil.java
 * System Name   : XBRL Component
 * Copyright     : Copyright (C) 2007 NTT DATA China. All Rights Reserved.
 * Author        : NTTDATA BJ Lingxian Liu
 * Make Date     : 2007/07/12
 * History
 *     2007/07/12 Lingxian Liu(BND) First Version
 *     2011/04/26 Chenxi Yu(BND) Second Version
 */
package cn.com.nttdata.batchserver.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.io.RandomAccessFile;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Common class<br>
 */
public class CommonUtil {

    /**
     * <p>
     * get system time
     * </p>
     *
     * @param timeFormat
     *            time format
     * @return systime the formatted system time
     */
    public static String getSysTime(String timeFormat) {
        SimpleDateFormat dateformatter = new SimpleDateFormat(timeFormat);
        return dateformatter.format(new Date());
    }

    /**
     * get a random long value for the signature.
     * @return the signature value
     */
    public static long getSignature() {
        long signature = 0;
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        Random random = new Random(Long.parseLong(df.format(date)));
        signature = random.nextLong();
        return signature < 0 ? (- signature) : signature;
    }

    /**
     * <p>
     * judge the string is null or not
     * </p>
     *
     * @param value
     *            the inputted String
     * @return TRUE:NULL FALSE:NOT NULL
     */
    public final static boolean isNullStr(String value) {
        if (value == null) {
            return true;
        }
        return (value.length() == 0);
    }

    /**
     * <p>
     * format string type date to Date type date
     * </p>
     *
     * @param date
     *            the string type date that will formatting to string
     * @param dataFormat
     *            the date's format
     * @return Date the fomatted Date type date
     */
    public static Date formatStringDate(String date, String dataFormat) {
        Date calDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat(dataFormat);
        ParsePosition pos = new ParsePosition(0);
        calDate = formatter.parse(date, pos);
        return calDate;
    }

    /**
     * format the date to string
     * @param date the date to format
     * @param Pattern the pattern
     * @return the formatted date
     */
    public static String formatDateToString(Date date, String Pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(Pattern);
        return formatter.format(date);
    }

    /**
     * <p>
     * add date by second
     * </p>
     *
     * @param date
     *            that will be adding date
     * @param amount
     *            how much second will add
     * @return Date the added date
     */
    public static Date addDateBySecond(Date date, int amount) {
        Calendar calendar = null;
        calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, amount);
        return calendar.getTime();
    }

    /**
     * <p>
     * get the string which after execute the toString method, if the input
     * object is null return ""
     * </p>
     *
     * @param value
     *            object
     * @return the object to string
     */
    public static String objectToString(Object value) {

        String returnValue = null;
        if (value == null) {
            returnValue = "";
        } else {
            returnValue = value.toString();
        }
        return returnValue;
    }


    /**
     * the method to read file
     * @param filePath the file path to read
     * @return file contents
     * @throws IOException when error occurs
     */
    public static List<String> readTxtFile(String filePath) throws IOException {
        List<String> txtStr = new ArrayList<String>();

        String tempStr = null;
        FileInputStream file = new FileInputStream(filePath);
        InputStreamReader reader = new InputStreamReader(file, "utf-8");

        BufferedReader br_reader = new BufferedReader(reader);
        // read property
        while ((tempStr = br_reader.readLine()) != null) {
            txtStr.add(tempStr);
        }
        // stream close
        br_reader.close();
        reader.close();
        return txtStr;
    }

}

package cn.com.nttdata.batchserver.functions;



/*

 * File Name     : Monitor.java

 * System Name   : XBRL Component

 * Copyright     : Copyright (C) 2007 NTT DATA Corporation. All Rights Reserved.

 * Author        : NTTDATA BJ Lingxian Liu

 * Make Date     : 2007/07/09

 * History

 *     2007/07/09 Lingxian Liu(BND) First Version

 */

import java.io.File;
import java.io.FileWriter;
import cn.com.nttdata.xbrl.common.CommonUtil;
import cn.com.nttdata.xbrl.common.Const;
import cn.com.nttdata.xbrl.info.ILog;

public class MyILog implements ILog {

	public MyILog(String logFilePath){
		this.logFilePath = logFilePath;
		}

	/**
	 * Loggin the error info
	 * @param msg
	 */
	public void error(Object msg) {
		String msgStr = attachString((String) msg);
		write(msgStr);
	};

	/**
	 * Logging the warn info
	 * @param msg
	 */

	public void warn(Object msg) {
		String msgStr = attachString((String) msg);
		write(msgStr);
	};

	/**
	 * Logging the normal info
	 * @param msg
	 */
	public void info(Object msg) {
		String msgStr = attachString((String) msg);
		write(msgStr);

	};

	/**
	 * Logging the stack trace
	 * @param e
	 */
	public void debug(Throwable e) {
		String msgStr = attachString(e.toString());
		write(msgStr);
	};

	/**
	 * <p>
	 * print message in console and log file
	 * </p>
	 *
	 * @param msg Object
	 * @return null
	 */
	private void write(Object msg) {
		if (msg != null) {
			try {
				String logFilePath = this.logFilePath;
				File file = new File(logFilePath);
				if (!(file.isFile() && file.exists())) {
					file.createNewFile();
				}
				FileWriter out = new FileWriter(file, true);
				out.write((String) msg);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String attachString(String info) {
		StringBuffer msgBuf = new StringBuffer();
		String msgStr = msgBuf.append(
		CommonUtil.getSysTime(Const.TIME_SECOND_MARK)).append(
		Const.HALF_SPACE_STRING).append(info).append(
		System.getProperty("line.separator")).toString();
		return msgStr;
	}

	private String logFilePath;

	public void setLogFilePath(String logFilePath){
		this.logFilePath = logFilePath;
	}

	public String getLogFilePath(){
		return this.logFilePath ;
	}

}

package cn.com.nttdata.rsa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadValidateWebService {
	public static void main(String[] args) throws IOException {
		String restAPIstr =
			"http://localhost:8080/localhost.crt"; // + 
//			"E:/01-Arelle/cases/taxonomy/100-schema/102-10-substitutiontuplevalid.xbrl" +
//			"/validation/xbrl?media=text";
		URL url = new URL(restAPIstr);
		HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();

		if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		}
//		InputStream in = conn.getInputStream();
		StringBuffer sb = new StringBuffer();
//		int i = 0;
//		do {
//            i = in.read();
//            sb.append(new String("" + (char) i));
//        } while (i != -1);
		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
		    sb.append(line);
			System.out.println(line);
		}
		rd.close();
		conn.disconnect();
	}
}

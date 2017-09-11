package cn.com.nttdata.batchserver.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.com.nttdata.batchserver.errors.DataAcquisitonError;

/**
 * Decryption<br>
 */
public final class Decryption {

    private static final int c_Cnt = 0xFFFF;

	private static final int c_Key = 0xB0;

    private static final int c_EOF = -1;

    private static final int c_ASCII0 = 0x0;
    /**
     * Decode function
     * @param the source file
     * @return File the decoded file
     */
	public static File decode(File srcFile, String srcFileName, String returnFileName) {
        StringBuffer tempSb = new StringBuffer();
        File returnFile = new File(srcFile.getPath() + File.separator + returnFileName);
        InputStream in = null;
        OutputStream out = null;
        int readResult = 0;
        int index = 0;
        try {
            if(!returnFile.exists()) {
                returnFile.createNewFile();
            }
            out = new FileOutputStream(returnFile);
            in = new FileInputStream(srcFile + File.separator + srcFileName);
            readResult = in.read();
            while(readResult != c_EOF && readResult != c_ASCII0) {
                index ++;
                int decode = readResult ^ c_Key;
                tempSb.append((char) decode);
                readResult = in.read();
                if(index == c_Cnt) {
                    out.write((tempSb.toString()).getBytes("ISO-8859-1"));
                    tempSb = new StringBuffer();
                }
            }
            out.write((tempSb.toString()).getBytes("ISO-8859-1"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        } finally {
            if(in != null)
            try {
                in.close();
            } catch (IOException e) {}
            if(out != null)
                try {
                    out.close();
                } catch (IOException e) {}
        }
        return returnFile;
	}

    /**
     * <p>
     * Decryption
     * </p>
     *
     * @param inFile input file path
     *        outFile output file path
     *        c which byte to change
     * @exception IOException e
     */
//	public void decrypt(File inFile, File outFile, int c) {
//		// end flag
//		int n = 0;
//		// buffer block
//		byte[] bs = new byte[1024];
//		FileInputStream in = null;
//		FileOutputStream out = null;
//		try {
//			// input stream
//			in = new FileInputStream(inFile);
//			// output stream
//			out = new FileOutputStream(outFile);
//			// read file
//			while ((n=in.read(bs)) != -1) {
//				for (int i = 0;i < n;i++) {
//					if (i != 0 && i%c==0){
//						// decryption
//						bs[i]^= CONS_KEY;
//					}
//				}
//				// write file
//				out.write(bs, 0, n);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				// close file
//				in.close();
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}

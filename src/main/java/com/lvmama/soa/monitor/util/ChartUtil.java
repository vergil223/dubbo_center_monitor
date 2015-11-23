package com.lvmama.soa.monitor.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class ChartUtil {
	public static void showChartToResponse(HttpServletResponse response,
			String chartFullPath) {
		response.setContentType("image/png");
		FileInputStream fis = null;
		OutputStream os = null;
		try {
			fis = new FileInputStream(chartFullPath);
			os = response.getOutputStream();
			int count = 0;
			byte[] buffer = new byte[1024 * 8];
			while ((count = fis.read(buffer)) != -1) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fis.close();
				os.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

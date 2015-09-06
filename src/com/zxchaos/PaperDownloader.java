package com.zxchaos;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PaperDownloader {
	private String outputFolder = "/home/zxchaos/软考视频/论文/";

	public static void main(String[] args) {
		PaperDownloader paperDownloader = new PaperDownloader();
		paperDownloader.process();
	}

	public void process() {
		Connection conn = getConnection("http://www.educity.cn/usercenter/fudao_model.aspx");
		try {
			Document document = conn.get();
			Elements aList = document.select("div.gr_xk_c>dl>dd>div[class=\"w440 fl\"]>a");
			for (Element a : aList) {
				String newUrl = a.attr("href");
				System.out.println("sleep......");
				Thread.sleep(2000);
				Connection newPageConn = getConnection(newUrl);
				Document newPageDoc = newPageConn.get();
				String paperTitle = newPageDoc.select("div#LabBookName").get(0).text();
				String imgSrc = "";
				try {
					Elements imgElements = newPageDoc.select("div.opeat>div.line:nth-child(2)>div.cont>p>img");
					if (imgElements != null && !imgElements.isEmpty()) {
						imgSrc = imgElements.get(0).attr("src");
					} else {
						imgSrc = newPageDoc.select("div.opeat>div.line:nth-child(2)>div.cont>img").get(0).attr("src");
					}
					
				} catch (Exception e) {
					System.out.println("处理url:" + newUrl + "出错");
					continue;
				}
				URL url = new URL(imgSrc);
				InputStream is = url.openStream();
				String fileName = genFileName(paperTitle);
				File paperFile = new File(outputFolder + fileName);
				OutputStream os = new BufferedOutputStream(new FileOutputStream(paperFile));
				int b = 0;
				while ((b = is.read()) != -1) {
					os.write(b);
				}
				os.close();
				is.close();
				System.out.println(paperFile.getAbsolutePath() + "生成完毕");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Connection getConnection(String url) {
		Connection conn = Jsoup.connect(url);
		conn.header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:31.0) Gecko/20100101 Firefox/31.0");
		conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		conn.cookie("bdshare_firstime", "1419678229652");
		conn.cookie("pgv_pvi", "1287242752");
		conn.cookie("safedog-flow-item", "FF44D613BECC0EF71DAE800C2E8CEA60");
		conn.cookie("ASP.NET_SessionId", "h2ts5gltvoi1wchfielv01un");
		conn.cookie("CheckCode", "NXJP");
		conn.cookie("IESESSION", "alive");
		conn.cookie("kingwangname", "Pzxchaos");
		conn.cookie("csaiuserid", "8269455");
		conn.cookie("edu51cmmpass", "27436bb8223964f9");
		conn.cookie("it119right", "10");
		conn.cookie("csairealname", "%u5f20%u946b");
		conn.cookie("csaiprovince", "");
		conn.cookie("pgv_si", "s9198659584");
		conn.cookie("Hm_lvt_555d9dcffdcb317595de82b0fc125cdf", "1441259981,1441356326,1441432224,1441470915");
		conn.cookie("Hm_lpvt_555d9dcffdcb317595de82b0fc125cdf", "1441471248");
		conn.cookie("IESESSION4007771218is", "2");
		conn.cookie("IESESSION4007771218mid", "720_28");
		conn.cookie("Hm_lvt_555d9dcffdcb317595de82b0fc125cdf",
				"1473007247786|1441259981,1441356326,1441432224,1441470915");
		conn.cookie("IESESSION4007771218slid", "slid_207_31|");
		conn.cookie("IESESSION4007771218mh", "1441471458554");
		conn.timeout(10000);
		return conn;
	}

	private String genFileName(String title) throws IOException {
		File paperFile = new File(outputFolder + title + ".gif");
		if (!paperFile.exists()) {
			paperFile.createNewFile();
			title = title + ".gif";
		} else {
			System.out.println("名称为:" + title + ".gif存在重新生成文件名称");
			Collection<File> files = FileUtils.listFiles(new File(outputFolder), new RegexFileFilter(title + ".+"),
					null);
			title = title + "_" + (files.size() + 1) + ".gif";
			System.out.println("生成后的文件名称:" + title);
		}
		return title;
	}
}

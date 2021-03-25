package com.timcircle.keeper.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.timcircle.keeper.util.StringUtil;

public class Util {

	public static String readContent(Process p) throws IOException {
		return readContent(p.getInputStream());
	}

	public static List<String> readLines(Process p) throws IOException {
		return readLines(p.getInputStream());
	}
	
	public static String readContent(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		return builder.toString();
	}
	
	public static List<String> readLines(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		List<String> lines = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		reader.close();
		return lines;
	}

	public static List<String> split(String line) {
		List<String> list = new ArrayList<>();
		if(StringUtil.isNotEmpty(line)) {
			String[] array = line.split(" ");
			for(String s : array) {
				if(StringUtil.isNotEmpty(s.trim())){
					list.add(s);
				}
			}
		}
		return list;
	}
}

package com.timcircle.keeper.ext;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

public class MainClass {
	public static void main(String[] args) throws Exception {
		
		File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "ext");
		System.out.println(jarDir.getAbsolutePath());
		
		File file2 = new File(".", "ext");
		System.out.println(file2.getAbsolutePath());
		
		URL jarLocationUrl = MainClass.class.getProtectionDomain().getCodeSource().getLocation();
		File file3 = new File(jarLocationUrl.toString(), "ext");
		System.out.println(file3.getAbsolutePath());
		
//		ProtectionDomain domain = OpenFileCountCondition.class.getProtectionDomain();
//		CodeSource codeSource = domain.getCodeSource();
//		URL location = codeSource.getLocation();
//		System.out.println(new File(location.getPath()).getAbsolutePath());
//		System.out.println(new File("./", "ext").exists());
//		URI uri = location.toURI();
//		System.out.println("uri:" + uri.getPath());
//		File file = new File(uri);
//		File extDir = file.getParentFile();
//		File extDir = new File(MainClass.class.getProtectionDomain().getCodeSource().getLocation()
//			    .toURI()).getParentFile();
//		System.out.println(extDir.getAbsolutePath());
//		String pid = getTomcatPid();
//		int tomcatOpenFileCount = getTomcatOpenFileCount(pid);
//		int tomcatPidLimit = getTomcatPidLimit(pid);		
//		String tomcatOpenFileCount = OpenFileCountCondition.readProcess("ls /proc/" + pid + "/fd | wc -l"); 
//		String allowOpenFileCount = OpenFileCountCondition.readProcess("ulimit -n");

//		System.out.println("pid : " + pid);
//		System.out.println("tomcat used: " + tomcatOpenFileCount);
//		System.out.println("tomcat limited: " + tomcatPidLimit);
//		System.out.println("system : " + allowOpenFileCount);
	}

	private static int getTomcatPidLimit(String pid) throws Exception {
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/cat", "/proc/" + pid + "/limits");
		Process p = pb.start();
		p.waitFor();

		final String HEADER = "Max open files";
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith(HEADER))
				continue;

			String l = line.replace(HEADER, "");
			String[] array = l.split(" ");

			for (String str : array) {
				if (str.equals(""))
					continue;
				builder.append(str);
				break;
			}
			break;
		}
		reader.close();
		return Integer.parseInt(builder.toString());
	}

	private static int getTomcatOpenFileCount(String pid) throws Exception {
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/ls", "/proc/" + pid + "/fd");
		Process p = pb.start();
		p.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		int count = 0;
		while (reader.readLine() != null) {
			count++;
		}
		reader.close();
		return count;
	}

	private static String getTomcatPid() throws Exception {
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/ps", "-ef");
		Process p = pb.start();
		p.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuilder builder = new StringBuilder();
		String line = null;
		int index = 0;
		while ((line = reader.readLine()) != null) {
			if (line.contains("tomcat")) {
				String[] list = line.split(" ");
				for (String str : list) {
					if (str.equals(""))
						continue;
					index++;

					if (index == 2) {
						builder.append(str);
						break;
					}
				}
			}
		}
		reader.close();
		return builder.toString();
	}
}

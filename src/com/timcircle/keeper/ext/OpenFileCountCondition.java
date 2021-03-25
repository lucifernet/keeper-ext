package com.timcircle.keeper.ext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.timcircle.keeper.cond.ICondition;
import com.timcircle.keeper.print.IPrinter;

public class OpenFileCountCondition implements ICondition {

	@Override
	public boolean match(Map<String, Object> args, Map<String, Object> jobBundle, IPrinter printer) throws Exception {
		Double trigger = 0.9d;
		if(args.containsKey("trigger")) {
			Object value = args.get("trigger");
			if(value instanceof Double)
				trigger = (Double) value;
		}
		
		String pid = getTomcatPid();
		int tomcatOpenFileCount = getTomcatOpenFileCount(pid);
		int tomcatPidLimit = getTomcatPidLimit(pid);
		
		printer.i("pid : " + pid);
		printer.i("Tomcat used files : " + tomcatOpenFileCount);
		printer.i("Tomcat limit files : " + tomcatPidLimit);
		
		Map<String, Object> bundle = new HashMap<>();
		jobBundle.put(OpenFileCountCondition.class.getSimpleName(), bundle);
		bundle.put("pid", pid);
		bundle.put("tomcat_used_files", tomcatOpenFileCount);
		bundle.put("tomcat_limit_files", tomcatPidLimit);
				
		return (tomcatOpenFileCount > (tomcatPidLimit * trigger));
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

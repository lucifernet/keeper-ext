package com.timcircle.keeper.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.timcircle.keeper.cond.ICondition;
import com.timcircle.keeper.print.IPrinter;

public class NetstatCheckPortCondition implements ICondition {

	private static String ARG_PORTS = "check_ports";

	@Override
	public boolean match(Map<String, Object> args, Map<String, Object> jobBundle, IPrinter printer) throws Exception {
		Object value = args.get(ARG_PORTS);
		printer.d(String.valueOf(value));
		@SuppressWarnings("rawtypes")
		List portValues = (List) value;
		List<Integer> ports = new ArrayList<>();
		for (Object p : portValues) {
			int port = 0;
			try {
				Double d = (Double) p;
				port = d.intValue();
				ports.add(port);
			} catch (Exception e) {
				printer.e("Parse value error", e);
			}
		}
		printer.i("check ports : " + ports.size());

		String[] cmd = { "/usr/bin/netstat", "-tulpn" };
		ProcessBuilder pb = new ProcessBuilder(cmd);
		Process p = pb.start();
		p.waitFor();
		List<String> lines = Util.readLines(p);

		boolean match = false;
		for (int port : ports) {
			boolean contains = false;
			for (String line : lines) {
				if (!line.contains("LISTEN"))
					continue;

				List<String> infos = Util.split(line);
				String address = infos.get(3);

				if (address.endsWith(":" + port)) {
					contains = true;
					break;
				}
			}

			String alive = contains ? "alive" : "dead";
			String format = "port %d is %s";
			printer.i(String.format(format, port, alive));

			if (!contains)
				match = true;
		}

		return match;
	}

}

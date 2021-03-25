package com.timcircle.keeper.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.timcircle.keeper.cond.ICondition;
import com.timcircle.keeper.print.IPrinter;
import com.timcircle.keeper.util.StringUtil;

public class LsofCheckPortCondition implements ICondition {

	private static String ARG_PORTS = "check_ports";

	@SuppressWarnings("rawtypes")
	@Override
	public boolean match(Map<String, Object> args, Map<String, Object> jobBundle, IPrinter printer) throws Exception {
		Object value = args.get(ARG_PORTS);
		printer.d(String.valueOf(value));
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
		boolean match = false;
		for (int port : ports) {
			String[] cmd = { "/usr/sbin/lsof", "-i", ":" + port };
			ProcessBuilder pb = new ProcessBuilder(cmd);
			Process p = pb.start();
			p.waitFor();
			String content = Util.readContent(p);
			String alive = StringUtil.isEmpty(content) ? "dead" : "alive";

			if (StringUtil.isEmpty(content)) {
				match = true;
				String format = "port %d is %s";
				printer.i(String.format(format, port, alive));
			} else {
				String format = "port %d is %s";
				printer.i(String.format(format, port, alive));
			}
		}
		return match;
	}


}

package es.caib.notib.back.helper;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Monitor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("restriction")
public class MonitorHelper {

	private static Boolean actiu = null;
	private static long prevUpTime, prevProcessCpuTime;
	private static RuntimeMXBean rmBean;
	private static com.sun.management.OperatingSystemMXBean sunOSMBean;

	public static com.sun.management.OperatingSystemMXBean getSunOSMBean() {
		return sunOSMBean;
	}

	public static String getArch() {

		try {
			return sunOSMBean.getArch();
		} catch (Exception e) {
			return "No disponible";
		}
	}
	
	public static String getName() {

		try {
			return sunOSMBean.getName();
		} catch (Exception e) {
			return "No disponible";
		}
	}
	
	public static String getVersion() {

		try {
			return sunOSMBean.getVersion();
		} catch (Exception e) {
			return "No disponible";
		}
	}

	private static Result result;
	public static Boolean getActiu() {
		return actiu;
	}

	private static class Result {
		long upTime = -1L;
		long processCpuTime = -1L;
		float cpuUsage = 0;
		int nCPUs;
	}

	static {
		try {
			rmBean = ManagementFactory.getRuntimeMXBean();
			// reperisco l'MBean relativo al sunOS
			sunOSMBean = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, com.sun.management.OperatingSystemMXBean.class);

			result = new Result();
			result.nCPUs = sunOSMBean.getAvailableProcessors();
			result.upTime = rmBean.getUptime();
			result.processCpuTime = 0;
			if (sunOSMBean != null) {
				result.processCpuTime = sunOSMBean.getProcessCpuTime();
			}
		} catch (Exception e) {
			System.err.println(MonitorHelper.class.getSimpleName() + " exception: " + e.getMessage());
		}
	}

	private static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

	public static long[] getThreadsIds() {
		return ManagementFactory.getThreadMXBean().getAllThreadIds();
	}

	public MonitorHelper(String sactiu) {
		super();
		actiu = !"true".equalsIgnoreCase(sactiu);
	}

	public static String humanReadableByteCount(long bytes) {

		var si = true;
		var unit = si ? 1000 : 1024;
		if (bytes < unit) {
			return bytes + " B";
		}
		var exp = (int) (Math.log(bytes) / Math.log(unit));
		var pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String getCPULoad() {

		try {
			result.upTime = rmBean.getUptime();
			result.processCpuTime = sunOSMBean.getProcessCpuTime();
			if (result.upTime > 0L && result.processCpuTime >= 0L) {
				updateCPUInfo();
			}
			return result.cpuUsage + "%";
		} catch (Exception e) {
			return "No disponible";
		}
	}

	public static void updateCPUInfo() {

		if (prevUpTime > 0L && result.upTime > prevUpTime) {
			var elapsedCpu = result.processCpuTime - prevProcessCpuTime;
			var elapsedTime = result.upTime - prevUpTime;
			result.cpuUsage = Math.round(Math.min(100F, elapsedCpu / (elapsedTime * 10000F * result.nCPUs)));
		}
		prevUpTime = result.upTime;
		prevProcessCpuTime = result.processCpuTime;
	}

	/** Get CPU time in nanoseconds. */
	public static long getCpuTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		for (long i : getThreadsIds()) {
			long t = bean.getThreadCpuTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	public static long getCpuTimePercent() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		long t;
		for (var i : getThreadsIds()) {
			t = bean.getThreadCpuTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	/** Get user time in nanoseconds. */
	public static long getUserTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		long t;
		for (var i : getThreadsIds()) {
			t = bean.getThreadUserTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	/** Get system time in nanoseconds. */
	public static long getSystemTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		long tc, tu;
		for (var i : getThreadsIds()) {
			tc = bean.getThreadCpuTime(i);
			tu = bean.getThreadUserTime(i);
			if (tc != -1 && tu != -1)
				time += (tc - tu);
		}
		return time;
	}
}

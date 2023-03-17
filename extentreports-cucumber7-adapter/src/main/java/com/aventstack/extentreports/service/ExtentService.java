package com.aventstack.extentreports.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.ReporterConfigurable;
import com.aventstack.extentreports.reporter.ReporterFilterable;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import tech.grasshopper.pdf.extent.ExtentPDFCucumberReporter;
import tech.grasshopper.pdf.extent.processor.MediaProcessor;
import tech.grasshopper.pdf.section.details.executable.MediaCleanup.CleanupType;
import tech.grasshopper.pdf.section.details.executable.MediaCleanup.MediaCleanupOption;

public class ExtentService implements Serializable {

	private static final long serialVersionUID = -5008231199972325650L;

	private static Properties properties;

	public static synchronized ExtentReports getInstance() {
		return ExtentReportsLoader.INSTANCE;
	}

	public static synchronized void flush() {
		System.out.println("BEFORE FLUSH - " + ExtentReportsLoader.CURRENT_RUNNER_COUNT.get());
		if (!ExtentReportsLoader.RUNNER_COUNT_AVAILABLE || ExtentReportsLoader.isRunnerLast()) {
			System.out.println("FLUSH CALLED " + ExtentReportsLoader.CURRENT_RUNNER_COUNT.get());
			ExtentReportsLoader.INSTANCE.flush();
		}
		System.out.println("AFTER FLUSH - " + ExtentReportsLoader.CURRENT_RUNNER_COUNT.get());
	}

	public static Object getProperty(String key) {
		String sys = System.getProperty(key);
		return sys == null ? (properties == null ? null : properties.get(key)) : sys;
	}

	public static Object getPropertyOrDefault(String key, Object defaultValue) {
		Object value = getProperty(key);
		return value == null ? defaultValue : value;
	}

	public static String getScreenshotFolderName() {
		return ExtentReportsLoader.SCREENSHOT_FOLDER_NAME;
	}

	public static String getScreenshotReportRelatvePath() {
		return ExtentReportsLoader.SCREENSHOT_FOLDER_REPORT_RELATIVE_PATH;
	}

	public static boolean isBase64ImageSrcEnabled() {
		return ExtentReportsLoader.ENABLE_BASE64_IMAGE_SRC;
	}

	public static boolean isDeviceEnabled() {
		return ExtentReportsLoader.IS_DEVICE_ENABLED;
	}

	public static boolean isAuthorEnabled() {
		return ExtentReportsLoader.IS_AUTHOR_ENABLED;
	}

	public static String getDevicePrefix() {
		return ExtentReportsLoader.DEVICE_NAME_PREFIX;
	}

	public static String getAuthorPrefix() {
		return ExtentReportsLoader.AUTHOR_NAME_PREFIX;
	}

	@SuppressWarnings("unused")
	private ExtentReports readResolve() {
		return ExtentReportsLoader.INSTANCE;
	}

	private static class ExtentReportsLoader {

		private static final ExtentReports INSTANCE = new ExtentReports();
		private static final String[] DEFAULT_SETUP_PATH = new String[] { "extent.properties",
				"com/aventstack/adapter/extent.properties" };

		private static final String SYS_INFO_MARKER = "systeminfo.";
		private static final String OUTPUT_PATH = "test-output/";
		private static final String EXTENT_REPORTER = "extent.reporter";
		private static final String START = "start";
		private static final String CONFIG = "config";
		private static final String OUT = "out";
		private static final String VIEW_ORDER = "vieworder";
		private static final String STATUS_FILTER = "statusfilter";
		private static final String BASE64_IMAGE_SRC = "base64imagesrc";
		private static final String ENABLE_DEVICE = "enable.device";
		private static final String ENABLE_AUTHOR = "enable.author";
		private static final String PREFIX_DEVICE = "prefix.device";
		private static final String PREFIX_AUTHOR = "prefix.author";
		private static final String DELIM = ".";

		private static final String KLOV = "klov";
		private static final String SPARK = "spark";
		private static final String JSONF = "json";
		private static final String PDF = "pdf";
		private static final String HTML = "html";

		private static final String INIT_KLOV_KEY = EXTENT_REPORTER + DELIM + KLOV + DELIM + START;
		private static final String INIT_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + START;
		private static final String INIT_JSONF_KEY = EXTENT_REPORTER + DELIM + JSONF + DELIM + START;
		private static final String INIT_PDF_KEY = EXTENT_REPORTER + DELIM + PDF + DELIM + START;
		private static final String INIT_HTML_KEY = EXTENT_REPORTER + DELIM + HTML + DELIM + START;

		private static final String CONFIG_KLOV_KEY = EXTENT_REPORTER + DELIM + KLOV + DELIM + CONFIG;
		private static final String CONFIG_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + CONFIG;
		private static final String CONFIG_HTML_KEY = EXTENT_REPORTER + DELIM + HTML + DELIM + CONFIG;

		private static final String OUT_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + OUT;
		private static final String OUT_JSONF_KEY = EXTENT_REPORTER + DELIM + JSONF + DELIM + OUT;
		private static final String OUT_PDF_KEY = EXTENT_REPORTER + DELIM + PDF + DELIM + OUT;
		private static final String OUT_HTML_KEY = EXTENT_REPORTER + DELIM + HTML + DELIM + OUT;

		private static final String VIEW_ORDER_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + VIEW_ORDER;
		// Use below for both Spark & Html reporters
		private static final String BASE64_IMAGE_SRC_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM
				+ BASE64_IMAGE_SRC;

		private static final String DEVICE_ENABLE_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + ENABLE_DEVICE;
		private static final String AUTHOR_ENABLE_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + ENABLE_AUTHOR;
		private static final String DEVICE_PREFIX_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + PREFIX_DEVICE;
		private static final String AUTHOR_PREFIX_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + PREFIX_AUTHOR;

		private static boolean ENABLE_BASE64_IMAGE_SRC = false;

		// Use below for both Spark & Pdf reporters
		private static final String STATUS_FILTER_KEY = EXTENT_REPORTER + DELIM + STATUS_FILTER;

		private static String SCREENSHOT_FOLDER_NAME;
		private static String SCREENSHOT_FOLDER_REPORT_RELATIVE_PATH;
		private static final String DEFAULT_SCREENSHOT_FOLDER_NAME = "test-output/";

		private static final String SCREENSHOT_DIR_PROPERTY = "screenshot.dir";
		private static final String SCREENSHOT_REL_PATH_PROPERTY = "screenshot.rel.path";

		private static final String REPORTS_BASEFOLDER = "basefolder";
		private static final String REPORTS_BASEFOLDER_NAME = REPORTS_BASEFOLDER + DELIM + "name";
		private static final String REPORTS_BASEFOLDER_DATETIMEPATTERN = REPORTS_BASEFOLDER + DELIM + "datetimepattern";
		private static final String REPORTS_BASEFOLDER_ENABLEDELIMITER = REPORTS_BASEFOLDER + DELIM
				+ "enable.delimiter";
		private static final String REPORTS_BASEFOLDER_DELIMITER = REPORTS_BASEFOLDER + DELIM + "delimiter";
		private static final LocalDateTime FOLDER_CURRENT_TIMESTAMP = LocalDateTime.now();

		private static boolean IS_DEVICE_ENABLED = false;
		private static boolean IS_AUTHOR_ENABLED = false;
		private static String DEVICE_NAME_PREFIX;
		private static String AUTHOR_NAME_PREFIX;

		private static final String DEFAULT_DEVICE_PREFIX = "@dev_";
		private static final String DEFAULT_AUTHOR_PREFIX = "@aut_";

		private static final String REPORTS_RUNNER_COUNT_KEY = "runner.count";
		private static int DECLARED_RUNNER_COUNT;
		private static boolean RUNNER_COUNT_AVAILABLE = true;
		private static final AtomicInteger CURRENT_RUNNER_COUNT = new AtomicInteger(0);

		static {
			createViaProperties();
			createViaSystem();
			configureRunnerCount();
			configureScreenshotProperties();
			configureDeviceAndAuthorProperties();
		}

		private static void createViaProperties() {

			ClassLoader loader = ExtentReportsLoader.class.getClassLoader();
			Optional<InputStream> is = Arrays.stream(DEFAULT_SETUP_PATH).map(x -> loader.getResourceAsStream(x))
					.filter(x -> x != null).findFirst();
			if (is.isPresent()) {
				Properties properties = new Properties();
				try {
					properties.load(is.get());
					ExtentService.properties = properties;

					if (properties.containsKey(INIT_KLOV_KEY)
							&& "true".equals(String.valueOf(properties.get(INIT_KLOV_KEY))))
						initKlov(properties);

					if (properties.containsKey(INIT_SPARK_KEY)
							&& "true".equals(String.valueOf(properties.get(INIT_SPARK_KEY))))
						initSpark(properties);

					if (properties.containsKey(INIT_JSONF_KEY)
							&& "true".equals(String.valueOf(properties.get(INIT_JSONF_KEY))))
						initJsonf(properties);

					if (properties.containsKey(INIT_PDF_KEY)
							&& "true".equals(String.valueOf(properties.get(INIT_PDF_KEY))))
						initPdf(properties);

					if (properties.containsKey(INIT_HTML_KEY)
							&& "true".equals(String.valueOf(properties.get(INIT_HTML_KEY))))
						initHtml(properties);

					addSystemInfo(properties);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private static void createViaSystem() {

			if ("true".equals(System.getProperty(INIT_KLOV_KEY)))
				initKlov(null);

			if ("true".equals(System.getProperty(INIT_SPARK_KEY)))
				initSpark(null);

			if ("true".equals(System.getProperty(INIT_JSONF_KEY)))
				initJsonf(null);

			if ("true".equals(System.getProperty(INIT_PDF_KEY)))
				initPdf(null);

			if ("true".equals(System.getProperty(INIT_HTML_KEY)))
				initHtml(null);

			addSystemInfo(System.getProperties());
		}

		private static void configureRunnerCount() {
			System.out.println("prop count " + getPropertyOrDefault(REPORTS_RUNNER_COUNT_KEY, 1));
			if (getProperty(REPORTS_RUNNER_COUNT_KEY) == null) {
				RUNNER_COUNT_AVAILABLE = false;
				return;
			}

			try {
				DECLARED_RUNNER_COUNT = Integer
						.parseInt(String.valueOf(getPropertyOrDefault(REPORTS_RUNNER_COUNT_KEY, 1)));
			} catch (Exception e) {
				// Do nothing, default to 1
				DECLARED_RUNNER_COUNT = 1;
			}
		}

		private synchronized static boolean isRunnerLast() {
			System.out.println("dec " + DECLARED_RUNNER_COUNT);
			if (RUNNER_COUNT_AVAILABLE && CURRENT_RUNNER_COUNT.addAndGet(1) == DECLARED_RUNNER_COUNT)
				return true;
			return false;
		}

		private static String getBaseFolderName() {
			String folderpattern = "";
			Object baseFolderPrefix = getProperty(REPORTS_BASEFOLDER_NAME);
			Object baseFolderPatternSuffix = getProperty(REPORTS_BASEFOLDER_DATETIMEPATTERN);
			String enableDelimiter = String.valueOf(getPropertyOrDefault(REPORTS_BASEFOLDER_ENABLEDELIMITER, "true"));
			String delimiter = String.valueOf(getPropertyOrDefault(REPORTS_BASEFOLDER_DELIMITER, " "));

			if (enableDelimiter.equalsIgnoreCase("false"))
				delimiter = "";

			if (baseFolderPrefix != null && !String.valueOf(baseFolderPrefix).isEmpty()
					&& baseFolderPatternSuffix != null && !String.valueOf(baseFolderPatternSuffix).isEmpty()) {
				DateTimeFormatter folderSuffix = DateTimeFormatter.ofPattern(String.valueOf(baseFolderPatternSuffix));
				folderpattern = baseFolderPrefix + delimiter + folderSuffix.format(FOLDER_CURRENT_TIMESTAMP) + "/";
			}
			return folderpattern;
		}

		private static String getOutputPath(Properties properties, String key) {
			String out;
			if (properties != null && properties.get(key) != null)
				out = String.valueOf(properties.get(key));
			else
				out = System.getProperty(key);
			out = out == null || out.equals("null") || out.isEmpty() ? OUTPUT_PATH + key.split("\\.")[2] + "/" : out;
			return getBaseFolderName() + out;
		}

		private static void configureScreenshotProperties() {
			Object property = getProperty(SCREENSHOT_DIR_PROPERTY);
			SCREENSHOT_FOLDER_NAME = property == null || String.valueOf(property).isEmpty()
					? DEFAULT_SCREENSHOT_FOLDER_NAME
					: String.valueOf(property);
			SCREENSHOT_FOLDER_NAME = getBaseFolderName() + SCREENSHOT_FOLDER_NAME;

			property = getProperty(SCREENSHOT_REL_PATH_PROPERTY);
			SCREENSHOT_FOLDER_REPORT_RELATIVE_PATH = property == null || String.valueOf(property).isEmpty()
					? SCREENSHOT_FOLDER_NAME
					: String.valueOf(property);
		}

		private static void configureDeviceAndAuthorProperties() {
			if ("true".equals(String.valueOf(getPropertyOrDefault(DEVICE_ENABLE_SPARK_KEY, "false"))))
				IS_DEVICE_ENABLED = true;
			if ("true".equals(String.valueOf(getPropertyOrDefault(AUTHOR_ENABLE_SPARK_KEY, "false"))))
				IS_AUTHOR_ENABLED = true;

			if (IS_DEVICE_ENABLED) {
				String property = String.valueOf(getPropertyOrDefault(DEVICE_PREFIX_SPARK_KEY, DEFAULT_DEVICE_PREFIX));
				if (!property.isEmpty())
					DEVICE_NAME_PREFIX = property;
			}

			if (IS_AUTHOR_ENABLED) {
				String property = String.valueOf(getPropertyOrDefault(AUTHOR_PREFIX_SPARK_KEY, DEFAULT_AUTHOR_PREFIX));
				if (!property.isEmpty())
					AUTHOR_NAME_PREFIX = property;
			}
		}

		private static void initKlov(Properties properties) {
			ExtentKlovReporter klov = new ExtentKlovReporter("Default");
			String configPath = properties == null ? System.getProperty(CONFIG_KLOV_KEY)
					: String.valueOf(properties.get(CONFIG_KLOV_KEY));
			File f = new File(configPath);
			if (configPath != null && !configPath.isEmpty() && f.exists()) {
				// Object prop = ExtentService.getProperty("screenshot.dir");
				// String screenshotDir = prop == null ? "test-output/" : String.valueOf(prop);
				configureScreenshotProperties();
				String url = Paths.get(SCREENSHOT_FOLDER_NAME).toString();
				ExtentService.getInstance().tryResolveMediaPath(new String[] { url });
				try {
					InputStream is = new FileInputStream(f);
					klov.loadInitializationParams(is);
					INSTANCE.attachReporter(klov);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private static void initSpark(Properties properties) {
			String out = getOutputPath(properties, OUT_SPARK_KEY);
			ExtentSparkReporter spark = new ExtentSparkReporter(out);
			sparkReportViewOrder(spark);
			filterReportStatus(spark);
			base64PngImageStyle();
			attach(spark, properties, CONFIG_SPARK_KEY);
		}

		private static void initHtml(Properties properties) {
			String out = getOutputPath(properties, OUT_HTML_KEY);
			ExtentHtmlReporter html = new ExtentHtmlReporter(out);
			filterReportStatus(html);
			base64PngImageStyle();
			attach(html, properties, CONFIG_HTML_KEY);
		}

		private static void filterReportStatus(ReporterFilterable<?> reporter) {
			try {
				if (getProperty(STATUS_FILTER_KEY) == null)
					return;

				List<Status> statuses = Arrays.stream(String.valueOf(getProperty(STATUS_FILTER_KEY)).split(","))
						.map(s -> convertToStatus(s)).collect(Collectors.toList());
				reporter.filter().statusFilter().as(statuses);
			} catch (Exception e) {
				// Do nothing. Uses no filter.
			}
		}

		private static void sparkReportViewOrder(ExtentSparkReporter spark) {
			try {
				if (getProperty(VIEW_ORDER_SPARK_KEY) == null)
					return;

				List<ViewName> viewOrder = Arrays.stream(String.valueOf(getProperty(VIEW_ORDER_SPARK_KEY)).split(","))
						.map(v -> ViewName.valueOf(v.toUpperCase())).collect(Collectors.toList());
				spark.viewConfigurer().viewOrder().as(viewOrder).apply();
			} catch (Exception e) {
				// Do nothing. Uses default order.
			}
		}

		private static void base64PngImageStyle() {
			if ("true".equals(String.valueOf(getPropertyOrDefault(BASE64_IMAGE_SRC_SPARK_KEY, "false")))) {
				ENABLE_BASE64_IMAGE_SRC = true;
			}
		}

		private static void initJsonf(Properties properties) {
			String out = getOutputPath(properties, OUT_JSONF_KEY);
			JsonFormatter jsonf = new JsonFormatter(out);
			INSTANCE.attachReporter(jsonf);
		}

		private static void initPdf(Properties properties) {
			String out = getOutputPath(properties, OUT_PDF_KEY);
			configureScreenshotProperties();
			MediaCleanupOption mediaCleanup = MediaCleanupOption.builder().cleanUpType(CleanupType.PATTERN)
					.pattern(MediaProcessor.EMBEDDED_PREFIX + ".*").build();
			ExtentPDFCucumberReporter pdf = new ExtentPDFCucumberReporter(out, SCREENSHOT_FOLDER_NAME, mediaCleanup);
			filterReportStatus(pdf);
			INSTANCE.attachReporter(pdf);
		}

		private static void attach(ReporterConfigurable r, Properties properties, String configKey) {
			Object configPath = properties == null ? System.getProperty(configKey) : properties.get(configKey);
			if (configPath != null && !String.valueOf(configPath).isEmpty())
				try {
					r.loadXMLConfig(String.valueOf(configPath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			INSTANCE.attachReporter((ExtentObserver<?>) r);
		}

		private static void addSystemInfo(Properties properties) {
			properties.forEach((k, v) -> {
				String key = String.valueOf(k);
				if (key.startsWith(SYS_INFO_MARKER)) {
					key = key.substring(key.indexOf('.') + 1);
					INSTANCE.setSystemInfo(key, String.valueOf(v));
				}
			});
		}

		private static Status convertToStatus(String status) {
			String lowerStatus = status.toLowerCase();

			switch (lowerStatus) {
			case "info":
				return Status.INFO;
			case "pass":
				return Status.PASS;
			case "Warning":
				return Status.WARNING;
			case "skip":
				return Status.SKIP;
			case "fail":
				return Status.FAIL;
			default:
				throw new IllegalArgumentException();
			}
		}
	}
}

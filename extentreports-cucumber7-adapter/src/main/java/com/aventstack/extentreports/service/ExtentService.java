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
import java.util.stream.Collectors;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.ReporterConfigurable;
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

	public static Object getProperty(String key) {
		String sys = System.getProperty(key);
		return sys == null ? (properties == null ? null : properties.get(key)) : sys;
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
		private static final String BASE64_IMAGE_SRC = "base64imagesrc";
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

		private static boolean ENABLE_BASE64_IMAGE_SRC = false;

		private static String SCREENSHOT_FOLDER_NAME;
		private static String SCREENSHOT_FOLDER_REPORT_RELATIVE_PATH;
		private static final String DEFAULT_SCREENSHOT_FOLDER_NAME = "test-output/";

		private static final String SCREENSHOT_DIR_PROPERTY = "screenshot.dir";
		private static final String SCREENSHOT_REL_PATH_PROPERTY = "screenshot.rel.path";

		public static final String REPORTS_BASEFOLDER_NAME = "basefolder.name";
		public static final String REPORTS_BASEFOLDER_DATETIMEPATTERN = "basefolder.datetimepattern";
		private static final LocalDateTime FOLDER_CURRENT_TIMESTAMP = LocalDateTime.now();

		static {
			createViaProperties();
			createViaSystem();
			configureScreenshotProperties();
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

		private static String getBaseFolderName() {
			String folderpattern = "";
			Object baseFolderPrefix = getProperty(REPORTS_BASEFOLDER_NAME);
			Object baseFolderPatternSuffix = getProperty(REPORTS_BASEFOLDER_DATETIMEPATTERN);

			if (baseFolderPrefix != null && !String.valueOf(baseFolderPrefix).isEmpty()
					&& baseFolderPatternSuffix != null && !String.valueOf(baseFolderPatternSuffix).isEmpty()) {
				DateTimeFormatter folderSuffix = DateTimeFormatter.ofPattern(String.valueOf(baseFolderPatternSuffix));
				folderpattern = baseFolderPrefix + " " + folderSuffix.format(FOLDER_CURRENT_TIMESTAMP) + "/";
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
			base64PngImageStyle();
			attach(spark, properties, CONFIG_SPARK_KEY);
		}

		private static void initHtml(Properties properties) {
			String out = getOutputPath(properties, OUT_HTML_KEY);
			ExtentHtmlReporter html = new ExtentHtmlReporter(out);
			base64PngImageStyle();
			attach(html, properties, CONFIG_HTML_KEY);
		}

		private static void sparkReportViewOrder(ExtentSparkReporter spark) {
			try {
				List<ViewName> viewOrder = Arrays.stream(String.valueOf(getProperty(VIEW_ORDER_SPARK_KEY)).split(","))
						.map(v -> ViewName.valueOf(v.toUpperCase())).collect(Collectors.toList());
				spark.viewConfigurer().viewOrder().as(viewOrder).apply();
			} catch (Exception e) {
				// Do nothing. Use default order.
			}
		}

		private static void base64PngImageStyle() {
			if ("true".equals(String.valueOf(properties.getOrDefault(BASE64_IMAGE_SRC_SPARK_KEY, "false")))) {
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
	}
}

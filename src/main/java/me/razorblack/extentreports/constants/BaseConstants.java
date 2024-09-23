package me.razorblack.extentreports.constants;

import java.time.LocalDateTime;

public class BaseConstants {

    public static final String PRETTY = "pretty";
    public static final String TEST_SPECS_PATH = "src/test/resources/test-specs";
    public static final String CUSTOM_EXTENT_REPORT_PLUGIN = "me.razorblack.extentreports.cucumber.adapter.ExtentCucumberAdapter:";
    public static final String STEP_DEF_PATH = "me.razorblack.stepdefinition";
    public static final String SYS_INFO_MARKER = "systeminfo.";
    public static final String OUTPUT_PATH = "test-output/";
    public static final String EXTENT_REPORTER = "extent.reporter";
    public static final String START = "start";
    public static final String CONFIG = "config";
    public static final String OUT = "out";
    public static final String VIEW_ORDER = "vieworder";
    public static final String STATUS_FILTER = "statusfilter";
    public static final String BASE64_IMAGE_SRC = "base64imagesrc";
    public static final String ENABLE_DEVICE = "enable.device";
    public static final String ENABLE_AUTHOR = "enable.author";
    public static final String PREFIX_DEVICE = "prefix.device";
    public static final String PREFIX_AUTHOR = "prefix.author";
    public static final String DELIM = ".";

    public static final String KLOV = "klov";
    public static final String SPARK = "spark";
    public static final String JSONF = "json";
    public static final String PDF = "pdf";
    public static final String HTML = "html";
    public static final String EXCEL = "excel";
    public static final String INIT_KLOV_KEY = EXTENT_REPORTER + DELIM + KLOV + DELIM + START;
    public static final String INIT_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + START;
    public static final String INIT_JSONF_KEY = EXTENT_REPORTER + DELIM + JSONF + DELIM + START;
    public static final String INIT_PDF_KEY = EXTENT_REPORTER + DELIM + PDF + DELIM + START;
    public static final String INIT_HTML_KEY = EXTENT_REPORTER + DELIM + HTML + DELIM + START;
    public static final String INIT_EXCEL_KEY = EXTENT_REPORTER + DELIM + EXCEL + DELIM + START;

    public static final String CONFIG_KLOV_KEY = EXTENT_REPORTER + DELIM + KLOV + DELIM + CONFIG;
    public static final String CONFIG_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + CONFIG;
    public static final String CONFIG_HTML_KEY = EXTENT_REPORTER + DELIM + HTML + DELIM + CONFIG;

    public static final String OUT_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + OUT;
    public static final String OUT_JSONF_KEY = EXTENT_REPORTER + DELIM + JSONF + DELIM + OUT;
    public static final String OUT_PDF_KEY = EXTENT_REPORTER + DELIM + PDF + DELIM + OUT;
    public static final String OUT_HTML_KEY = EXTENT_REPORTER + DELIM + HTML + DELIM + OUT;
    public static final String OUT_EXCEL_KEY = EXTENT_REPORTER + DELIM + EXCEL + DELIM + OUT;

    public static final String VIEW_ORDER_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + VIEW_ORDER;
    // Use below for both Spark & Html reporters
    public static final String BASE64_IMAGE_SRC_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM
            + BASE64_IMAGE_SRC;

    public static final String DEVICE_ENABLE_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + ENABLE_DEVICE;
    public static final String AUTHOR_ENABLE_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + ENABLE_AUTHOR;
    public static final String DEVICE_PREFIX_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + PREFIX_DEVICE;
    public static final String AUTHOR_PREFIX_SPARK_KEY = EXTENT_REPORTER + DELIM + SPARK + DELIM + PREFIX_AUTHOR;
    

    // Use below for both Spark & Pdf reporters
    public static final String STATUS_FILTER_KEY = EXTENT_REPORTER + DELIM + STATUS_FILTER;

    public static final String DEFAULT_SCREENSHOT_FOLDER_NAME = "test-output/";

    public static final String SCREENSHOT_DIR_PROPERTY = "screenshot.dir";
    public static final String SCREENSHOT_REL_PATH_PROPERTY = "screenshot.rel.path";

    public static final String REPORTS_BASEFOLDER = "basefolder";
    public static final String REPORTS_BASEFOLDER_NAME = REPORTS_BASEFOLDER + DELIM + "name";
    public static final String REPORTS_BASEFOLDER_DATETIMEPATTERN = REPORTS_BASEFOLDER + DELIM + "datetimepattern";
    public static final String REPORTS_BASEFOLDER_ENABLEDELIMITER = REPORTS_BASEFOLDER + DELIM
            + "enable.delimiter";
    public static final String REPORTS_BASEFOLDER_DELIMITER = REPORTS_BASEFOLDER + DELIM + "delimiter";
    public static final LocalDateTime FOLDER_CURRENT_TIMESTAMP = LocalDateTime.now();
}

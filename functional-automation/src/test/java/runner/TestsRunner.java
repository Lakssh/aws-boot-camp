package runner;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stepdefinitions.CukeHooks;


@RunWith(Cucumber.class)
@CucumberOptions(strict = true, monochrome = true,
        features = "src/test/resources/features/",
        tags = "@UITest and @Smoke",
        glue = {"stepdefinitions"},
        plugin = {"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"}
)
public class TestsRunner {

    private TestsRunner() {

    }

    protected static final Logger LOG = LoggerFactory.getLogger(TestsRunner.class);
    protected static Integer passedCount;
    protected static Integer failedCount;
    protected static String finalOutput = "{\"moduleName\":regressiontest_ui/regressiontest_api/regressiontest_rwd_ui,\"pass\":passCount,\"fail\":failCount,\"status\":In Progress/Pass/Fail};";

    @BeforeClass
    public static void beforeClass() {
        LOG.info("########### INSIDE BEFORE CLASS ###########");
        CukeHooks.passedCount = 0;
        CukeHooks.failedCount = 0;

        String functionalType = getFunctionalType();
        String strFunctionalType = "functionalType : " + functionalType;
        LOG.info(strFunctionalType);
        String status = "PASS";

        JSONObject sampleObject = new JSONObject();
        sampleObject.put("moduleName", functionalType);
        sampleObject.put("pass", 0);
        sampleObject.put("fail", 0);
        sampleObject.put("status", "In Progress");
        String strDashboardHost;

        String strDashboardIP = System.getenv("backendPrivateIP");
        if (strDashboardIP == null || strDashboardIP.isEmpty()) {
            strDashboardHost = System.getProperty("dashboardHost");
            LOG.info("Dashboard URL Fetched from POM Settings");
        } else {
            strDashboardHost = strDashboardIP;
            LOG.info("Dashboard URL Fetched from AWS ENVIRONMENT");
        }

        String strDashboardHostURL = "http://" + strDashboardHost + ":3337/api/v1/aws_dashboard_report/moudule/update";
        String strAppurl = "API URL : " + strDashboardHostURL;
        LOG.info(strAppurl);

        LOG.info("############ DASHBOARD OBJECT ##########");
        LOG.info(String.valueOf(sampleObject));
        try{
            Response response = RestAssured.given().contentType("application/json")
                    .body(sampleObject)
                    .post(strDashboardHostURL);
            LOG.info(response.body().prettyPrint());
        } catch(Exception e) {
            LOG.info("UNABLE TO MAKE POST REQUEST TO THE DASHBOARD");
            LOG.info("################## START ERROR MESSAGE ##################");
            LOG.info(e.getMessage());
            LOG.info("################## END ERROR MESSAGE ##################");

        }
    }

    private static String getFunctionalType() {
        String functionalType = System.getProperty("cucumber.options").trim().toUpperCase();
        String funcationalTypeLocal = "";

        if (functionalType.contains("RWD")) {
            funcationalTypeLocal = "regressiontest_rwd_ui";
        } else if (functionalType.contains("UITEST")) {
            if (functionalType.contains("SMOKE")) {
                funcationalTypeLocal = "smoketest";
            } else {
                funcationalTypeLocal = "regressiontest_ui";
            }
        } else if (functionalType.contains("APITEST")) {
            if (functionalType.contains("SMOKE")) {
                funcationalTypeLocal = "smoketest";
            } else {
                funcationalTypeLocal = "regressiontest_api";
            }
        } else {
            funcationalTypeLocal = "smoketest";
        }

        return funcationalTypeLocal;
    }

    @AfterClass
    public static void afterSuite() {
        LOG.info("########### INSIDE AFTER CLASS ###########");
        String functionalType = getFunctionalType();
        String strFunctionalType = "functionalType : " + functionalType;
        LOG.info(strFunctionalType);
        String status = "PASS";
        if (CukeHooks.failedCount > 0) {
            status = "FAIL";
        }

        JSONObject sampleObject = new JSONObject();
        sampleObject.put("moduleName", functionalType);
        sampleObject.put("pass", CukeHooks.passedCount);
        sampleObject.put("fail", CukeHooks.failedCount);
        sampleObject.put("status", status);
        String strDashboardHost;

        String strDashboardIP = System.getenv("backendPrivateIP");
        if (strDashboardIP == null || strDashboardIP.isEmpty()) {
            strDashboardHost = System.getProperty("dashboardHost");
            LOG.info("Dashboard URL Fetched from POM Settings");
        } else {
            strDashboardHost = strDashboardIP;
            LOG.info("Dashboard URL Fetched from AWS ENVIRONMENT");
        }

        String strDashboardHostURL = "http://" + strDashboardHost + ":3337/api/v1/aws_dashboard_report/moudule/update";
        String strAppurl = "API URL : " + strDashboardHostURL;
        LOG.info(strAppurl);

        LOG.info("############ DASHBOARD OBJECT ##########");
        LOG.info(String.valueOf(sampleObject));
        try{
            Response response = RestAssured.given().contentType("application/json")
                    .body(sampleObject)
                    .post(strDashboardHostURL);
            LOG.info(response.body().prettyPrint());
        } catch(Exception e) {
            LOG.info("UNABLE TO MAKE POST REQUEST TO THE DASHBOARD");
            LOG.info("################## START ERROR MESSAGE ##################");
            LOG.info(e.getMessage());
            LOG.info("################## END ERROR MESSAGE ##################");

        }
    }
}

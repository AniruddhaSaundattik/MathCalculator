package service;

import com.google.gson.Gson;
import config.CalcConfig;
import model.Request;
import model.Result;
import repository.AuditRepository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CalcService {

    private static final Logger logger = Logger.getLogger(CalcService.class.getName());
    private static final String APPL_JSON = "application/json";
    private static final Gson gson = new Gson();

    HttpURLConnection conn = null;
    AuditRepository repository;
    CalcConfig config;

    public CalcService(CalcConfig config){
        this.config = config;
        this.repository = new AuditRepository(config);
    }

    public Result calcWithAudit(Request request) {

        CompletableFuture<Void> traceFuture = CompletableFuture.runAsync(() -> {
            try {
                logger.info("Creating trace");
                repository.addTrace(request.getExpr());
                logger.info("Created trace");
            } catch (ClassNotFoundException | SQLException e) {
                logger.severe("Error while inserting trace" + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });

        logger.info("Started calc");
        Result response = getCalculationResult(request);
        logger.info("Calculation done");
        traceFuture.thenRunAsync(() -> {
            try {
                logger.info("Updating trace");
                repository.updateTrace(response.getResult());
                logger.info("Updated trace");
                repository.closeConnection();
            } catch (ClassNotFoundException | SQLException e) {
                logger.severe("Error while updating trace" + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });
        return response;
    }

    private Result getCalculationResult(Request request) {
        logger.info("Hitting external calculator");
        Result response;
        try {
            URL url = new URL(config.getPropertyValue("calculator.url"));
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", APPL_JSON);
            conn.setRequestProperty("Content-Type", APPL_JSON);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            byte[] input = gson.toJson(request).getBytes();
            os.write(input, 0, input.length);

            StringBuilder stringBuilderResponse;
            InputStream is;
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is));
            stringBuilderResponse = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                stringBuilderResponse.append(responseLine.trim());
            }
            logger.info("Got response successfully from external API");
            response = gson.fromJson(stringBuilderResponse.toString(), Result.class);
            response.setResponseCode(responseCode);
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
            String msg = "Server error while calculating: " + ex.getMessage();
            response = new Result(null, msg, 500);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return response;
    }
}

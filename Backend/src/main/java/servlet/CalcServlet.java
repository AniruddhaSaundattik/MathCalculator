package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import config.CalcConfig;
import model.Request;
import model.Result;
import service.CalcService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebServlet("/calc")
public class CalcServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CalcServlet.class.getName());
    private static final Gson gson = new Gson();
    private static final String APPL_JSON = "application/json";

    CalcService service;
    CalcConfig config;

    @Override
    public void init() {
        this.config = new CalcConfig();
        this.service = new CalcService(config);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {

            String requestBody = getBody(request);
            Request input = gson.fromJson(requestBody, Request.class);

            Result result = service.calcWithAudit(input);

            PrintWriter out = response.getWriter();
            response.setContentType(APPL_JSON);
            response.setStatus(result.getResponseCode());
            out.print(gson.toJson(result));
            out.flush();

        } catch (JsonSyntaxException e) {
            logger.severe("Request payload is not correct" + e.getMessage());
            response.setStatus(400);
        } catch (Exception e) {
            logger.severe("Error while calculating the expressions: " + e.getMessage());
            response.setStatus(500);
        }
    }

    @Override
    public void destroy() {
        this.service.closeConnections();
    }

    private String getBody(HttpServletRequest request) throws IOException {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append(System.lineSeparator());
        }
        reader.close();
        return buffer.toString();
    }
}

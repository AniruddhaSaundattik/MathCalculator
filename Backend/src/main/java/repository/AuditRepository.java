package repository;

import config.CalcConfig;

import java.sql.*;
import java.util.logging.Logger;

public class AuditRepository {

    private static final Logger logger = Logger.getLogger(AuditRepository.class.getName());
    private int maxId;
    private Connection conn;
    private final CalcConfig config;

    public AuditRepository(CalcConfig config) {
        this.config = config;
    }

    public void addTrace(String[] formulae) throws ClassNotFoundException, SQLException {
        conn = getConnection();
        maxId = getLastSeq();

        PreparedStatement statement = conn.prepareStatement(config.getPropertyValue("query.insert"));
        for (int i=0; i<formulae.length; i++) {
            statement.setInt(1, maxId+1+i);
            statement.setString(2, formulae[i]);
            statement.addBatch();
        }

        statement.executeBatch();
        logger.info("All audit traces created");
    }

    public void updateTrace(String[] result) throws SQLException, ClassNotFoundException {
        conn = getConnection();

        PreparedStatement statement = conn.prepareStatement(config.getPropertyValue("query.update"));
        for (int i=0; i<result.length; i++) {
            statement.setString(1, result[i]);
            statement.setInt(2, maxId+1+i);
            statement.addBatch();
        }

        statement.executeBatch();
        logger.info("All traces updated");
    }

    public int getLastSeq() throws SQLException, ClassNotFoundException {
        logger.info("Getting the last audit index");
        conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(config.getPropertyValue("query.maxId"));
        ResultSet resultSet = stmt.executeQuery();
        int maxid = 0;
        try {
            resultSet.next();
            maxid = resultSet.getInt(1);
        } catch (Exception e) {
            logger.info("No table entries found");
        }
        return maxid;
    }

    public void closeConnection() throws SQLException {
        conn.close();
        conn = null;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(config.getPropertyValue("jdbc.driver"));
        if (conn == null) {
            conn = DriverManager.getConnection(config.getPropertyValue("database.url"),
                    config.getPropertyValue("database.username"), config.getPropertyValue("database.password"));
            logger.info("Audit database connection created");
        }
        return conn;
    }
}

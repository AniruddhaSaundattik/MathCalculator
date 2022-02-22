package model;

public class Result {

    private String[] result;
    private String error;

    private int responseCode;

    public Result(String[] result, String error, int respCode) {
        this.result = result;
        this.error = error;
        this.responseCode = respCode;
    }

    public Result() {

    }

    public String[] getResult() {
        return result;
    }

    public void setResult(String[] result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}

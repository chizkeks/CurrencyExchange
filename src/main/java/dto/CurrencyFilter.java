package dto;

public class CurrencyFilter {
    private String code;
    private String fullName;
    private String sign;
    private int limit;
    private int offset;

    public CurrencyFilter(String code, String fullName, String sign, int limit, int offset) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
        this.limit = limit;
        this.offset = offset;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Currency filter:" +
                "\ncode = " + this.code +
                "\nfullName = " + this.fullName +
                "\nsign = " + this.sign +
                "\nlimit = " + this.limit +
                "\noffset = " + this.offset;
    }
}

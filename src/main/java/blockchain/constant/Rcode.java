package blockchain.constant;

/**
 * @author bjyiguoqiang on 2018/4/4.
 */
public enum Rcode {
    BIZ_FAILED(4001, "处理失败"),
    BIZ_REPEAT(4002, "重复处理"),
    BIZ_INVALID(4003, "验证失败"),
    BIZ_ERROR(5001, "内部错误");

    private int code;
    private String msg;

    Rcode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

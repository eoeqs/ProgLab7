package me.lab7.common.utility;

public class Constraints {

    private final DataType dataType;
    private final boolean nullable;
    private final boolean positive;
    private final String inviteMessage;
    private final String wrongTypeMessage;
    private final String noInputMessage;

    //no arg
    public Constraints() {
        dataType = null;
        nullable = true;
        positive = false;
        inviteMessage = null;
        wrongTypeMessage = null;
        noInputMessage = null;
    }

    //arg
    public Constraints(DataType dataType, String wrongTypeMessage, String noInputMessage) {
        this.dataType = dataType;
        nullable = false;
        positive = false;
        inviteMessage = null;
        this.wrongTypeMessage = wrongTypeMessage;
        this.noInputMessage = noInputMessage;
    }

    //field no messages
    public Constraints(DataType dataType, boolean nullable, boolean positive) {
        this.dataType = dataType;
        this.nullable = nullable;
        this.positive = positive;
        this.inviteMessage = null;
        this.wrongTypeMessage = null;
        this.noInputMessage = null;
    }

    //field
    public Constraints(DataType dataType, boolean nullable, boolean positive,
                       String inviteMessage, String wrongTypeMessage, String noInputMessage) {
        this.dataType = dataType;
        this.nullable = nullable;
        this.positive = positive;
        this.inviteMessage = inviteMessage;
        this.wrongTypeMessage = wrongTypeMessage;
        this.noInputMessage = noInputMessage;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isPositive() {
        return positive;
    }

    public String getInviteMessage() {
        return inviteMessage;
    }

    public String getWrongTypeMessage() {
        return wrongTypeMessage;
    }

    public String getNoInputMessage() {
        return noInputMessage;
    }
}

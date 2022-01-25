package io.aregger.odff.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ConnectionDefinition(String name, String tnsString, String username, String password) {

    public String buildJdbcConnectionString() {
        return "jdbc:oracle:thin:" + this.username + "/" + this.password + "@" + this.tnsString;
    }

    public List<String> validate() {
        List<String> result = new ArrayList<>();
        if (this.name == null || this.name.length() == 0) {
            result.add("name not present");
        }
        if (this.tnsString == null || this.tnsString.length() == 0) {
            result.add("tnsString not present");
        }
        if (this.username == null || this.username.length() == 0) {
            result.add("username not present");
        }
        if (this.password == null || this.password.length() == 0) {
            result.add("password not present and not provided on invocation");
        }
        return Collections.unmodifiableList(result);
    }

}

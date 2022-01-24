package io.aregger.odff.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ConnectionDefinition(String name, String tnsString, String username, String password) {

    public String buildJdbcConnectionString() {
        return "jdbc:oracle:thin:" + username + "/" + password + "@" + tnsString;
    }

    public List<String> validate() {
        List<String> result = new ArrayList<>();
        if (name == null || name.length() == 0) {
            result.add("name not present");
        }
        if (tnsString == null || tnsString.length() == 0) {
            result.add("tnsString not present");
        }
        if (username == null || username.length() == 0) {
            result.add("username not present");
        }
        if (password == null || password.length() == 0) {
            result.add("password not present and not provided on invocation");
        }
        return Collections.unmodifiableList(result);
    }

}

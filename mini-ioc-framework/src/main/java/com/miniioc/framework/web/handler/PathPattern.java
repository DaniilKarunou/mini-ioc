package com.miniioc.framework.web.handler;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PathPattern {

    private final Pattern pattern;
    private final String[] variableNames;
    private final String template;

    public PathPattern(String pathTemplate) {
        this.template = pathTemplate;

        List<String> varNames = new ArrayList<>();

        String regex = Arrays.stream(pathTemplate.split("/"))
                .map(part -> {
                    if (part.startsWith("{") && part.endsWith("}")) {
                        String varName = part.substring(1, part.length() - 1);
                        varNames.add(varName);
                        return "([^/]+)";
                    } else {
                        return Pattern.quote(part);
                    }
                })
                .reduce((a, b) -> a + "/" + b)
                .orElse("");

        this.pattern = Pattern.compile("^" + regex + "$");
        this.variableNames = varNames.toArray(new String[0]);
    }

    public boolean matches(String path) {
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

    public Map<String, String> extract(String path) {
        Map<String, String> vars = new HashMap<>();
        Matcher matcher = pattern.matcher(path);

        if (!matcher.matches()) {
            return vars;
        }

        for (int i = 0; i < variableNames.length; i++) {
            vars.put(variableNames[i], matcher.group(i + 1));
        }
        return vars;
    }

    public String getTemplate() {
        return template;
    }
}
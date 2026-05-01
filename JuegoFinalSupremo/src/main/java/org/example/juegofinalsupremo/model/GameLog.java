package org.example.juegofinalsupremo.model;

import org.example.juegofinalsupremo.data.MyList;

public class GameLog {
    private final MyList<String> entries = new MyList<String>();

    public void add(String entry) {
        entries.add(entry);
    }

    public MyList<String> getEntries() {
        return entries;
    }

    public String asText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            builder.append(i + 1).append(". ").append(entries.get(i));
            if (i < entries.size() - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }
}

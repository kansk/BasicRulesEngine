package gov.va.vba.vbms.rules;

import java.io.Serializable;

public class Rule implements Comparable<Rule>, Serializable {
    private static final long serialVersionUID = -8450457014011112167L;

    private String name;
    private String expression;
    private String description;
    private String result;
    private int priority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int compareTo(Rule rule) {
        //reversed, since we want highest priority first in the list!
        return (this.priority < rule.priority ? 1 : (this.priority == rule.priority ? 0 : -1));
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", expression='" + expression + '\'' +
                ", description='" + description + '\'' +
                ", result='" + result + '\'' +
                ", priority=" + priority +
                '}';
    }
}

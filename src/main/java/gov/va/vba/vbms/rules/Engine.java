package gov.va.vba.vbms.rules;

import org.mvel2.MVEL;
import org.mvel2.PropertyAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class Engine {

    private static final Logger log = LoggerFactory.getLogger(Engine.class);

    private final List<CompiledRule> compiledRules = new ArrayList<CompiledRule>();

    public Engine(Rule... rules) {
        for (Rule rule : rules) {
            compiledRules.add(new CompiledRule(rule));
        }
    }

    /**
     * Evaluates all compiledRules against the input and returns the result of the outcome associated with the rule having the highest priority.
     *
     * @param nameSpacePattern optional.  if not null, then only compiledRules with matching namespaces are evaluated.
     * @param input            the Object containing all inputs to the expression language rule.
     * @return The outcome belonging to the best rule which is found.
     */
    public <Input> String getMatchedResult(String nameSpacePattern, Input input, Map<String, Object> additionalVars) {

        List<Rule> matches = getMatchingRules(nameSpacePattern, input, additionalVars);
        if (matches == null || matches.isEmpty()) {
            return null;
        } else {
            return matches.get(0).getResult();
        }
    }

    /**
     * @param nameSpacePattern optional.  if not null, then only compiledRules with matching namespaces are evaluated.
     * @param input            the Object containing all inputs to the expression language rule.
     * @return an ordered list of Rules which evaluated to "true", sorted by {@link Rule#getPriority()}, with the highest priority compiledRules first in the list.
     */
    public <Input> List<Rule> getMatchingRules(String nameSpacePattern, Input input, Map<String, Object> additionalVars) {

//        Pattern pattern = null;
//        if(nameSpacePattern != null){
//            pattern = Pattern.compile(nameSpacePattern);
//        }

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("input", input);
        vars.putAll(additionalVars);

        List<Rule> matchingRules = new ArrayList<Rule>();
        for (CompiledRule r : compiledRules) {

//            if(pattern != null){
//                if(!pattern.matcher(r.getRule().getNamespace()).matches()){
//                    continue;
//                }
//            }

            Object object = null;
            try {
                object = MVEL.executeExpression(r.getCompiled(), vars);
            } catch (PropertyAccessException e) {
                log.info("No match for the expression with supplied variables", e);
            }

            String msg = r.getRule().getName() + "-{" + r.getRule().getExpression() + "}";
            if (String.valueOf(object).equals("true")) {
                matchingRules.add(r.getRule());
                log.info("matched: " + msg);
            } else {
                log.info("unmatched: " + msg);
            }
        }

        //order by priority!
        Collections.sort(matchingRules);

        return matchingRules;
    }

    private static final class CompiledRule {
        private Rule rule;
        private Serializable compiled;

        public CompiledRule(Rule rule) {
            this.rule = rule;
            this.compiled = MVEL.compileExpression(rule.getExpression());
        }

        public Serializable getCompiled() {
            return compiled;
        }

        public Rule getRule() {
            return rule;
        }
    }
}

package gov.va.vba.vbms.rules;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RulesTest {

    private Engine rulesEngine;

    @Before
    public void setUp() {
        Rule specialIssuesRule = new Rule();
        specialIssuesRule.setName("Special Issues");
        specialIssuesRule.setDescription("This is a description");
        specialIssuesRule.setResult("TRUE");
        specialIssuesRule.setPriority(1);
        specialIssuesRule.setExpression("!java.util.Collections.disjoint(input,specialIssues)");

        Rule segmentedLaneRule = new Rule();
        segmentedLaneRule.setName("Segmented Lanes");
        segmentedLaneRule.setDescription("This is a rule for segmented lanes");
        segmentedLaneRule.setResult("TRUE");
        segmentedLaneRule.setPriority(2);
        segmentedLaneRule.setExpression("segmentedLanes.contains(input.segmentedLane.ID)");

        Rule terminalDigitRule1 = new Rule();
        terminalDigitRule1.setName("Terminal Digit Range");
        terminalDigitRule1.setDescription("This is a rule for terminal digits");
        terminalDigitRule1.setResult("TRUE");
        terminalDigitRule1.setPriority(3);
        terminalDigitRule1.setExpression("input.size() == 2 && (input.get(0) <= terminalDigit && (input.get(1) >= terminalDigit))");

        Rule terminalDigitRule2 = new Rule();
        terminalDigitRule2.setName("Terminal Digit Single");
        terminalDigitRule2.setDescription("This is a rule for terminal digits");
        terminalDigitRule2.setResult("TRUE");
        terminalDigitRule2.setPriority(4);
        terminalDigitRule2.setExpression("input.size() == 1 && (input.get(0) == terminalDigit)");

        rulesEngine = new Engine(specialIssuesRule, segmentedLaneRule, terminalDigitRule1, terminalDigitRule2);
    }

    @Test
    public void specialIssuesRuleMatches() {
        List<String> claimAssignRuleValues = new ArrayList<String>();
        claimAssignRuleValues.add("test");

        List<String> specialIssues = new ArrayList<String>();
        specialIssues.add("1");
        specialIssues.add("2");
        specialIssues.add("test");

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("specialIssues", specialIssues);

        String result = rulesEngine.getMatchedResult(null, claimAssignRuleValues, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertTrue(ruleMatches);

//        vars.put("input", claimAssignRuleValues);
//        Boolean s = (Boolean) MVEL.eval("!java.util.Collections.disjoint(input,specialIssues)", vars);
//        System.out.println(s);
    }

    @Test
    public void specialIssuesRuleNoMatch() {
        List<String> claimAssignRuleValues = new ArrayList<String>();
        claimAssignRuleValues.add("test");

        List<String> specialIssues = new ArrayList<String>();
        specialIssues.add("1");
        specialIssues.add("2");

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("specialIssues", specialIssues);

        String result = rulesEngine.getMatchedResult(null, claimAssignRuleValues, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertFalse(ruleMatches);
    }

    @Test
    public void segmentedLaneRuleMatches() {
        Claim claim = new Claim();
        SegmentedLane segmentedLane = new SegmentedLane();
        segmentedLane.setID("3222");
        claim.setSegmentedLane(segmentedLane);

        List<String> segmentedLanes = new ArrayList<String>();
        segmentedLanes.add("1");
        segmentedLanes.add("2");
        segmentedLanes.add("3222");

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("segmentedLanes", segmentedLanes);

        String result = rulesEngine.getMatchedResult(null, claim, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertTrue(ruleMatches);
    }

    @Test
    public void segmentedLaneRuleNoMatch() {
        Claim claim = new Claim();
        SegmentedLane segmentedLane = new SegmentedLane();
        segmentedLane.setID("3222");
        claim.setSegmentedLane(segmentedLane);

        List<String> segmentedLanes = new ArrayList<String>();
        segmentedLanes.add("1");
        segmentedLanes.add("2");

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("segmentedLanes", segmentedLanes);

        String result = rulesEngine.getMatchedResult(null, claim, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertFalse(ruleMatches);
    }

    @Test
    public void terminalDigitRule1Match() {
        List<Long> claimAssignRuleValues = new ArrayList<Long>();
        claimAssignRuleValues.add(1234L);
        claimAssignRuleValues.add(9999L);


        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("terminalDigit", 1234L);

        String result = rulesEngine.getMatchedResult(null, claimAssignRuleValues, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertTrue(ruleMatches);

        List<Rule> rules = rulesEngine.getMatchingRules(null, claimAssignRuleValues, vars);
        assertTrue(rules.size() == 1);
        assertTrue("Terminal Digit Range".equals(rules.get(0).getName()));
    }

    @Test
    public void terminalDigitRule1NoMatch() {
        List<Long> claimAssignRuleValues = new ArrayList<Long>();
        claimAssignRuleValues.add(1234L);
        claimAssignRuleValues.add(9999L);

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("terminalDigit", 323L);

        String result = rulesEngine.getMatchedResult(null, claimAssignRuleValues, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertFalse(ruleMatches);

        List<Rule> rules = rulesEngine.getMatchingRules(null, claimAssignRuleValues, vars);
        assertTrue(rules.size() == 0);
    }

    @Test
    public void terminalDigitRule2Match() {
        List<Long> claimAssignRuleValues = new ArrayList<Long>();
        claimAssignRuleValues.add(1234L);

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("terminalDigit", 1234L);

        String result = rulesEngine.getMatchedResult(null, claimAssignRuleValues, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertTrue(ruleMatches);

        List<Rule> rules = rulesEngine.getMatchingRules(null, claimAssignRuleValues, vars);
        assertTrue(rules.size() == 1);
        assertTrue("Terminal Digit Single".equals(rules.get(0).getName()));
    }

    @Test
    public void terminalDigitRule2NoMatch() {
        List<Long> claimAssignRuleValues = new ArrayList<Long>();
        claimAssignRuleValues.add(1234L);

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("terminalDigit", 83232L);

        String result = rulesEngine.getMatchedResult(null, claimAssignRuleValues, vars);
        Boolean ruleMatches = Boolean.valueOf(result);
        assertFalse(ruleMatches);

        List<Rule> rules = rulesEngine.getMatchingRules(null, claimAssignRuleValues, vars);
        assertTrue(rules.size() == 0);
    }

    public class Claim {
        private SegmentedLane segmentedLane;

        public SegmentedLane getSegmentedLane() {
            return segmentedLane;
        }

        public void setSegmentedLane(SegmentedLane segmentedLane) {
            this.segmentedLane = segmentedLane;
        }
    }

    public class SegmentedLane {
        private String ID;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }
    }
}

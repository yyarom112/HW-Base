package il.ac.bgu.cs.formalmethodsintro.base;

import il.ac.bgu.cs.formalmethodsintro.base.circuits.Circuit;
import il.ac.bgu.cs.formalmethodsintro.base.exceptions.StateNotFoundException;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ActionDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ConditionDef;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.PGTransition;
import il.ac.bgu.cs.formalmethodsintro.base.programgraph.ProgramGraph;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TSTransition;
import il.ac.bgu.cs.formalmethodsintro.base.transitionsystem.TransitionSystem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class FvmFacadeTest {


    FvmFacade fvmFacade;
    TransitionSystem simple;
    TransitionSystem notDeterministic;
    AlternatingSequence simpleSq;
    AlternatingSequence simpleSqNotInitial;

    TransitionSystem interval_withHandshake1;
    TransitionSystem interval_withHandshake2;
    TransitionSystem interval_ans1_emptyH;
    TransitionSystem interval_ans2_Hisa;
    TransitionSystem interval_ans3__Hisa;

    Circuit c;

    ProgramGraph<String,String> pg;






    @Before
    public void setUp() throws Exception {
        fvmFacade=new FvmFacade();
        simple=buildSimpleTS();
        notDeterministic=buildnotDeterministicTS();
        simpleSq=buildSimpleSq();
        simpleSqNotInitial=buildSimpleSqNotInitial();
        c=buildCircuit();
        pg=buildPG();

        intervalCreation();

    }

    private ProgramGraph<String, String> buildPG() {
        ProgramGraph<String, String> output=new ProgramGraph<>();
        for(int i=0;i<2;i++)
            output.addLocation("L"+i);
        output.setInitial("L0",true);
        List<String> initalization=new ArrayList<>(Arrays.asList("x==1"));
        output.addInitalization(initalization);
        initalization=new ArrayList<>(Arrays.asList("x==2"));
        output.addInitalization(initalization);
        output.addTransition(new PGTransition<>(
                "L0",
                "true",
                "x=x+1",
                "L1"
        ));
        return output;
    }

    private Circuit buildCircuit() {
        Circuit output=new Circuit() {
            @Override
            public Set<String> getInputPortNames() {
                return new HashSet<String>(Arrays.asList("x"));
            }

            @Override
            public Set<String> getRegisterNames() {
                return new HashSet<String>(Arrays.asList("r1","r2"));
            }

            @Override
            public Set<String> getOutputPortNames() {
                return new HashSet<String>(Arrays.asList("y"));
            }

            @Override
            public Map<String, Boolean> updateRegisters(Map<String, Boolean> inputs, Map<String, Boolean> registers) {
                Map<String, Boolean> output=new HashMap<>();
                boolean r1= (inputs.get("x") && !registers.get("r1")) || (!inputs.get("x") && registers.get("r1"));
                boolean r2=(!registers.get("r1")) && (inputs.get("x"));
                output.put("r1",r1);
                output.put("r2",r2);
                return output;
            }

            @Override
            public Map<String, Boolean> computeOutputs(Map<String, Boolean> inputs, Map<String, Boolean> registers) {
                Map<String, Boolean> output=new HashMap<>();
                output.put("y",registers.get("r1") && registers.get("r2"));
                return output;
            }
        };
        return output;
    }

    private void intervalCreation() {
        interval_withHandshake1=new TransitionSystem();
        interval_withHandshake1.addInitialState(1);
        interval_withHandshake1.addAllStates(new Integer[]{2});
        interval_withHandshake1.addAllActions(new String[]{"l"});
        interval_withHandshake1.addTransition(new TSTransition(1,"l",2));
        interval_withHandshake1.addAllAtomicPropositions(new String[]{"x1","y1"});
        interval_withHandshake1.addToLabel(1,"x1");
        interval_withHandshake1.addToLabel(2,"y1");

        interval_withHandshake2=new TransitionSystem();
        interval_withHandshake2.addInitialState('a');
        interval_withHandshake2.addAllStates(new Character[]{'b'});
        interval_withHandshake2.addAllActions(new String[]{"m"});
        interval_withHandshake2.addTransition(new TSTransition('a',"m",'b'));
        interval_withHandshake2.addAllAtomicPropositions(new String[]{"x2","y2"});
        interval_withHandshake2.addToLabel('a',"x2");
        interval_withHandshake2.addToLabel('b',"y2");
    }

    private AlternatingSequence buildSimpleSq() {
        List<Integer> States= new ArrayList<Integer>(Arrays.asList(1, 3,4)) ;
        List<Character> Actions= new ArrayList<Character>(Arrays.asList('b','c'));
        AlternatingSequence output=new AlternatingSequence(States,Actions);
        return output;
    }

    private AlternatingSequence buildSimpleSqNotInitial() {
        List<Integer> States= new ArrayList<Integer>(Arrays.asList(3,4)) ;
        List<Character> Actions= new ArrayList<Character>(Arrays.asList('c'));
        AlternatingSequence output=new AlternatingSequence(States,Actions);
        return output;
    }

    private TransitionSystem buildnotDeterministicTS() {
        TransitionSystem ts=new TransitionSystem();
        ts.addInitialState(1);
        ts.addAllStates(new Integer[]{2,3,4,5});
        ts.addAllActions(new Character[]{'a','b','c'});


        ts.addTransition(new TSTransition(1,'a',2));
        ts.addTransition(new TSTransition(1,'a',3));
        ts.addTransition(new TSTransition(3,'c',4));
        ts.addTransition(new TSTransition(2,'b',2));

        ts.addAllAtomicPropositions(new String[]{"x","y","z"});


        ts.addToLabel(1,"x");
        ts.addToLabel(2,"y");
        ts.addToLabel(3,"y");
        ts.addToLabel(4,"z");

        return ts;
    }

    private TransitionSystem buildSimpleTS() {
        TransitionSystem ts=new TransitionSystem();
        ts.addInitialState(1);
        ts.addAllStates(new Integer[]{2,3,4,5});
        ts.addAllActions(new Character[]{'a','b','c'});


        ts.addTransition(new TSTransition(1,'a',2));
        ts.addTransition(new TSTransition(1,'b',3));
        ts.addTransition(new TSTransition(3,'c',4));
        ts.addTransition(new TSTransition(2,'a',2));

        ts.addAllAtomicPropositions(new String[]{"x","y","z"});


        ts.addToLabel(1,"x");
        ts.addToLabel(2,"y");
        ts.addToLabel(3,"z");

        return ts;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isActionDeterministic() {
        Assert.assertEquals(true,fvmFacade.isActionDeterministic(simple));
        Assert.assertEquals(true,fvmFacade.isActionDeterministic(new TransitionSystem<>()));
        simple.addInitialState(7);
        Assert.assertEquals(false,fvmFacade.isActionDeterministic(simple));
        Assert.assertEquals(false,fvmFacade.isActionDeterministic(notDeterministic));
    }

    @Test
    public void isAPDeterministic() {
        Assert.assertEquals(true,fvmFacade.isAPDeterministic(simple));
        Assert.assertEquals(true,fvmFacade.isAPDeterministic(new TransitionSystem<>()));
        simple.addInitialState(7);
        Assert.assertEquals(false,fvmFacade.isAPDeterministic(simple));
        Assert.assertEquals(false,fvmFacade.isAPDeterministic(notDeterministic));
    }

    @Test
    public void isExecution() {
        Assert.assertEquals(true,fvmFacade.isExecution(simple,simpleSq));
        Assert.assertEquals(false,fvmFacade.isExecution(simple,simpleSqNotInitial));
        List<Integer> States= new ArrayList<Integer>(Arrays.asList(1, 3)) ;
        List<Character> Actions= new ArrayList<Character>(Arrays.asList('b'));
        simpleSqNotInitial=new AlternatingSequence(States,Actions);
        Assert.assertEquals(false,fvmFacade.isExecution(simple,simpleSqNotInitial));
    }

    @Test
    public void isExecutionFragment() {
        boolean check=fvmFacade.isExecutionFragment(simple,simpleSq);
        Assert.assertTrue(check);
        simple.removeTransition(new TSTransition(3,'c',4));
        check=fvmFacade.isExecutionFragment(simple,simpleSq);
        Assert.assertFalse(check);
    }

    @Test
    public void isInitialExecutionFragment() {
        boolean check=fvmFacade.isExecutionFragment(simple,simpleSq);
        Assert.assertTrue(check);
        simple.removeTransition(new TSTransition(3,'c',4));
        check=fvmFacade.isExecutionFragment(simple,simpleSq);
        Assert.assertFalse(check);
        check=fvmFacade.isExecutionFragment(simple,simpleSqNotInitial);
        Assert.assertFalse(check);
    }

    @Test
    public void isMaximalExecutionFragment() {
        Assert.assertEquals(true,fvmFacade.isMaximalExecutionFragment(simple,simpleSq));
        List<Integer> States= new ArrayList<Integer>(Arrays.asList(1, 3)) ;
        List<Character> Actions= new ArrayList<Character>(Arrays.asList('b'));
        simpleSqNotInitial=new AlternatingSequence(States,Actions);
        Assert.assertEquals(false,fvmFacade.isMaximalExecutionFragment(simple,simpleSqNotInitial));

    }

    @Test
    public void isStateTerminal() {
        Assert.assertEquals(false,fvmFacade.isStateTerminal(simple,1));
        Assert.assertEquals(true,fvmFacade.isStateTerminal(simple,4));
        Assert.assertEquals(true,fvmFacade.isStateTerminal(simple,5));
    }

    @Test
    public void TEstPost_AllPostState_WithoutAction() {
        try{
            fvmFacade.post(simple,6);
            Assert.assertTrue(false);
        }catch (StateNotFoundException s){
            Set<Integer> posts=fvmFacade.post(simple,1);
            Assert.assertTrue(posts.contains(2) && posts.contains(3) && posts.size()==2);
            posts=fvmFacade.post(simple,2);
            Assert.assertTrue(posts.contains(2) &&  posts.size()==1);
            posts=fvmFacade.post(simple,5);
            Assert.assertTrue(posts.size()==0);
        }
    }

    @Test
    public void testPost_forGroupOfStates() {
        Set<Integer> c1 = new HashSet<>(Arrays.asList(1, 3));
        Set<Integer> c2 = new HashSet<>(Arrays.asList(5));
        Set<Integer> c3 = new HashSet<>(Arrays.asList(6));

        try{
            fvmFacade.post((TransitionSystem<Integer, ?, ?>) simple, c3);
            Assert.assertTrue("testPost_forGroupOfStates: State not contain in TS",false);
        }catch (StateNotFoundException s){
            Set<Integer> posts= fvmFacade.post((TransitionSystem<Integer, ?, ?>) simple, c1);
            Assert.assertTrue("testPost_forGroupOfStates: regular case fail",posts.contains(2) && posts.contains(3)&& posts.contains(4) && posts.size()==3);
           posts= fvmFacade.post((TransitionSystem<Integer, ?, ?>) simple, c2);
            Assert.assertTrue("testPost_forGroupOfStates: empty case fail",posts.size()==0);
        }
    }

    @Test
    public void testPost_withSingleActionAndSingleState() {
        try{
            fvmFacade.post(simple,6,'a');
            Assert.assertTrue(false);
        }catch (StateNotFoundException s){
            Set<Integer> posts=fvmFacade.post(simple,1, 'a');
            Assert.assertTrue(posts.contains(2) && !posts.contains(3) && posts.size()==1);
            posts=fvmFacade.post(simple,2,'a');
            Assert.assertTrue(posts.contains(2) &&  posts.size()==1);
            posts=fvmFacade.post(simple,5,'a');
            Assert.assertTrue(posts.size()==0);
        }
    }

    @Test
    public void testPost_withSingleActionAndGroupState() {
        Set<Integer> c1 = new HashSet<>(Arrays.asList(1, 3));
        Set<Integer> c2 = new HashSet<>(Arrays.asList(5));
        Set<Integer> c3 = new HashSet<>(Arrays.asList(6));

        try{
            fvmFacade.post((TransitionSystem<Integer, Character, ?>) simple, c3,'a');
            Assert.assertTrue("testPost_forGroupOfStates: State not contain in TS",false);
        }catch (StateNotFoundException s){
            Set<Integer> posts= fvmFacade.post((TransitionSystem<Integer, Character, ?>) simple, c1,'a');
            Assert.assertTrue("testPost_forGroupOfStates: regular case fail",posts.contains(2) && posts.size()==1);
            posts= fvmFacade.post((TransitionSystem<Integer, Character, ?>) simple, c2,'a');
            Assert.assertTrue("testPost_forGroupOfStates: empty case fail",posts.size()==0);
        }
    }

    @Test
    public void preAllState_WithoutAction() {
        try {
            fvmFacade.pre(simple, 6);
            Assert.assertTrue(false);
        } catch (StateNotFoundException s) {
            Set<Integer> pres = fvmFacade.pre(simple, 1);
            Assert.assertTrue(pres.size() == 0);
            pres = fvmFacade.pre(simple, 2);
            Assert.assertTrue(pres.contains(2) && pres.contains(1) && pres.size() == 2);
            pres = fvmFacade.pre(simple, 5);
            Assert.assertTrue(pres.size() == 0);
        }
    }

    @Test
    public void testPreforGroupOfStates() {
        Set<Integer> c1 = new HashSet<>(Arrays.asList(1, 3));
        Set<Integer> c2 = new HashSet<>(Arrays.asList(5));
        Set<Integer> c3 = new HashSet<>(Arrays.asList(6));

        try{
            fvmFacade.pre((TransitionSystem<Integer, ?, ?>) simple, c3);
            Assert.assertTrue("testPreforGroupOfStates: State not contain in TS",false);
        }catch (StateNotFoundException s){
            Set<Integer> pres= fvmFacade.pre((TransitionSystem<Integer, ?, ?>) simple, c1);
            Assert.assertTrue("testPreforGroupOfStates: regular case fail",pres.contains(1) && pres.size()==1);
            pres= fvmFacade.pre((TransitionSystem<Integer, ?, ?>) simple, c2);
            Assert.assertTrue("testPreforGroupOfStates: empty case fail",pres.size()==0);
        }
    }


    @Test
    public void testPre__withSingleActionAndSingleState() {
        try{
            fvmFacade.pre(simple,6,'a');
            Assert.assertTrue(false);
        }catch (StateNotFoundException s){
            Set<Integer> pres=fvmFacade.pre(simple,1, 'a');
            Assert.assertTrue( pres.size()==0);
            pres=fvmFacade.pre(simple,2,'a');
            Assert.assertTrue(pres.contains(2) && pres.contains(1) &&  pres.size()==2);
            pres=fvmFacade.pre(simple,5,'a');
            Assert.assertTrue(pres.size()==0);
        }
    }

    @Test
    public void testPre_withSingleActionAndGroupState() {
        Set<Integer> c1 = new HashSet<>(Arrays.asList(1, 3));
        Set<Integer> c2 = new HashSet<>(Arrays.asList(5));
        Set<Integer> c3 = new HashSet<>(Arrays.asList(6));

        try{
            fvmFacade.pre((TransitionSystem<Integer, Character, ?>) simple, c3,'a');
            Assert.assertTrue("testPre_withSingleActionAndGroupState: State not contain in TS",false);
        }catch (StateNotFoundException s){
            Set<Integer> pres= fvmFacade.pre((TransitionSystem<Integer, Character, ?>) simple, c1,'b');
            Assert.assertTrue("testPre_withSingleActionAndGroupState: regular case fail",pres.contains(1) && pres.size()==1);
            pres= fvmFacade.pre((TransitionSystem<Integer, Character, ?>) simple, c2,'a');
            Assert.assertTrue("testPre_withSingleActionAndGroupState: empty case fail",pres.size()==0);
        }
    }

    @Test
    public void reach() {
        Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2,3,4));
        Assert.assertEquals("reach- Case without Circuits",expected,fvmFacade.reach(simple));
        simple.addTransition(new TSTransition(2,'b',1)); //add circuit (1-> 2 ->1)
        simple.addTransition(new TSTransition(3,'a',2)); //add circuit (1-> 3-> 2-> 1)
        Assert.assertEquals("reach- Case without Circuits",expected,fvmFacade.reach(simple));
    }

    @Test
    public void interleave() {
    }

    @Test
    public void testInterleave_withHandshakeing() {
        TransitionSystem ts1=fvmFacade.interleave(interval_withHandshake1,interval_withHandshake2,new HashSet<>());
        HashSet<String> h=new HashSet<>();
        h.add("l");
        TransitionSystem ts2=fvmFacade.interleave(interval_withHandshake1,interval_withHandshake1,h);
        TransitionSystem ts3=fvmFacade.interleave(interval_withHandshake1,interval_withHandshake2,h);

    }

    @Test
    public void createProgramGraph() {
    }

    @Test
    public void testInterleave1() {
    }

    @Test
    public void transitionSystemFromCircuit() {
        TransitionSystem check=fvmFacade.transitionSystemFromCircuit(c);
        Assert.assertTrue(Math.pow(2,c.getInputPortNames().size())*Math.pow(2,c.getRegisterNames().size())==check.getStates().size());
    }

    @Test
    public void transitionSystemFromProgramGraph() {
        ActionDef act=new ActionDef() {
            @Override
            public boolean isMatchingAction(Object candidate) {
                if(candidate instanceof String){
                    return "x=x+1".equals(candidate);
                }
                return false;
            }

            @Override
            public Map<String, Object> effect(Map<String, Object> eval, Object action) {
                Map<String, Object> newEta=new HashMap<>();
                    for (Map.Entry<String,Object> var: eval.entrySet()) {
                        if(isMatchingAction(action) && var.getValue() instanceof Integer)
                            newEta.put(var.getKey(),((Integer)var.getValue())+1);
                        else
                            newEta.put(var.getKey(),((Integer)var.getValue()));
                    }
                    return newEta;
            }
        };
        ActionDef initAct1=new ActionDef() {
            @Override
            public boolean isMatchingAction(Object candidate) {
                if(candidate instanceof String){
                    return ((String) candidate).contains("x==1");
                }
                return false;
            }

            @Override
            public Map<String, Object> effect(Map<String, Object> eval, Object action) {
                Map<String, Object> newEta=new HashMap<>();
                    if(isMatchingAction(action) )
                        newEta.put("x",1);
                return newEta;
            }
        };
        ActionDef initAct2=new ActionDef() {
            @Override
            public boolean isMatchingAction(Object candidate) {
                if(candidate instanceof String){
                    return ((String) candidate).contains("x==2");
                }
                return false;
            }

            @Override
            public Map<String, Object> effect(Map<String, Object> eval, Object action) {
                Map<String, Object> newEta=new HashMap<>();
                    if(isMatchingAction(action) )
                        newEta.put("x",2);
                return newEta;
            }
        };
        Set<ActionDef> actDef=new HashSet<>(Arrays.asList(act,initAct1,initAct2));
        ConditionDef condInit=new ConditionDef() {
            @Override
            public boolean evaluate(Map<String, Object> eval, String condition) {
                if(eval.containsKey("x")&&(((Integer) eval.get("x"))==1 && ((Integer)eval.get("x"))==2))
                    return true;
                return false;
            }
        };
        ConditionDef condtrans=new ConditionDef() {
            @Override
            public boolean evaluate(Map<String, Object> eval, String condition) {
                return true;
            }
        };
        Set<ConditionDef> condDef=new HashSet<>(Arrays.asList(condInit,condtrans));

        TransitionSystem check=fvmFacade.transitionSystemFromProgramGraph(pg,actDef,condDef);
        Assert.assertTrue(2==check.getInitialStates().size());
        Assert.assertTrue(4==check.getStates().size());
        Assert.assertTrue(1==check.getActions().size());
        Assert.assertTrue(5==check.getAtomicPropositions().size());



    }

    @Test
    public void transitionSystemFromChannelSystem() {
    }

    @Test
    public void testTransitionSystemFromChannelSystem() {
    }

    @Test
    public void programGraphFromNanoPromela() {
    }

    @Test
    public void programGraphFromNanoPromelaString() {
    }

    @Test
    public void testProgramGraphFromNanoPromela() {
    }

    @Test
    public void product() {
    }

    @Test
    public void verifyAnOmegaRegularProperty() {
    }

    @Test
    public void LTL2NBA() {
    }

    @Test
    public void GNBA2NBA() {
    }
}